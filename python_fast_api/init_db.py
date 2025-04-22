from database import Base, engine
import database

# Create all tables
Base.metadata.create_all(bind=engine)

# Create a session
db = database.SessionLocal()

# Add some initial data
try:
    # Add English levels
    levels = [
        database.EnglishLevel(level_name="Beginner"),
        database.EnglishLevel(level_name="Intermediate"),
        database.EnglishLevel(level_name="Advanced")
    ]
    db.add_all(levels)
    db.commit()

    # Add categories
    categories = [
        database.Category(category_name="Vocabulary"),
        database.Category(category_name="Grammar"),
        database.Category(category_name="Speaking"),
        database.Category(category_name="Listening")
    ]
    db.add_all(categories)
    db.commit()

    # Add types
    types = [
        database.Type(type_name="Multiple Choice"),
        database.Type(type_name="Fill in the Blank"),
        database.Type(type_name="True/False")
    ]
    db.add_all(types)
    db.commit()

    # Add options
    options = [
        database.Option(option_text="A"),
        database.Option(option_text="B"),
        database.Option(option_text="C"),
        database.Option(option_text="D")
    ]
    db.add_all(options)
    db.commit()

except Exception as e:
    print(f"Error initializing database: {e}")
    db.rollback()
finally:
    db.close() 