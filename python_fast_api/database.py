from sqlalchemy import create_engine, Column, Integer, String, DateTime, ForeignKey, BIGINT
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship, sessionmaker
import os
from dotenv import load_dotenv

load_dotenv()

SQLALCHEMY_DATABASE_URL = f"mysql+pymysql://{os.getenv('DB_USER')}:{os.getenv('DB_PASSWORD')}@{os.getenv('DB_HOST')}:3306/{os.getenv('DB_NAME')}"

engine = create_engine(SQLALCHEMY_DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()

class User(Base):
    __tablename__ = "Users"

    user_id = Column(Integer, primary_key=True, index=True)
    username = Column(String(45))
    password = Column(String(64))
    email = Column(String(45), unique=True)
    user_reg_date = Column(DateTime)
    gender = Column(String(45))
    age = Column(Integer)
    edu_status = Column(String(45))
    prev_edu_year = Column(Integer)
    level_id = Column(Integer, ForeignKey("Eng_Levels.level_id"))

    questions = relationship("Question", back_populates="user")
    bookmarks = relationship("Bookmark", back_populates="user")

class Question(Base):
    __tablename__ = "Questions"

    ques_id = Column(Integer, primary_key=True, index=True)
    solve_date = Column(DateTime)
    question = Column(String(45), unique=True)
    given_ans = Column(String(45))
    true_ans = Column(String(45))
    res_time = Column(String(45))
    level_id = Column(Integer, ForeignKey("Eng_Levels.level_id"))
    type_id = Column(Integer, ForeignKey("Types.type_id"))
    op_id = Column(Integer, ForeignKey("Options.op_id"))
    category_id = Column(Integer, ForeignKey("Categories.category_id"))
    solved_by = Column(Integer, ForeignKey("Users.user_id"))

    user = relationship("User", back_populates="questions")

class Category(Base):
    __tablename__ = "Categories"

    category_id = Column(Integer, primary_key=True, index=True)
    category_name = Column(String(45))

class EnglishLevel(Base):
    __tablename__ = "Eng_Levels"

    level_id = Column(Integer, primary_key=True, index=True)
    level_name = Column(String(45))

class Type(Base):
    __tablename__ = "Types"

    type_id = Column(Integer, primary_key=True, index=True)
    type_name = Column(String(45))

class Option(Base):
    __tablename__ = "Options"

    op_id = Column(Integer, primary_key=True, index=True)
    option_text = Column(String(45))

class Word(Base):
    __tablename__ = "Words"

    word = Column(String(100), primary_key=True)
    eng_level = Column(String(45))
    count = Column(BIGINT)

class Bookmark(Base):
    __tablename__ = "Bookmarks"

    bm_id = Column(Integer, primary_key=True, index=True)
    word = Column(String(100))
    word_tr = Column(String(100))
    user_id = Column(Integer, ForeignKey("Users.user_id"))

    user = relationship("User", back_populates="bookmarks")

# Dependency
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close() 