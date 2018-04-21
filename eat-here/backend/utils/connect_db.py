from pymongo import MongoClient


# deal with db connection
def get_db_connection():
    client = MongoClient('mongodb://localhost:27017/')
    db = client['mydb']
    return db
