import os
import sys
os.environ['SPARK_HOME'] = "/Users/HaikuoLiu/Desktop/CloudComputing/spark/spark-1.6.1-bin-hadoop2.6"
sys.path.append("/Users/HaikuoLiu/Desktop/CloudComputing/spark/spark-1.6.1-bin-hadoop2.6/python")
sys.path.append("/Users/HaikuoLiu/Desktop/CloudComputing/spark/spark-1.6.1-bin-hadoop2.6/python/py4j-0.9-src.zip")


from pyspark import SparkConf, SparkContext
from pyspark.mllib.recommendation import Rating

sc = SparkContext('local')

import numpy as np
import json, time
from operator import add


# Load and parse the data
data = sc.textFile("/Users/HaikuoLiu/PycharmProjects/restaurants_recommendation/spark/data/reviews.jl")
ratings = data.map(lambda l: json.loads(l))\
    .map(lambda l: Rating(int(l[0]), int(l[1]), float(l[2]))).cache()
print "====================="
print ratings.count()
print "====================="

business_dict = sc.textFile("/Users/HaikuoLiu/PycharmProjects/restaurants_recommendation/spark/data/business.jl").map(lambda r: json.loads(r))\
                .map(lambda  r: (r[0], (r[1], r[2]))).collectAsMap()

user_dict = sc.textFile("/Users/HaikuoLiu/PycharmProjects/restaurants_recommendation/spark/data/user.jl").map(lambda r: json.loads(r))\
                .map(lambda  r: (r[0], r[1])).collectAsMap()

def loadModel():
    start_time = time.time()
    # Load vUser
    vUser = sc.pickleFile("/Users/HaikuoLiu/PycharmProjects/restaurants_recommendation/spark/target/final/vUser")
    # Load vBusiness
    vBusiness = sc.pickleFile("/Users/HaikuoLiu/PycharmProjects/restaurants_recommendation/spark/target/final/vBusiness")
    # Load Model
#     model = MatrixFactorizationModel.load(sc, save_dir+name+"/cfModel")
    end_time = time.time()
    print "Time Cost: %.2fs"%(end_time - start_time)
    return vUser, vBusiness

vUser, vBusiness = loadModel()

vUser.cache()
vBusiness.cache()

print "====================="
print vUser.count()
print vBusiness.count()
print "====================="

def getKNN(vUser, vu, k):
    return vUser.map(lambda r: (r[0], np.linalg.norm(vu-r[1]))).top(k, key=lambda r: -r[1])

def getUserFeature(vUser, uid):
    a = vUser.filter(lambda r: r[0] == uid)
    if a.isEmpty(): return None
    return np.array(a.first()[1])

def getProductFeature(vBusiness, bid):
    a = vBusiness.filter(lambda r: r[0] == bid)
    if a.isEmpty(): return None
    return np.array(a.first()[1])

def getRecommedProduct(vBusiness, vu, num=10):
    return map(
            lambda r: r[0],
            vBusiness.filter(lambda r: r[0] in business_dict)\
                .map(lambda r: (r[0], np.array(r[1]).dot(vu))).top(num, key=lambda r: r[1])
        )

def getTaste(uid):
    start_time = time.time()
    vu = getUserFeature(vUser, uid)
    res = getRecommedProduct(vBusiness, vu, 1000)
    recomd = map(lambda r: business_dict[r][0], res[:5])
    users = map(lambda r: user_dict[r[0]], getKNN(vUser, vu, 6)[1:] )
    taste = sc.parallelize(res).flatMap(lambda r: business_dict[r][1])\
        .map(lambda r: (r, 1)).reduceByKey(add).takeOrdered(50, key=lambda r: -r[1])
    end_time = time.time()
    print "Time Cost: %.2fs"%(end_time - start_time)
    res = {}
    res["recommend"] = recomd
    res["tag"] = taste
    res["uid"] = uid
    f = open('cache.jl', "a")
    f.write(json.dumps(res))
    f.write('\n')
    f.flush()
    f.close()
    return recomd, users, taste


# with open('cache.jl', "a") as f:
for i in range(1,200):
    print "Start Calculation On %d"%i
    getTaste(i)
    # r,u,t = getTaste(i)
    # f.write( json.dumps( [i, r, u, t] ) )
    # print r
    # f.write('\n')
    # f.flush()

# f.close()

# with open('cache.jl', "a") as f:
#     r,u,t = getTaste(101)
#     f.write( json.dumps( [101, r, u, t] ) )
#     f.write('\n')
#     f.flush()
# f.close()