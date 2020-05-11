import Adafruit_BBIO.PWM as PWM
from time import sleep

servoPin="P9_22"
servoPin2="P9_21"

print(servoPin)
print(servoPin2)
PWM.start(servoPin,2,50)
PWM.start(servoPin2,2,50)

while(1):
        #desiredAngle=input("What Angle do You Want")
        dutyCycle=1./18.* 90 + 2
        PWM.set_duty_cycle(servoPin,dutyCycle)
        sleep(1)
        PWM.set_duty_cycle(servoPin2,dutyCycle)
        sleep(1)
        #PWM.cleanup()
        break


