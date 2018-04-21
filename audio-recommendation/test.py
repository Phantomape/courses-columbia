__author__ = 'Daniel Chen'
import os
import sys
import numpy
# Path for spark source folder
#os.environ['SPARK_HOME']="/usr/local/bin/spark-1.3.1-bin-hadoop2.6"
# os.environ['SPARK_HOME'] = "C:\Spark\spark-2.0.2-bin-hadoop2.7\spark-2.0.2-bin-hadoop2.7"

os.environ['SPARK_HOME'] = "/Users/HaikuoLiu/Desktop/CloudComputing/spark/spark-1.6.1-bin-hadoop2.6"


# sys.path.append("C:\Spark\spark-2.0.2-bin-hadoop2.7\spark-2.0.2-bin-hadoop2.7\python")

sys.path.append("/Users/HaikuoLiu/Desktop/CloudComputing/spark/spark-1.6.1-bin-hadoop2.6/python")

# sys.path.append("C:\Spark\spark-2.0.2-bin-hadoop2.7\spark-2.0.2-bin-hadoop2.7\python\lib\py4j-0.8.2.1-src.zip")
sys.path.append("/Users/HaikuoLiu/Desktop/CloudComputing/spark/spark-1.6.1-bin-hadoop2.6/python/py4j-0.9-src.zip")

try:
    from pyspark import SparkContext
    from pyspark import SparkConf
    print ("Imported Spark Modules")
except ImportError as e:
    print ("Can not import Spark Modules", e)
    sys.exit(1)

logFile = "/Users/HaikuoLiu/PycharmProjects/AudioRecommendation/log.txt"  # Should be some file on your system

sc = SparkContext("local", "Audio Recommendation")
logData = sc.textFile(logFile).cache()
datasetPath = '/Users/HaikuoLiu/Desktop/CloudComputing/spark/profiledata_06-May-2005/user_artist_data.txt'
artistDataPath = '/Users/HaikuoLiu/Desktop/CloudComputing/spark/profiledata_06-May-2005/artist_data.txt'
artistAliasPath = '/Users/HaikuoLiu/Desktop/CloudComputing/spark/profiledata_06-May-2005/artist_alias.txt'
numPartitions = 2
rawDataRDD = sc.textFile(datasetPath, numPartitions)
rawDataRDD.cache()
print "Loaded AudioScrobbler dataset with {0} points".format(rawDataRDD.count())
head = rawDataRDD.take(10)
print head

weights = [.1, .9]
seed = 42
sample, someOtherJunk = rawDataRDD.randomSplit(weights, seed)
sample.cache()

print sample.map(lambda x: float(x.split()[0])).stats()
print sample.map(lambda x: float(x.split()[1])).stats()
rawArtistRDD = sc.textFile(artistDataPath)

def parseArtistIdNamePair(singlePair):
    splitPair = singlePair.rsplit('\t')
    # we should have two items in the list - id and name of the artist.
    if len(splitPair) != 2:
        #print singlePair
        return []
    else:
        try:
            return [(int(splitPair[0]), splitPair[1])]
        except:
            return []

artistByID = dict(rawArtistRDD.flatMap(lambda x: parseArtistIdNamePair(x)).collect())


def parseArtistAlias(alias):
    splitPair = alias.rsplit('\t')
    # we should have two items in the list - id and name of the artist.
    if len(splitPair) != 2:
        #print singlePair
        return []
    else:
        try:
            return [(int(splitPair[0]), int(splitPair[1]))]
        except:
            return []

print "Let's load artist aliases..."
rawAliasRDD = sc.textFile(artistAliasPath)
artistAlias = rawAliasRDD.flatMap(lambda x: parseArtistAlias(x)).collectAsMap()
print "Artist aliases loaded OK.."

print "Let's convert all artist IDs to canonical form using aliases!"


from pyspark.mllib import recommendation
from pyspark.mllib.recommendation import *
# Let's turn the artist aliases into a broadcast variable.
# That'll distribute it to worker nodes efficiently, so we save bandwidth.
artistAliasBroadcast = sc.broadcast(artistAlias)

def mapSingleObservation(x):
    userID, artistID, count = map(lambda lineItem: int(lineItem), x.split())
    finalArtistID = artistAliasBroadcast.value.get(artistID)
    if finalArtistID is None:
        finalArtistID = artistID
    return Rating(userID, finalArtistID, count)

trainData = sample.map(lambda x: mapSingleObservation(x))
trainData.cache()

# rank = 10
#   The number of latent factors in the model, or equivalently, the number of columns
#   k in the user-feature and product-feature matrices. In nontrivial cases, this
#   is also their rank.
# iterations = 5
#   The number of iterations that the factorization runs. More iterations take more
#   time but may produce a better factorization.
# lambda = 0.01
#   A standard overfitting parameter. Higher values resist overfitting, but values that
#   are too high hurt the factorization??s accuracy.
# alpha = 1.0
#   Controls the relative weight of observed versus unobserved user-product interactions
#   in the factorization.

print "We're finally ready to build the first model! Here goes..."
model = ALS.trainImplicit(trainData, 10, 5, 0.01)
print "Model construction finished..."

print "Let's see if this model makes sense..."
# testUserID = 2120603

testUserID = 2093760
# print "Let's grab all artists played by user {0}".format(testUserID)
#
# artistByIDBroadcast = sc.broadcast(artistByID)
#
# artistsForUser = (trainData
#                   .filter(lambda observation: observation.user == testUserID)
#                   .map(lambda observation: artistByIDBroadcast.value.get(observation.product))
#                   .collect())
# print artistsForUser

print "Now let's generate some recommendations and see if they make sense for the guy..."

# Got to use the model.call syntax here, because recommendProducts is not implemented
# In pySpark 1.3 :(
recommendationsForUser = \
    map(lambda observation: artistByID.get(observation.product), model.call("recommendProducts", testUserID, 10))

print recommendationsForUser

print "top ten recommendations for user " + str(testUserID) + ":"
for r in recommendationsForUser:
    print r

# # Now let's get done to evaluation. To do this right, we'll split the data into test and training sets, then
# # compare test data set AUC between models!
# def areaUnderCurve(positiveData, bAllItemIDs, predictFunction):
#     """Computes mean AUC metric given a labeled data set and a prediction function.
#     Adapted from the Scala version distributed with "Advanced Analytics with Spark".
#     Args:
#         positiveData: A labelled dataset to evaluate on, as a Spark RDD.
#         bAllItemIDs: All items' IDs as a broadcast variable.
#         predictFunction: A lambda function that maps an RDD of data points into an RDD of Ratings.
#     Returns:
#         The mean AUC.
#     """
#     # Take held-out data as the "positive", and map to tuples
#     positiveUserProducts = positiveData.map(lambda r: (r.user, r.product))
#     # Make predictions for each of them, including a numeric score, and gather by user
#     positivePredictions = predictFunction(positiveUserProducts).groupBy(lambda x: x.user)
#     # BinaryClassificationMetrics.areaUnderROC is not used here since there are really lots of
#     # small AUC problems, and it would be inefficient, when a direct computation is available.
#
#     # Create a set of "negative" products for each user. These are randomly chosen
#     # from among all of the other items, excluding those that are "positive" for the user.
#     negativeUserProducts = (positiveUserProducts
#                             .groupByKey()
#                             .mapPartitions(lambda userIDAndPosItemIDs: createNegativeProductSet(userIDAndPosItemIDs, bAllItemIDs))
#                             .flatMap(lambda x: x)) #  flatmap breaks the collections above down into one big set of tuples
#
#     negativePredictions = predictFunction(negativeUserProducts).groupBy(lambda x: x.user)
#     # join positive and negative by user
#     return (positivePredictions
#             .join(negativePredictions)
#             .values()
#             .map(lambda (positiveRatings, negativeRatings): computeAUC(positiveRatings, negativeRatings))
#             .mean())
#
# import random
#
# def createNegativeProductSet(userIDAndPosItemIDs, bAllItemIDs):
#     """Creates a random set of products that were predicted by a recommendation model,
#     but never actually liked/used by a particular user.
#     We can then use these products to verify that
#     our recommendation model consistently rates these negative products
#     lower than the ones liked by the user (based on test/CV data)
#     Args:
#        userIDAndPosItemIDs: A tuple of <UserID, ResultIterable>
#        bAllItemIDs: a broadcast variable containing IDs of all the products in the dataset.
#     Returns:
#         A collection of tuples of form <UserID, ItemID> with products recommended
#         to but not ever liked by users.
#     """
#     allItemIDs = bAllItemIDs.value
#     return map(lambda (userID, posItemIDs): getNegativeProductsForSingleUser(userID, posItemIDs, allItemIDs), userIDAndPosItemIDs)
#
# from array import array
#
# def getNegativeProductsForSingleUser(userID, posItemIDs, allItemIDs):
#     posItemIDSet = set(posItemIDs)
#     negative = array('i')
#     i = 0
#     # Keep about as many negative examples per user as positive.
#     # Duplicates are OK
#     while i < len(allItemIDs) and len(negative) < len(posItemIDSet):
#         itemID = allItemIDs[random.randint(0, len(allItemIDs) - 1)]
#         if itemID not in posItemIDSet:
#             negative.append(itemID)
#         i += 1
#     # Result is a collection of (user,negative-item) tuples
#     return map(lambda itemID: (userID, itemID), negative)
#
# def computeAUC(positiveRatings, negativeRatings):
#     # AUC may be viewed as the probability that a random positive item scores
#     # higher than a random negative one. Here the proportion of all positive-negative
#     # pairs that are correctly ranked is computed. The result is equal to the AUC metric.
#     correct = 0L
#     total = 0L
#     # for each pairing
#     for positive in positiveRatings:
#         for negative in negativeRatings:
#             # Count the correctly-ranked pairs
#             if positive.rating > negative.rating:
#                 correct += 1
#             total += 1
#     return float(correct) / total
#
# # OK, now that we've fleshed out that AUC function, let's get to the easy part.
# # Lets split the data into training and cross-validation sets!
# mappedSampleRDD = sample.map(lambda x: mapSingleObservation(x))
# trainData, cvData = mappedSampleRDD.randomSplit([0.9, 0.1])
# trainData.cache()
# cvData.cache()
#
# allItemIDs = mappedSampleRDD.map(lambda x: x.product).distinct().collect()
# bAllItemIDs = sc.broadcast(allItemIDs)
#
# model = ALS.trainImplicit(trainData, 10, 5, 0.01)
# auc = areaUnderCurve(cvData, bAllItemIDs, model.predictAll)
#
# # Now let's run grid search to optimize rank, lambda and alpha hyperparameters.
# # These values are just to get a sense of where we should be heading.
#
# def runGridSearch(rankRange, lambdaRange, alphaRange):
#     for r in rankRange:
#         for l in lambdaRange:
#             for a in alphaRange:
#                 model = ALS.trainImplicit(trainData, r, 10, l, -1, a)
#                 auc = areaUnderCurve(cvData, bAllItemIDs, model.predictAll)
#                 yield ((r, l, a), auc)
#
# evaluations = runGridSearch(rankRange=[10, 50], lambdaRange=[1.0, 0.0001], alphaRange=[1.0, 40.0])
#
# sorted(list(evaluations), key=lambda x: x[1])