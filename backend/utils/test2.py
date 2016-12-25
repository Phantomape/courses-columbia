from pymongo import MongoClient
client = MongoClient()
db = client.test
userTag = db.userTag
for turple in userTag.find():
  print turple
  break
 
