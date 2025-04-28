from pydantic import BaseModel, EmailStr, Field
from typing import Optional, List
from datetime import datetime

class Token(BaseModel):
    access_token: str
    token_type: str

class TokenData(BaseModel):
    email: Optional[str] = None

class Login(BaseModel):
    email: EmailStr
    password: str

class UserBase(BaseModel):
    email: EmailStr
    username: str = Field(..., min_length=3, max_length=45)
    gender: str = Field(..., pattern="^(male|female|other)$")
    age: int = Field(..., gt=0, lt=150)
    edu_status: str = Field()
    prev_edu_year: int = Field(..., gt=0)
    level_id: int = Field(..., gt=0)

class UserCreate(UserBase):
    password: str = Field(..., min_length=6, max_length=45)

class User(UserBase):
    user_id: int
    user_reg_date: datetime

    class Config:
        from_attributes = True

class QuestionBase(BaseModel):
    question: str = Field(..., min_length=1, max_length=45)
    true_ans: str = Field(..., min_length=1, max_length=45)
    level_id: int = Field(..., gt=0)
    type_id: int = Field(..., gt=0)
    op_id: int = Field(..., gt=0)
    category_id: int = Field(..., gt=0)

class QuestionCreate(QuestionBase):
    pass

class Question(QuestionBase):
    ques_id: int
    solve_date: Optional[datetime] = None
    given_ans: Optional[str] = None
    res_time: Optional[str] = None
    solved_by: Optional[int] = None

    class Config:
        from_attributes = True

class CategoryBase(BaseModel):
    category_name: str = Field(..., min_length=1, max_length=45)

class Category(CategoryBase):
    category_id: int

    class Config:
        from_attributes = True

class EnglishLevelBase(BaseModel):
    level_name: str = Field(..., min_length=1, max_length=45)

class EnglishLevel(EnglishLevelBase):
    level_id: int

    class Config:
        from_attributes = True

class TypeBase(BaseModel):
    type_name: str = Field(..., min_length=1, max_length=45)

class Type(TypeBase):
    type_id: int

    class Config:
        from_attributes = True

class OptionBase(BaseModel):
    option_text: str = Field(..., min_length=1, max_length=45)

class Option(OptionBase):
    op_id: int

    class Config:
        from_attributes = True

class Bookmark(BaseModel):
    bm_id: int
    word: str
    word_tr: str
    user_id: int

    class Config:
        orm_mode = True

class BookmarkCreate(BaseModel):
    word: str
    word_tr: str 