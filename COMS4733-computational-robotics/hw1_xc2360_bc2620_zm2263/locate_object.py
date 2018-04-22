from gopigo import *
import time

# Find Hole Program from lecture notes

DPR = 360.0/64
INF = 200
LIMIT = 100
WHEEL_RAD = 3.25
ERROR = 1.3

def left_deg(deg=None):
    if deg is not None:
        pulse = int(deg/DPR)
        if pulse == 0:
            pulse = 1
        enc_tgt(0, 1, pulse)
    left()

def fwd_cm(dist=None):
    if dist is not None:
	pulse = int(cm2pulse(dist))
	enc_tgt(1,1,pulse)
    fwd()

def cm2pulse(dist):
    wheel_circ = 2*math.pi*WHEEL_RAD
    revs = dist/wheel_circ
    PPR = 18
    pulses = PPR*revs
    return pulses

servo(90)

tmp = INF

dist = []
for i in range(0, 9):
    d = us_dist(15)
    if d > INF:
        d = INF
    print 'dist:' + str(d) + ' after turning ' + str(i * 45) + ' degrees'
    dist.append(d)
    left_deg(45)
    time.sleep(1)
    

ang = 0
for i in range(len(dist)):
    if dist[i] < tmp:
        ang = i * 45
        tmp = dist[i]

print 'turn angle:' + str(ang)
time.sleep(1)
left_deg(ang)
time.sleep(4)

if (tmp >= 20 * ERROR):
   fwd_cm(tmp - 20 * ERROR)
   time.sleep(4)	

stop()

