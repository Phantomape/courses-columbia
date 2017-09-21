from gopigo import *
import time

enable_servo()
servo(30)
time.sleep(2)

flag = True
change = []
prevDeg = 0
prevDist = 0
for currDeg in range(30, 150):
    servo(currDeg)
    currDist = us_dist(15)
    print 'degree: ' + str(currDeg) + ', distance: ' + str(currDist)
    if flag is True:
        if prevDist - currDist > 50:
            change.append(currDeg)
            flag = False
    else:
        if currDist - prevDist > 50:
            change.append(prevDeg)
            flag = True

    prevDeg = currDeg
    prevDist = currDist

if len(change) < 2:
    print 'unsuccessful attempt'
else:
    print 'beam width: ' + str(change[1] - change[0])

