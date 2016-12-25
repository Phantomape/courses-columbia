import os
import sys
os.environ['SPARK_HOME'] = "/Users/HaikuoLiu/Desktop/CloudComputing/spark/spark-1.6.1-bin-hadoop2.6"
sys.path.append("/Users/HaikuoLiu/Desktop/CloudComputing/spark/spark-1.6.1-bin-hadoop2.6/python")
sys.path.append("/Users/HaikuoLiu/Desktop/CloudComputing/spark/spark-1.6.1-bin-hadoop2.6/python/py4j-0.9-src.zip")


from pyspark.mllib.recommendation import ALS, MatrixFactorizationModel, Rating
from pyspark.mllib.regression import LabeledPoint, LinearRegressionWithSGD, LinearRegressionModel
import numpy as np
import json, time
from pyspark import SparkConf, SparkContext
from pyspark.mllib.recommendation import Rating


sc = SparkContext('local')

filepath = "data/reviews.jl"
save_dir = "target/"
ALS_setting = {
    'rank': 20,
    'numIterations': 5
}

data = sc.textFile(filepath)
ratings = data.map(lambda l: json.loads(l))\
    .map(lambda l: Rating(int(l[0]), int(l[1]), float(l[2]))).cache()


def splitRatings(ratings, bound=1000):
    c = ratings.count()
    print("Number of Reviews: %d"%c)
    print("_________________________________")
    train_ratings = ratings.filter(lambda r: r.user >= bound)
    test_ratings = ratings.filter(lambda r: r.user < bound)
    tmp = train_ratings.count()
    print("Training Data: %6.2f%% %d"%(float(tmp)/c*100, tmp))
    tmp = test_ratings.count()
    print("    Test Data: %6.2f%% %d"%(float(tmp)/c*100, tmp))
    return train_ratings, test_ratings

# Build the recommendation model using Alternating Least Squares
def cf_als(ratings, ALS_setting):
    print("Training CF Model")
    print("_________________________________")
    start_time = time.time()
    model = ALS.train(ratings, ALS_setting['rank'], ALS_setting['numIterations'])
    end_time = time.time()
    print "Time Cost: %.2fs"%(end_time - start_time)
    return model


def saveModel(model, name):
    start_time = time.time()
    vBusiness = model.productFeatures()
    vBusiness.saveAsPickleFile(save_dir + name + "/vBusiness")
    vUser = model.userFeatures()
    vUser.saveAsPickleFile(save_dir + name + "/vUser")

    #     # Save and load model
    #     model.save(sc, save_dir+name+"/cfModel")

    end_time = time.time()
    print("Number of Business: %d" % vBusiness.count())
    print("Number of User: %d" % vUser.count())
    print "Time Cost: %.2fs" % (end_time - start_time)


def loadModel(name):
    start_time = time.time()
    # Load vUser
    vUser = sc.pickleFile(save_dir + name + "/vUser")
    # Load vBusiness
    vBusiness = sc.pickleFile(save_dir + name + "/vBusiness")
    # Load Model
    #     model = MatrixFactorizationModel.load(sc, save_dir+name+"/cfModel")
    end_time = time.time()
    print "Time Cost: %.2fs" % (end_time - start_time)
    return vUser, vBusiness

train_ratings, test_ratings = splitRatings(ratings)

trainModel = cf_als(train_ratings, ALS_setting)

saveModel(trainModel, "full")

model = cf_als(ratings, ALS_setting)

saveModel(model, "final")
