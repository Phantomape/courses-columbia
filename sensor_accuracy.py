from gopigo import *
import time

min_dist = 3
dist = [5, 30, 40]

def autonomy():
    prevLoc = 0
    currLoc = 0
    initLoc = us_dist(15)
    while True:
        currLoc = us_dist(15)
        while currLoc > initLoc:
            currLoc = us_dist(15)
        print currLoc

        if currLoc == 3:
            stop()
            return

        for t in dist:
            if prevLoc > t and currLoc <= t:
                stop()
                time.sleep(5)

        fwd()
        stop()

        prevLoc = currLoc


stop()
enable_servo()
servo(90)
autonomy()

