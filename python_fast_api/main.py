from fastapi import FastAPI, Depends, HTTPException, status, Request
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session
from datetime import datetime, timedelta
from typing import List
import os
from dotenv import load_dotenv
import logging
import random
from googletrans import Translator
from pydantic import BaseModel
import pandas as pd
import numpy as np
import tensorflow as tf
from sklearn.preprocessing import LabelEncoder, StandardScaler
import joblib

import database, schemas, auth
from database import User, Question, Category, EnglishLevel, Type, Option, Word

# Configure logging
logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

load_dotenv()

app = FastAPI()

# Configure CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # In production, replace with your app's domain
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Auth routes
@app.post("/token", response_model=schemas.Token)
async def login_for_access_token(form_data: schemas.Login, db: Session = Depends(database.get_db)):
    logger.debug(f"Login attempt for email: {form_data.email}")
    user = db.query(User).filter(User.email == form_data.email).first()
    if not user:
        logger.debug("User not found")
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect email or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    
    logger.debug(f"User found: {user.email}")
    logger.debug(f"Stored password hash: {user.password}")
    logger.debug(f"Input password hash: {auth.get_password_hash(form_data.password)}")
    
    if not auth.verify_password(form_data.password, user.password):
        logger.debug("Password verification failed")
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect email or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    
    logger.debug("Password verification successful")
    access_token_expires = timedelta(minutes=auth.ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = auth.create_access_token(
        data={"sub": user.email}, expires_delta=access_token_expires
    )
    return {"access_token": access_token, "token_type": "bearer"}

@app.post("/register", response_model=schemas.User)
async def register_user(request: Request, user: schemas.UserCreate, db: Session = Depends(database.get_db)):
    logger.debug(f"Received registration request: {user.dict()}")
    try:
        db_user = db.query(User).filter(User.email == user.email).first()
        if db_user:
            logger.debug(f"User with email {user.email} already exists")
            raise HTTPException(status_code=400, detail="Email already registered")
        
        hashed_password = auth.get_password_hash(user.password)
        logger.debug(f"Password hashed successfully")
        
        db_user = User(
            username=user.username,
            email=user.email,
            password=hashed_password,
            gender=user.gender,
            age=user.age,
            edu_status=user.edu_status,
            prev_edu_year=user.prev_edu_year,
            level_id=user.level_id,
            user_reg_date=datetime.utcnow()
        )
        logger.debug(f"Created user object: {db_user.__dict__}")
        
        db.add(db_user)
        db.commit()
        db.refresh(db_user)
        logger.debug(f"User registered successfully: {db_user.__dict__}")
        return db_user
    except Exception as e:
        logger.error(f"Error during registration: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

# Question routes
@app.get("/questions/", response_model=List[schemas.Question])
async def get_questions(
    skip: int = 0, 
    limit: int = 100,
    level_id: int = None,
    category_id: int = None,
    db: Session = Depends(database.get_db),
    current_user: User = Depends(auth.get_current_user)
):
    query = db.query(Question)
    if level_id:
        query = query.filter(Question.level_id == level_id)
    if category_id:
        query = query.filter(Question.category_id == category_id)
    return query.offset(skip).limit(limit).all()

@app.post("/questions/", response_model=schemas.Question)
async def create_question(
    question: schemas.QuestionCreate,
    db: Session = Depends(database.get_db),
    current_user: User = Depends(auth.get_current_user)
):
    db_question = Question(**question.dict(), solved_by=current_user.user_id)
    db.add(db_question)
    db.commit()
    db.refresh(db_question)
    return db_question

# Category routes
@app.get("/categories/", response_model=List[schemas.Category])
async def get_categories(
    skip: int = 0, 
    limit: int = 100,
    db: Session = Depends(database.get_db)
):
    return db.query(Category).offset(skip).limit(limit).all()

# English Level routes
@app.get("/levels/", response_model=List[schemas.EnglishLevel])
async def get_levels(
    skip: int = 0, 
    limit: int = 100,
    db: Session = Depends(database.get_db)
):
    return db.query(EnglishLevel).offset(skip).limit(limit).all()

# Type routes
@app.get("/types/", response_model=List[schemas.Type])
async def get_types(
    skip: int = 0, 
    limit: int = 100,
    db: Session = Depends(database.get_db)
):
    return db.query(Type).offset(skip).limit(limit).all()

# Option routes
@app.get("/options/", response_model=List[schemas.Option])
async def get_options(
    skip: int = 0, 
    limit: int = 100,
    db: Session = Depends(database.get_db)
):
    return db.query(Option).offset(skip).limit(limit).all()

# Word endpoints
@app.get("/words/random")
async def get_random_word(db: Session = Depends(database.get_db)):
    # Get all words from the database
    words = db.query(Word.word, Word.eng_level, Word.count).all()
    if not words:
        raise HTTPException(status_code=404, detail="No words found")
    
    # Select a random word
    random_word = random.choice(words)
    return {
        "word": random_word.word,
        "eng_level": random_word.eng_level,
        "count": random_word.count
    }

@app.get("/words/random/{count}")
async def get_random_words(count: int, db: Session = Depends(database.get_db)):
    # Get all words from the database
    words = db.query(Word.word, Word.eng_level, Word.count).all()
    if not words:
        raise HTTPException(status_code=404, detail="No words found")
    
    # Select random words
    if count > len(words):
        count = len(words)
    random_words = random.sample(words, count)
    
    return [
        {
            "word": word.word,
            "eng_level": word.eng_level,
            "count": word.count
        }
        for word in random_words
    ]

# Bookmark endpoints
@app.post("/bookmarks/", response_model=schemas.Bookmark)
async def create_bookmark(
    bookmark: schemas.BookmarkCreate,
    db: Session = Depends(database.get_db),
    current_user: User = Depends(auth.get_current_user)
):
    db_bookmark = database.Bookmark(
        word=bookmark.word,
        word_tr=bookmark.word_tr,
        user_id=current_user.user_id
    )
    db.add(db_bookmark)
    db.commit()
    db.refresh(db_bookmark)
    return db_bookmark

@app.get("/bookmarks/", response_model=List[schemas.Bookmark])
async def get_bookmarks(
    skip: int = 0,
    limit: int = 100,
    db: Session = Depends(database.get_db),
    current_user: User = Depends(auth.get_current_user)
):
    return db.query(database.Bookmark).filter(
        database.Bookmark.user_id == current_user.user_id
    ).offset(skip).limit(limit).all()

@app.delete("/bookmarks/{bookmark_id}")
async def delete_bookmark(
    bookmark_id: int,
    db: Session = Depends(database.get_db),
    current_user: User = Depends(auth.get_current_user)
):
    bookmark = db.query(database.Bookmark).filter(
        database.Bookmark.bm_id == bookmark_id,
        database.Bookmark.user_id == current_user.user_id
    ).first()
    
    if not bookmark:
        raise HTTPException(status_code=404, detail="Bookmark not found")
    
    db.delete(bookmark)
    db.commit()
    return {"message": "Bookmark deleted successfully"}

# Translation endpoint
@app.get("/translate")
async def translate_text(text: str, source: str = "en", target: str = "tr"):
    try:
        logger.debug(f"Translating text: {text} from {source} to {target}")
        translator = Translator()
        translation = translator.translate(text, src=source, dest=target)
        logger.debug(f"Translation result: {translation.text}")
        return {
            "original_text": text,
            "translated_text": translation.text,
            "source_language": source,
            "target_language": target
        }
    except Exception as e:
        logger.error(f"Translation error: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/user", response_model=schemas.User)
async def get_current_user(current_user: User = Depends(auth.get_current_user)):
    return current_user

# Load and preprocess the dataset
def load_and_preprocess_data():
    # Load the Excel file
    df = pd.read_excel('dataset/İngilizce Test (Yanıtlar).xlsx')
    
    # No categorical encoding needed, all columns are numeric
    # Split features and target
    X = df.drop('eng_level', axis=1)
    y = df['eng_level']
    
    # Scale the features
    scaler = StandardScaler()
    X_scaled = scaler.fit_transform(X)
    
    # Create label encoder for target
    label_encoder = LabelEncoder()
    y_encoded = label_encoder.fit_transform(y)
    
    return X_scaled, y_encoded, label_encoder, scaler

# Train the ANN model
def train_model(X, y):
    model = tf.keras.Sequential([
        tf.keras.layers.Dense(64, activation='relu', input_shape=(X.shape[1],)),
        tf.keras.layers.Dropout(0.2),
        tf.keras.layers.Dense(32, activation='relu'),
        tf.keras.layers.Dropout(0.2),
        tf.keras.layers.Dense(len(np.unique(y)), activation='softmax')
    ])
    
    model.compile(optimizer='adam',
                 loss='sparse_categorical_crossentropy',
                 metrics=['accuracy'])
    
    model.fit(X, y, epochs=50, batch_size=32, validation_split=0.2)
    
    return model

# Load or train the model
try:
    X, y, label_encoder, scaler = load_and_preprocess_data()
    model = train_model(X, y)
except Exception as e:
    print(f"Error loading/training model: {e}")
    model = None
    label_encoder = None
    scaler = None

class UserData(BaseModel):
    gender: int
    age: int
    edu_status: int
    prev_edu_ye: int
    q1: int
    q2: int
    q3: int
    q4: int
    q5: int
    q6: int
    q7: int
    q8: int
    q9: int
    q10: int

@app.post("/predict_cefr_level")
async def predict_cefr_level(user_data: UserData):
    logger.debug("Received CEFR prediction request with data:")
    logger.debug(f"Gender: {user_data.gender}")
    logger.debug(f"Age: {user_data.age}")
    logger.debug(f"Education Status: {user_data.edu_status}")
    logger.debug(f"Previous Education Years: {user_data.prev_edu_ye}")
    logger.debug(f"Test Answers: {[user_data.q1, user_data.q2, user_data.q3, user_data.q4, user_data.q5, user_data.q6, user_data.q7, user_data.q8, user_data.q9, user_data.q10]}")

    try:
        if model is None or scaler is None or label_encoder is None:
            raise HTTPException(status_code=500, detail="Model not initialized")
            
        # Prepare input data
        input_data = np.array([
            user_data.gender,
            user_data.age,
            user_data.edu_status,
            user_data.prev_edu_ye,
            user_data.q1,
            user_data.q2,
            user_data.q3,
            user_data.q4,
            user_data.q5,
            user_data.q6,
            user_data.q7,
            user_data.q8,
            user_data.q9,
            user_data.q10
        ]).reshape(1, -1)
        
        # Scale input data
        logger.debug("Scaling input data...")
        input_data_scaled = scaler.transform(input_data)
        
        # Make prediction
        logger.debug("Making prediction...")
        prediction = model.predict(input_data_scaled)
        
        # Get the index of the highest probability
        predicted_class = np.argmax(prediction, axis=1)
        logger.debug(f"Raw prediction: {prediction}")
        logger.debug(f"Predicted class index: {predicted_class}")
        
        # Convert prediction to CEFR level and convert to Python int
        predicted_level = int(label_encoder.inverse_transform(predicted_class)[0])
        logger.debug(f"Predicted CEFR level: {predicted_level}")
        
        return {"predicted_cefr_level": predicted_level}
    except Exception as e:
        logger.error(f"Error in CEFR prediction: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000) 