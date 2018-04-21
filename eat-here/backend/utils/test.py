# -*- coding: utf-8 -*-
# for userCollection:
from pymongo import MongoClient
client = MongoClient()
# client set up
db = client.test
userMongoData = db.userMongoData
businessMongoData1 = db.businessMongoData2

from bson.objectid import ObjectId
def getByUser_id(findId):
    try:
        document = userMongoData.find_one({"user_id":findId})
        return document
    except Exception:
        return "id incorrect"

def getByRestaurant_id(findId):
    try:
#         document = businessMongoData1.find({"item_id":{$regex:findId,$options:"$i"}})
        document = businessMongoData1.find({"item_id":{'$regex':findId}})
        return document
    except Exception:
        return "id incorrect"


def getByEmail(email):
    try:
        document = userMongoData.find_one({"email": email})
        return document
    except Exception:
        return "Email incorrect"
def insertData(data):
    # you should assign an id at first or MongDB will assign a random one for you.
    try:
        post_id = userMongoData.insert_one(data).inserted_id
        return post_id
    except Exception:
        return "insert fail"

def updateData(user_id, target,value):
    # target is the name of attribute and value is the assigned value
    update = userMongoData.find_one({"user_id": user_id})
    try:
        update[target] = value
        return "update correct"
    except Exception:
        return "update fail"



# s = getByUser_id("4U9kSBLuBDU391x6bxU-YA")
# print s


# print getByUser_id("18kPq7GPye-YQ3LyKyAZPw")
#
# print ""
#
# print getByEmail("2@gmail.com")



print businessMongoData1.count()
print getByRestaurant_id("6KON3CR5ZEM5RMYKLPCFCW")
