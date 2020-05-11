'''
****************************************************************************************************
Author:     Jean-Christophe Owens + Sipeng Liang + Hongtao Cai
Team:       90% Asian
Product:    Orient
90Panda!
****************************************************************************************************

                    ,,,         ,,,
                  ;"   ^;     ;'   ",
                  ;    s$$$$$$$s     ;
                  ,  ss$$$$$$$$$$s  ,'
                  ;s$$$$$$$$$$$$$$$
                  $$$$$$$$$$$$$$$$$$
                 $$$$P""Y$$$Y""W$$$$$
                 $$$$  p"$$$"q  $$$$$
                 $$$$  .$$$$$.  $$$$
                  $$DcaU$$$$$$$$$$
                     "Y$$$"*"$$$Y"
                        "$b.$$"
                                                88
                                                88
                                                88
    8b,dPPYba,  ,adPPYYba, 8b,dPPYba,   ,adPPYb,88 ,adPPYYba,
    88P'    "8a ""     `Y8 88P'   `"8a a8"    `Y88 ""     `Y8
    88       d8 ,adPPPPP88 88       88 8b       88 ,adPPPPP88
    88b,   ,a8" 88,    ,88 88       88 "8a,   ,d88 88,    ,88
    88`YbbdP"'  `"8bbdP"Y8 88       88  `"8bbdP"Y8 `"8bbdP"Y8
    88
    88


****************************************************************************************************
@Mission:
Mission is to control orientation of camera with a mobile device (remote Control Viewing | VRRV | RVVR) over Wifi.
Connects to Wifi Network of both BeagleBone AND Mobile device
Emulate Cloud Architectura and modeled off UCB Wireless *Big Range campus with same Wifi*

****************************************************************************************************
'''



#********* Includes ***********************************************************************************
import socket
from time import sleep
# ---------GPIO --------------------------------------------------------------------------------------
import Adafruit_BBIO.PWM as PWM
# ---------Gstreamer Pipeline-------------------------------------------------------------------------
import sys
import subprocess as sp
import os
#********** Server Setup ******************************************************************************

TCP_IP = '192.168.0.8'                              #IMPORTANT, connect to IP app displays
TCP_PORT = 19132                                    #Minecraft port
BUFFER_SIZE = 65536
MESSAGE = "Successfully Connected....."              #Debug print statemet

print("TCP Initialized Beagle Bone... ")
print("Socket Complete....")
print(TCP_IP)
print(TCP_PORT)
print(BUFFER_SIZE)
print(MESSAGE)



#Connect to Java app
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((TCP_IP, TCP_PORT))
s.send(MESSAGE)

#********** Gpio Setup ********************************************************************************
#    - Select value BTWeen [0-10]
#Select Pin from (config-pin P9.21 pmw | config-pin P9_22 pmw)

servoPin="P9_21"   #Panning
servoPin_y ="P9_22"   #Tilting

#************ Gyro Data ********************************************************************************
#define DT 0.02         // [s/loop] loop period. 20ms
#define AA 0.97         // complementary filter constant
#define A_GAIN 0.0573    // [deg/LSB]
#define G_GAIN 0.070     // [deg/s/LSB]

# float loop = 0.02
# float A_GAIN = 0.070
# gyroXangle = 0.
# gyroYangle = 0.
#**************Reset State Motors ***********************************************************************

#PWM.start(servoPin,2,50)                      #Enable timer for PWM
#PWM.start(servoPin_y,2,50)
#dutyCycle = 1./18.* 90 + 2
#PWM.set_duty_cycle(servoPin,dutyCycle)
#print("Reset Mode: Pan")
#sleep(1)
#PWM.set_duty_cycle(servoPin_y,dutyCycle)
#print("Reset Mode: Tilting")



#******************************************************************************************************
#Gstreamer Pipeline Launching
TX_file = sp.Popen("/home/debian/scripts/runserverjpeg1.sh", shell = True)
print("Successful Run :D")



#***** Stream Gryo Data *******************************************************************************
#Conditional Statement
# - must cut up the values
# - store into arrays
# - compare strings
# - Make instructions for servo

data_log = []                                         #Create an array
# after getting the firstc data
while True:
    data = s.recv(BUFFER_SIZE)                    #Buffer Size, this can be changed
    if not data:
            break
    data = data.decode('utf-8')                   #decode
    #Change or comment out for X | Y | Z if needed
    data_log.append(data)                         #Save data to array for x values
    #       print "\n".join(data_log)

    #Save Data stream
    block_x = (data.split("="))
    block_y = (data.split("="))
    #        print(block_y)

    #For x-val Gyro
    for i in range(0, len(block_x), 3):
            block_x[i:i+3]
    x = (block_x[0])
    print(x)

    #For y-val Gyro
    for j in range(0,len(block_y), 3):
            block_y[j:j+3]
    y = (block_y[1])
    print(y)

#*************Rotation of Gyro*******************************************************************************************
#Processing the data
# #Convert Gyro raw to degrees per second
#     rate_gyr_x = (float) block_x[0] * G_GAIN;
#     rate_gyr_y = (float) block_y[1]  * G_GAIN;
#     # rate_gyr_z = (float) gyrRaw[2]  * G_GAIN;


# #Calculate the angles from the gyro
#     gyroXangle+=rate_gyr_x*DT;
#     gyroYangle+=rate_gyr_y*DT;
#     gyroZangle+=rate_gyr_z*DT;

#*************************************************************************************************************************
#Motors For Panning 0-9 | [0-4 Left -- 5 middle -- 6-9 right]
#Left - Down
    if (((block_x[0] >= '0.0') and (block_x[0] <= '0.02')) or  ((block_y[1] >= '0.0') and (block_y[1] <= '0.02'))):
        PWM.start(servoPin, 2, 50)                       #Enable timer for PWM_x
        PWM.start(servoPin_y, 2, 50)                     #Enable timer for PWM_y

        dutyCycle = 1./18.* 20  + 2
        PWM.set_duty_cycle(servoPin,dutyCycle)
        sleep(1)
        PWM.set_duty_cycle(servoPin_y,dutyCycle)
        sleep(1)

    if (((block_x[0] >= '0.02') and (block_x[0] <= '0.04')) or  ((block_y[1] >= '0.02') and (block_y[1] <= '0.04'))):
        PWM.start(servoPin, 2, 50)                       #Enable timer for PWM_x
        PWM.start(servoPin_y, 2, 50)                     #Enable timer for PWM_y

        dutyCycle = 1./18.* 35  + 2
        PWM.set_duty_cycle(servoPin,dutyCycle)
        sleep(1)
        PWM.set_duty_cycle(servoPin_y,dutyCycle)
        sleep(1)

    if (((block_x[0] >= '0.04') and (block_x[0] <= '0.06')) or  ((block_y[1] >= '0.04') and (block_y[1] <= '0.06'))):
        PWM.start(servoPin, 2, 50)                       #Enable timer for PWM_x
        PWM.start(servoPin_y, 2, 50)                     #Enable timer for PWM_y

        dutyCycle = 1./18.* 50  + 2
        PWM.set_duty_cycle(servoPin,dutyCycle)
        sleep(1)
        PWM.set_duty_cycle(servoPin_y,dutyCycle)
        sleep(1)

    if (((block_x[0] >= '0.06') and (block_x[0] <= '0.08')) or  ((block_y[1] >= '0.06') and (block_y[1] <= '0.08'))):
        PWM.start(servoPin, 2, 50)                       #Enable timer for PWM_x
        PWM.start(servoPin_y, 2, 50)                     #Enable timer for PWM_y

        dutyCycle = 1./18.* 65  + 2
        PWM.set_duty_cycle(servoPin,dutyCycle)
        sleep(1)
        PWM.set_duty_cycle(servoPin_y,dutyCycle)
        sleep(1)
    #***************************************************************************************************************************
    #Middle
    if (((block_x[0] >= '0.08') and (block_x[0] <= '0.1')) or  ((block_y[1] >= '0.08') and (block_y[1] <= '0.1'))):
        PWM.start(servoPin, 2, 50)                       #Enable timer for PWM_x
        PWM.start(servoPin_y, 2, 50)                     #Enable timer for PWM_y

        dutyCycle = 1./18.* 90  + 2
        PWM.set_duty_cycle(servoPin,dutyCycle)
        sleep(1)
        PWM.set_duty_cycle(servoPin_y,dutyCycle)
        sleep(1)
    #****************************************************************************************************************************
    #Right - Up
    if (((block_x[0] >= '0.1') and (block_x[0] <= '0.12')) or  ((block_y[1] >= '0.1') and (block_y[1] <= '0.12'))):
        PWM.start(servoPin, 2, 50)                       #Enable timer for PWM_x
        PWM.start(servoPin_y, 2, 50)                     #Enable timer for PWM_y

        dutyCycle = 1./18.* 110  + 2
        PWM.set_duty_cycle(servoPin,dutyCycle)
        sleep(1)
        PWM.set_duty_cycle(servoPin_y,dutyCycle)
        sleep(1)

    if (((block_x[0] >= '0.12') and (block_x[0] <= '0.14')) or  ((block_y[1] >= '0.12') and (block_y[1] <= '0.14'))):
        PWM.start(servoPin, 2, 50)                       #Enable timer for PWM_x
        PWM.start(servoPin_y, 2, 50)                     #Enable timer for PWM_y

        dutyCycle = 1./18.* 135  + 2
        PWM.set_duty_cycle(servoPin,dutyCycle)
        sleep(1)
        PWM.set_duty_cycle(servoPin_y,dutyCycle)
        sleep(1)

    if (((block_x[0] >= '0.14') and (block_x[0] <= '0.16')) or  ((block_y[1] >= '0.14') and (block_y[1] <= '0.16'))):
        PWM.start(servoPin, 2, 50)                       #Enable timer for PWM_x
        PWM.start(servoPin_y, 2, 50)                     #Enable timer for PWM_y

        dutyCycle = 1./18.* 150  + 2
        PWM.set_duty_cycle(servoPin,dutyCycle)
        sleep(1)
        PWM.set_duty_cycle(servoPin_y,dutyCycle)
        sleep(1)

    if (((block_x[0] >= '0.16') and (block_x[0] <= '0.18')) or  ((block_y[1] >= '0.16') and (block_y[1] <= '0.18'))):
        PWM.start(servoPin, 2, 50)                       #Enable timer for PWM_x
        PWM.start(servoPin_y, 2, 50)                     #Enable timer for PWM_y

        dutyCycle = 1./18.* 165  + 2
        PWM.set_duty_cycle(servoPin,dutyCycle)
        sleep(1)
        PWM.set_duty_cycle(servoPin_y,dutyCycle)
        sleep(1)


#*********************************** EXIT Loop************************************************************
s.close()

PWM.start(servoPin,2,50)                       #Enable timer for PWM
dutyCycle = 1./18.* 90  + 2
PWM.set_duty_cycle(servoPin,dutyCycle)
sleep(1)

PWM.start(servoPin_y,2,50)                       #Enable timer for PWM
dutyCycle = 1./18.* 90  + 2
PWM.set_duty_cycle(servoPin_y,dutyCycle)
sleep(1)
# *********************************** END ****************************************************************


