from gopigo import *
from random import randint

t = 0.0
while (t < 20):
	i = randint(0,4)
	if i == 0:
		fwd()
	elif i == 1:
		bwd()
	elif i == 2:
		left()
	elif i == 3:
		right()
	else:
		led_on(0)
		led_on(1)
		time.sleep(1)
		led_off(0)
		led_off(1)
		t += 1
		continue
	time.sleep(1)
	stop()
	time.sleep(.5)
	t += 1.5
