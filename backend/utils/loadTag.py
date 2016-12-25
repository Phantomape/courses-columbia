import json
from pymongo import MongoClient
client = MongoClient()
db = client.test
userTag = db.userTag
fin = open('/home/ec2-user/restaurants_recommendation/spark_data/userTag.json','r')
count = 0
for eachLine in fin:
    line = eachLine.strip().decode('utf-8')
    line = line.strip(',')
    count = count +1
    try:
        js = json.loads(line)
    except Exception, e:
        continue
    userTag.insert_one(js)
    if count>100:
        print 'processing'
        count = 0
print 'finished'
