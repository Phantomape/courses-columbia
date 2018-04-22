from pymongo import MongoClient
import datetime

client = MongoClient('mongodb://localhost:27017/')
db = client['mydb']
collection = db['recommend_db']

post = {"user_name": "xiaoming",
         "text": "My first blog post!",
        "tags": ["mongodb", "python", "pymongo"],
         "date": datetime.datetime.utcnow()}

collection = db['test-collection']
posts = db.user
post_id = posts.insert_one(post).inserted_id