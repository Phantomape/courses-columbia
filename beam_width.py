from gopigo import *

dist = []
idx = []

enable_servo()
for i in range(180):
    tmp = us_dist(15)
    if abs(tmp - dist[-1]) > 1:
        idx.append(i)
    dist.append(tmp)
    

