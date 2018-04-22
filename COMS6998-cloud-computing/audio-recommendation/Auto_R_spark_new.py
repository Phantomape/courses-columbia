import sys
import os
os.environ['SPARK_HOME'] = "/Users/HaikuoLiu/Desktop/CloudComputing/spark/spark-1.6.1-bin-hadoop2.6"
sys.path.append("/Users/HaikuoLiu/Desktop/CloudComputing/spark/spark-1.6.1-bin-hadoop2.6/python")
sys.path.append("/Users/HaikuoLiu/Desktop/CloudComputing/spark/spark-1.6.1-bin-hadoop2.6/python/py4j-0.9-src.zip")

from pyspark import *
from pyspark.mllib.recommendation import *

# result
"""
Jay-Z
50 Cent
2Pac
Snoop Dogg
Nas
Kanye West
Dr. Dre
Eminem
Outkast
Ludacris
"""

"""
Jay-Z
Snoop Dogg
50 Cent
2Pac
Nas
Kanye West
Dr. Dre
Outkast
Eminem
Ludacris
"""

"""
Jay-Z
50 Cent
Snoop Dogg
2Pac
Kanye West
Nas
Dr. Dre
Outkast
Eminem
Ludacris
"""

def quiet_logs( spark_context ):
  logger = spark_context._jvm.org.apache.log4j
  logger.LogManager.getLogger("org").setLevel(logger.Level.ERROR)
  logger.LogManager.getLogger("akka").setLevel(logger.Level.ERROR)

def parse_artist(p):
    separate_pair = p.rsplit('\t')
    # we should have two items in the list - id and name of the artist.
    if len(separate_pair) != 2:
        #print p
        return []
    else:
        try:
            return [(int(separate_pair[0]), separate_pair[1])]
        except:
            return []

def parse_A_A(alias):
    separate_pair = alias.rsplit('\t')
    # we should have two items in the list - id and name of the artist.
    if len(separate_pair) != 2:
        #print p
        return []
    else:
        try:
            return [(int(separate_pair[0]), int(separate_pair[1]))]
        except:
            return []

def map_one(x):
    uid, aid, num = map(lambda l: int(l), x.split())
    ret_aid = A_A_broadcast.value.get(aid)
    if ret_aid is None:
        ret_aid = aid
    return Rating(uid, ret_aid, num)

#   Begining
spark_context = SparkContext("local", "Audio Recommendation")
quiet_logs(spark_context)

print "Initialized successfully, loading the dataset..."


#   For erm

#   erm location: /home/hadoop/python_code/run.py

#   spark_context = SparkContext("local", "Haikuo Liu-Xucheng Chen-Audio Recommendation")
#   user_artist_data = 's3://aws-logs-160401747272-us-east-1/Auto_R/user_artist_data.txt'
#   artist_data = 's3://aws-logs-160401747272-us-east-1/Auto_R/artist_data.txt'
#   artist_alias = 's3://aws-logs-160401747272-us-east-1/Auto_R/artist_alias.txt'


#   For local test
user_artist_data = '/Users/HaikuoLiu/Desktop/CloudComputing/spark/profiledata_06-May-2005/user_artist_data.txt'
artist_data = '/Users/HaikuoLiu/Desktop/CloudComputing/spark/profiledata_06-May-2005/artist_data.txt'
artist_alias = '/Users/HaikuoLiu/Desktop/CloudComputing/spark/profiledata_06-May-2005/artist_alias.txt'

#   Load data
RDD_artist = spark_context.textFile(artist_data)
RDD_alias = spark_context.textFile(artist_alias)
RDD_data = spark_context.textFile(user_artist_data, 2)
RDD_data.cache()

id_of_artist = dict(RDD_artist.flatMap(lambda x: parse_artist(x)).collect())
alias_of_artist = RDD_alias.flatMap(lambda x: parse_A_A(x)).collectAsMap()

#   Broadcast variable
A_A_broadcast = spark_context.broadcast(alias_of_artist)

#   Prepare training data
trained_data = RDD_data.map(lambda x: map_one(x))
trained_data.cache()

# rank = 10
#   The number of latent factors in the model, or equivalently, the number of columns
#   k in the user-feature and product-feature matrices. In nontrivial cases, this
#   is also their rank.
# iterations = 5
#   The number of iterations that the factorization runs. More iterations take more
#   time but may produce a better factorization.
# lambda = 0.01
#   A standard overfitting parameter. Higher values resist overfitting, but values that
# alpha = 1.0
#   Controls the relative weight of observed versus unobserved user-product interactions
#   in the factorization.

#   Build models
model = ALS.trainImplicit(trained_data, 10, 5, 0.01)

uid_to_recommend = 2093760

#   In version of pySpark under 1.3, use call method
#recommendation_results = map(lambda res: id_of_artist.get(res.product), model.call("recommendProducts", uid_to_recommend, 10))
#   Latest version
recommendation_results = map(lambda res: id_of_artist.get(res.product), model.recommendProducts(uid_to_recommend, 10))

print "top ten recommendations for user " + str(uid_to_recommend) + ":"
for r in recommendation_results:
    print r
