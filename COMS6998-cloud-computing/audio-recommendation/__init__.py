__author__ = 'Daniel Chen'
import os
import sys
import numpy
# Path for spark source folder
#os.environ['SPARK_HOME']="/usr/local/bin/spark-1.3.1-bin-hadoop2.6"
os.environ['SPARK_HOME'] = "C:\Spark\spark-2.0.2-bin-hadoop2.7\spark-2.0.2-bin-hadoop2.7"
print os.environ.keys()
# Append pyspark  to Python Path
sys.path.append("C:\Spark\spark-2.0.2-bin-hadoop2.7\spark-2.0.2-bin-hadoop2.7\python")
sys.path.append("C:\Spark\spark-2.0.2-bin-hadoop2.7\spark-2.0.2-bin-hadoop2.7\python\lib\py4j-0.8.2.1-src.zip")
print sys.path
try:
    from pyspark import SparkContext
    from pyspark import SparkConf
    print ("Imported Spark Modules")
except ImportError as e:
    print ("Can not import Spark Modules", e)
    sys.exit(1)