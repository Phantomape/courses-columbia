import os
import sys
os.environ['SPARK_HOME'] = "/Users/HaikuoLiu/Desktop/CloudComputing/spark/spark-1.6.1-bin-hadoop2.6"
sys.path.append("/Users/HaikuoLiu/Desktop/CloudComputing/spark/spark-1.6.1-bin-hadoop2.6/python")
sys.path.append("/Users/HaikuoLiu/Desktop/CloudComputing/spark/spark-1.6.1-bin-hadoop2.6/python/py4j-0.9-src.zip")


from pyspark.mllib.recommendation import ALS, MatrixFactorizationModel, Rating
import json
from pyspark import SparkConf, SparkContext
from pyspark.mllib.recommendation import Rating


spark_context = SparkContext('local')

path = "spark_data/reviews.jl"
save_dir = "target/"
settings = {'r': 20, 'iter': 3}

spark_data = spark_context.textFile(path)
ratings = spark_data.map(lambda l: json.loads(l))\
    .map(lambda l: Rating(int(l[0]), int(l[1]), float(l[2]))).cache()


def separate_rating(ratings, bound=1000):
    train_ratings = ratings.filter(lambda r: r.user >= bound)
    test_ratings = ratings.filter(lambda r: r.user < bound)
    return train_ratings, test_ratings


def cf_als(ratings, settings):
    return ALS.train(ratings, settings['r'], settings['iter'])


def save_model(model, name):
    v_busi = model.productFeatures()
    v_busi.saveAsPickleFile(save_dir + name + "/v_busi")
    v_user = model.userFeatures()
    v_user.saveAsPickleFile(save_dir + name + "/v_user")


def get_model(name):
    v_user = spark_context.pickleFile(save_dir + name + "/v_user")
    v_busi = spark_context.pickleFile(save_dir + name + "/v_busi")
    return v_user, v_busi

train_ratings, test_ratings = separate_rating(ratings)
trained_model = cf_als(train_ratings, settings)
save_model(trained_model, "all")
m = cf_als(ratings, settings)
save_model(m, "final_model")
