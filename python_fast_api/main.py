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

# Translation endpoint
@app.get("/translate")
async def translate_text(text: str, source: str = "en", target: str = "tr"):
    try:
        translator = Translator()
        translation = translator.translate(text, src=source, dest=target)
        return {
            "original_text": text,
            "translated_text": translation.text,
            "source_language": source,
            "target_language": target
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000) 