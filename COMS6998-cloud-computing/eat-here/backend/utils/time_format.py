import time
import datetime


def date_to_timestamp(d):
    return int(time.mktime(d.timetuple())) * 1000


def timestamp_to_datetime(t):
    return datetime.datetime.fromtimestamp(int(t)).strftime('%Y-%m-%d %H:%M:%S')
