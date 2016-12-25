from pymongo import MongoClient
client = MongoClient()
# client set up
db = client.test
userMongoData = db.perUserMongoData2
print userMongoData.count()
email = "2@gmail.com"
document = userMongoData.find_one({"email": email})
print document
