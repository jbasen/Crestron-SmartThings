# Crestron-SmartThings
This code was originally written by Sytanek.  However, changes made by SmartThings caused issues with the initial release.
Sytanek didn't have time to support the code so I made the corrections required to get it working.  The code currently has
working support for switches, dimmers, and presence sensors.  The code to support locks and thermostats was partially written.  Unfortunately, I don't have either a compatible thermostat or lock to complete this code so I'm putting everything I have up on Github to allow others to continue the work done by Sytanek and myself.  

Basic Steps to Install the SmartThings Crestron API and Get it Working

1)	Add your devices to your SmartThings hub

2)	Add a port forward to your router for the SmartThings Interface and point it to your processor

3)	Go to the SmartThings Web Site (www.smartthings.com)

4)	Scroll to the bottom of the page and click on "Developers"

5)	Login using your SmartThings username and password

6)	Validate that your location and hub are shown

7)	Click on My SmartApps and add a new smartapp using the provided groovy code

8)	Make sure you insert your routers public IP address (typically your mycrestron.com url) and the port you forwarded in step 1 into the goovy code

9)	Save the code and then publish it "For Me"

10)	Using the SmartThings App on your phone, Goto the marketplace and then select SmartApps.  

11)	Scroll to the bottom of the screen and select My Apps.

12)	You should see the CrestronAPI Smart App Listed.  Click on it and use the displayed screen to enable your devices to send updates when your devices change state through the SmartApp

13)	Picking up on the SmartThings web site where you were on step 9, press the App Settings button at the top of the screen

14)	Toward the bottom of the page click on OAuth

15)	Enable OAuth and copy down your Client ID and Secret ID

16)	Following the code in the example program add the SmartThings Receiver module to your program with the clientID and Secret ID from step 11 as parameters and the port from step 1 as a 3rd parameter.  Load the program to your processor

17)	Pulse the Startup Signal and then Pulse the Authenticate Signal.  

18)	Copy the url from the event log to your browser and use the displayed page to authorize your Crestron processor to access your devices

19)	Pulse the Print_Device_List signal on the Crestron SmartThings Receiver Module.  

20)	Use the device ID's that are added to the error log to add SmartThings modules to your program to control your devices.  The device ID's need to be added as parameters to these modules.

21)	You should now be able to control your SmartThings devices through your Crestron program and the outputs of the modules should reflect the state of the devices.  
