# for userCollection:
# from pymongo import MongoClient
# client = MongoClient()
# client set up
# db = client.database_name
# userCollection = db.userCollection
from bson.objectid import ObjectId
def getById(id):
    # the userColllection like the table which store the data of user
    try:
        document = userCollection.find_one({'_id': ObjectId(id)})
        return document
    except Exception:
        return "id incorrect"
def getByEmail(email):
    # the userColllection like the table which store the data of user
    try:
        document = userCollection.find_one({'email': email})
        return document
    except Exception:
        return "Email incorrect"
def insertData(data):
    # you should assign an id at first or MongDB will assign a random one for you.
    try:
        post_id = userCollection.insert_one(data).inserted_id
        return post_id
    except Exception:
        return "insert fail"

def updateData(id, target,value):
    # target is the name of attribute and value is the assigned value
    update = userCollection.find_one({'_id': ObjectId(id)})
    try:
        update[target] = value
        return "update correct"
    except Exception:
        return "update fail"
