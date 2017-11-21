from __future__ import division
from gopigo import *

en_debug=1
DPR = 360.0/64
WHEEL_RAD = 3.2     #True radius of the wheel
ratio = 6/64        

def left_deg(deg=None):
    if deg:
        pulse = int(ratio*deg)
        if pulse>0:
            enc_tgt(1,1,pulse)
            left_rot()

def right_deg(deg=None):
    if deg:
        pulse = int(ratio*deg)
        if pulse>0:
            enc_tgt(1,1,pulse)
            right_rot()

def cm2pulse(dist):
    wheel_circ = 2*math.pi*WHEEL_RAD
    revs = dist/wheel_circ
    PPR = 18
    pulses = PPR*revs
    return pulses

def fwd_cm(dist):
    if dist:
        pulse = int(cm2pulse(dist))
        enc_tgt(1,1,pulse)
        if dist>0:
            fwd()

def bwd_cm(dist):
    if dist:
        pulse = int(cm2pulse(dist))
        enc_tgt(1,1,pulse)
        if dist>0:
            bwd()

if __name__ == "__main__":
    left_deg(360)
