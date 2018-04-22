import os
import sys
os.environ['SPARK_HOME'] = "/Users/HaikuoLiu/Desktop/CloudComputing/spark/spark-1.6.1-bin-hadoop2.6"
sys.path.append("/Users/HaikuoLiu/Desktop/CloudComputing/spark/spark-1.6.1-bin-hadoop2.6/python")
sys.path.append("/Users/HaikuoLiu/Desktop/CloudComputing/spark/spark-1.6.1-bin-hadoop2.6/python/py4j-0.9-src.zip")

from pyspark import SparkConf, SparkContext
from pyspark.mllib.recommendation import Rating
from operator import add
import numpy as np
import json


spark_context = SparkContext('local')

spark_data = spark_context.textFile("/Users/HaikuoLiu/PycharmProjects/restaurants_recommendation/spark/spark_data/reviews.jl")
r = spark_data.map(lambda l: json.loads(l))\
    .map(lambda l: Rating(int(l[0]), int(l[1]), float(l[2]))).cache()

b_dict = spark_context.textFile("/Users/HaikuoLiu/PycharmProjects/restaurants_recommendation/spark/spark_data/business.jl").map(lambda r: json.loads(r))\
                .map(lambda  r: (r[0], (r[1], r[2]))).collectAsMap()

u_dict = spark_context.textFile("/Users/HaikuoLiu/PycharmProjects/restaurants_recommendation/spark/spark_data/user.jl").map(lambda r: json.loads(r))\
                .map(lambda  r: (r[0], r[1])).collectAsMap()

def get_model():
    ve_user = spark_context.pickleFile("/Users/HaikuoLiu/PycharmProjects/restaurants_recommendation/spark/target/final_model/ve_user")
    v_busi = spark_context.pickleFile("/Users/HaikuoLiu/PycharmProjects/restaurants_recommendation/spark/target/final_model/v_busi")
    return ve_user, v_busi

ve_user, v_busi = get_model()
v_busi.cache()
ve_user.cache()


def knn_alg(ve_user, vu, k):
    return ve_user.map(lambda r: (r[0], np.linalg.norm(vu-r[1]))).top(k, key=lambda r: -r[1])


def get_u_features(ve_user, user_id):
    a = ve_user.filter(lambda r: r[0] == user_id)
    if a.isEmpty(): return None
    return np.array(a.first()[1])


def get_features(v_busi, bid):
    a = v_busi.filter(lambda r: r[0] == bid)
    if a.isEmpty(): return None
    return np.array(a.first()[1])


def get_product(v_busi, vu, num=10):
    return map(
            lambda r: r[0],
            v_busi.filter(lambda r: r[0] in b_dict)\
                .map(lambda r: (r[0], np.array(r[1]).dot(vu))).top(num, key=lambda r: r[1])
        )


def get_tags(user_id):
    v_user = get_u_features(ve_user, user_id)
    res = get_product(v_busi, v_user, 1000)
    recommendation = map(lambda r: b_dict[r][0], res[:5])
    tag = spark_context.parallelize(res).flatMap(lambda r: b_dict[r][1])\
        .map(lambda r: (r, 1)).reduceByKey(add).takeOrdered(50, key=lambda r: -r[1])
    res = {}
    res["recommend"] = recommendation
    res["tag"] = tag
    res["uid"] = user_id
    file = open('cache.jl', "a")
    file.write(json.dumps(res))
    file.write('\n')
    file.flush()
    file.close()

if __name__ == '__main__':
    for i in range(1,200):
        get_tags(i)
