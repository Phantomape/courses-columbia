import os
import sys
os.environ['SPARK_HOME'] = "/Users/HaikuoLiu/Desktop/CloudComputing/spark/spark-1.6.1-bin-hadoop2.6"
sys.path.append("/Users/HaikuoLiu/Desktop/CloudComputing/spark/spark-1.6.1-bin-hadoop2.6/python")
sys.path.append("/Users/HaikuoLiu/Desktop/CloudComputing/spark/spark-1.6.1-bin-hadoop2.6/python/py4j-0.9-src.zip")

from pyspark import SparkContext
from pyspark.mllib.recommendation import *


# Red Hot Chili Peppers
# U2
# Coldplay
# Queen
# The Beatles
# Led Zeppelin
# David Bowie
# [unknown]
# Radiohead
# Pink Floyd


# For erm

# erm location: /home/hadoop/python_code/run.py

# spark_context = SparkContext("local", "Haikuo Liu-Xucheng Chen-Audio Recommendation")
# user_artist_data = 's3://aws-logs-160401747272-us-east-1/Auto_R/user_artist_data.txt'
# artist_data = 's3://aws-logs-160401747272-us-east-1/Auto_R/artist_data.txt'
# artist_alias = 's3://aws-logs-160401747272-us-east-1/Auto_R/artist_alias.txt'

# For local test
spark_context = SparkContext("local", "Audio Recommendation")
user_artist_data = '/Users/HaikuoLiu/Desktop/CloudComputing/spark/profiledata_06-May-2005/user_artist_data.txt'
artist_data = '/Users/HaikuoLiu/Desktop/CloudComputing/spark/profiledata_06-May-2005/artist_data.txt'
artist_alias = '/Users/HaikuoLiu/Desktop/CloudComputing/spark/profiledata_06-May-2005/artist_alias.txt'

partit_num = 2
RDD_raw_data = spark_context.textFile(user_artist_data, partit_num)
RDD_raw_data.cache()

weights = [0.1, 0.9]
seed = 20
sample, someOtherJunk = RDD_raw_data.randomSplit(weights, seed)
sample.cache()


print "hello!"

RDD_raw_artist = spark_context.textFile(artist_data)

def get_artist_id_name_pair(p):
    split_p = p.rsplit('\t')
    if len(split_p) != 2:
        return []
    else:
        try:
            return [(int(split_p[0]), split_p[1])]
        except:
            return []

id_of_artist = dict(RDD_raw_artist.flatMap(lambda x: get_artist_id_name_pair(x)).collect())


def parsealias_of_artist(alias):
    split_p = alias.rsplit('\t')
    if len(split_p) != 2:
        return []
    else:
        try:
            return [(int(split_p[0]), int(split_p[1]))]
        except:
            return []

RDD_raw_alias = spark_context.textFile(artist_alias)
alias_of_artist = RDD_raw_alias.flatMap(lambda x: parsealias_of_artist(x)).collectAsMap()

artist_alias_broadcasted = spark_context.broadcast(alias_of_artist)

def map_one(x):
    m_uid, m_aid, count = map(lambda lineItem: int(lineItem), x.split())
    m_result_aid = artist_alias_broadcasted.value.get(m_aid)
    if m_result_aid is None:
        m_result_aid = m_aid
    return Rating(m_uid, m_result_aid, count)

trained_data = sample.map(lambda x: map_one(x))
trained_data.cache()

model = ALS.trainImplicit(trained_data, 10, 5, 0.01)

uid_to_recommend = 2093760

id_of_artistBroadcast = spark_context.broadcast(id_of_artist)

artists_user = (trained_data
                  .filter(lambda observation: observation.user == uid_to_recommend)
                  .map(lambda observation: id_of_artistBroadcast.value.get(observation.product))
                  .collect())

# Got to use the model.call syntax here, because recommendProducts is not implemented
# In pySpark 1.3 :(
recommendation_results = \
    map(lambda observation: id_of_artist.get(observation.product), model.call("recommendProducts", uid_to_recommend, 10))

print "finished!"

print recommendation_results

print "top ten recommendations for user " + str(uid_to_recommend) + ":"
for r in recommendation_results:
    print r

