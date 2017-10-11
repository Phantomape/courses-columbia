from gopigo import *

en_debug=1

## 360 roation is ~64 encoder pulses or 5 deg/pulse
## DPR is the Deg:Pulse Ratio or the # of degrees per
##  encoder pulse.
DPR = 360.0/64
WHEEL_RAD = 3.25 # Wheels are ~6.5 cm diameter. 
CHASS_WID = 13.5 # Chassis is ~13.5 cm wide.

def left_deg(deg=None):
    '''
    Turn chassis left by a specified number of degrees.
    DPR is the #deg/pulse (Deg:Pulse ratio)
    This function sets the encoder to the correct number
     of pulses and then invokes left().
    '''
    if deg is not None:
        pulse= int(deg/DPR)
        enc_tgt(0,1,pulse)
    left()

def right_deg(deg=None):
    '''
    Turn chassis right by a specified number of degrees.
    DPR is the #deg/pulse (Deg:Pulse ratio)
    This function sets the encoder to the correct number
     of pulses and then invokes right().
    '''
    if deg is not None:
        pulse= int(deg/DPR)
        enc_tgt(0,1,pulse)
    right()

def fwd_cm(dist=None):
    '''
    Move chassis fwd by a specified number of cm.
    This function sets the encoder to the correct number
     of pulses and then invokes fwd().
    '''
    if dist is not None:
        pulse = int(cm2pulse(dist))
        enc_tgt(1,1,pulse)
    fwd()

def bwd_cm(dist=None):
    '''
    Move chassis bwd by a specified number of cm.
    This function sets the encoder to the correct number
     of pulses and then invokes bwd().
    '''
    if dist is not None:
        pulse = int(cm2pulse(dist))
        enc_tgt(1,1,pulse)
    bwd()

def cm2pulse(dist):
    '''
    Calculate the number of pulses to move the chassis dist cm.
    pulses = dist * [pulses/revolution]/[dist/revolution]
    '''
    wheel_circ = 2*math.pi*WHEEL_RAD # [cm/rev] cm traveled per revolution of wheel
    revs = dist/wheel_circ
    PPR = 18 # [p/rev] encoder Pulses Per wheel Revolution
    pulses = PPR*revs # [p] encoder pulses required to move dist cm.
    if en_debug:
        print 'WHEEL_RAD',WHEEL_RAD
        print 'revs',revs
        print 'pulses',pulses
    return pulses

class Bug():
    def __init__(self):
        self.offset_x = 0
        self.offset_y = 0
        self.curr_x = 0
        self.curr_y = 0
        self.tgt_dist = 10   #   As in 10 cm
        self.mode = 'LINE'
        self.trajectory_x = [0]
        self.trajectory_y = [0]
        self.orientation = [0, 1, 2, 3] #   fwd, left, bwd, right
        self.dir_x = [0, -1, 0, 1]
        self.dir_y = [1, 0, -1, 0]
        self.curr_ori = 0
        self.wall = 'RIGHT'

    def search(self):
        while self.mode != 'REACH':
            if self.mode is 'LINE':
                dist = us_dist(15)
                if dist < 10:
                    self.mode = 'OBS'
                else:
                    fwd_cm(10)
                    self.curr_y = self.curr_y + 1

                if self.curr_x == 0 and self.curr_y == self.tgt_dist:
                    self.mode = 'REACH'

            elif self.mode is 'OBS':
                flag_fwd = self.test_fwd()
                flag_left = self.test_left()
                flag_right = self.test_right()

                if self.wall is 'RIGHT':
                    if flag_right is True:
                        right_deg(90)
                        self.curr_ori = (self.curr_ori + 3) % 4
                        fwd_cm(10)
                        self.curr_x = self.curr_x + self.dir_x[self.curr_ori]
                        self.curr_y = self.curr_y + self.dir_y[self.curr_ori]
                    elif flag_fwd is True:
                        fwd_cm(10)
                    elif flag_left is True:
                        left_deg(90)
                        self.curr_ori = (self.curr_ori + 1) % 4
                        fwd_cm(10)

                if flag_left is True:
                    self.wall = 'RIGHT'
                    left_deg(90)
                    self.curr_ori = (self.curr_ori + 1) % 4
                elif flag_right is True:
                    self.wall = 'LEFT'
                    right_deg(90)
                    self.curr_ori = (self.curr_ori + 3) % 4

    def test_left(self):
        left_deg(90)
        d = us_dist(15)
        right_deg(90)
        if d < 10:
            return False
        return True


if __name__ == '__main__':
    enable_servo()
    servo(90)
    bot = Bug()
    bot.search()