import time
import random
from gopigo import *

distance = [5, 30, 60]
min_distance = 3

def autonomy():
    while True:
        servo(90)
        time.sleep(1)
        dist = us_dist(15)
        if dist > min_distance:
            print 'Forward is good!!!!!!'
            fwd()
            time.sleep(1)
            for d in distance:
                if dist == d:
                    print 'Current distance:' + dist
                    stop()
        else:
            stop()


stop()
enable_servo()
autonomy()