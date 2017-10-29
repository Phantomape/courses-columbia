from gopigo import *

DPR = 360.0/64
WHEEL_RAD = 3.2
ratio = 6/64


def left_deg(deg=None):
    if deg is not None:
        pulse = int(deg/DPR)
        if pulse == 0:
            pulse = 1
        enc_tgt(0, 1, pulse)
    left()


def right_deg(deg=None):
    if deg is not None:
        pulse = int(deg/DPR)
        if pulse == 0:
            pulse = 1
        enc_tgt(1, 0, pulse)
    right()


def fwd_cm(dist=None):
    if dist is not None:
        pulse = int(cm2pulse(dist))
        enc_tgt(1,1,pulse)
    fwd()


def bwd_cm(dist=None):
    if dist is not None:
        pulse = int(cm2pulse(dist))
        enc_tgt(1,1,pulse)
    bwd()


def cm2pulse(dist):
    wheel_circ = 2*math.pi*WHEEL_RAD
    revs = dist/wheel_circ
    PPR = 18
    pulses = PPR*revs
    return pulses
