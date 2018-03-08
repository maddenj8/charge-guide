# Technical Specification for ChargeGuide

#### Table of contents: 










page 1 Overview

page 1 System Architecture

page 2 High-Level Design

page 3 Problems and Resoloutions

page 5 Installation Guide



































###### Overview: 1.1


ChargeGuide is a android application used to inform the user which chargers are working in use or broken. The user can then select a charger on a map and route with a estimate of state of charge on arrival and distance to the charger to it directly routing to those chargers directly. The user can also search for a destination and the application will select upto 3 possible routes each route can contain upto 4 stops. The user can also configure what there state of charge is at the bottom of the main page and when the click on a charger. The user can then select one of these routes and open it up in google maps directly in navigation mode. There is also a brief help page explaining the chargers states and the different connectors. When the user first uses the app they can setup where they live and what car they drive so the app can estimate the range and only show applicable chargers.

The charger information is acquired from pulling form a kml from the esb charge point cap. This kml file is then parsed into 3 files one for each charger. The application gets its information over ftp from dcu student.computing server.  This information is updated every 10 minutes.



###### Glossary 1.2:

Soc = State of charge
Chademo , CCS , AC43  , Chademo = Fast charge plugs




















#### 2 System Architecture

This section describes the high-level overview of the system architecture showing the distribution functions across (potential) system modules. Architectural components that are reused or 3rd party should be highlighted. Unlike the architecture in the Functional Specification  this description must reflect the design components of the system as it is demonstrated.


 The application is made up of get_charging_data.sh which wgets the kml file , split.py splits out the kml files into 3 files chademo ccs and ac. Parse.py pares out the kml file into the latitude longitude state and name spit by “|”. 

The main class for the application is MapMain.java.


Inside of MapMain.java there is downloadChargerInfo this uses sftp form the school of computing servers to pulls the appropriate file for the car and stores it on the device.  This is an external library from Java secure channel.

getDistance() gets the distance from a charger in km to home and getdistancetodestnation() gets the distance between any two coordinates.

pinDrop will open up the downloaded file and makes it a marker object and draws it on the map this will give a title when clicked on an gives the apporatpate icon.

 mMap.setOnInfoWindowClickListener checks to see if if destination updated is set if so it opens chargerInfo.class activity is opened otherwise it ignores the input.

 onNavigationItemSelected() listens for any click on the navigation drawer so it can start a the help or set up activity.

getDirectionUrl() makes a url that is used by startDirectionSetps to draw a path from home to the charger to the destination.

onmapReady() will position the camera with just ireland in frame and call pin drop and gets the users home location.

getNext() charger will pick the chargers that are near to the chargers that are in range sort them in terms of distance to the destination.

addMarker() is given a marker and puts an icon on the map with the corresponding title and icon

route0 is a button for journeys with no need to stop and buttons 1 to  3 deal with routes with routes with waypoints

The charger info class has to display information about the charger when the charger icon is clicked.

Information about the charger is passed by a bundle from map main class.

Using the battery size in kwh’s and the state of charge percentage it will estimate the range in km and the charge time. This is then displayed to the user. There is a button that will open google maps in navigation mode passing the charger as the destination.

The onCreate has getting a shared preference set up in the setup page the battery size in kwh and is pulled.


In the first_launch class is called if the app has never been run before.
It is made up of 2 spinners used to take the make and model of car. The selection of the first spinner sets the options for the second spinner.

The first_launch class is called when the app is first run this is determened with SharedPreference setup being true

onCheckBoxClicked will pull the gps latitude and longitude using location manager and saves the coordinates to shared Preferences as a float later used in mapMain and chargerInfo.


#### 3 High-Level Design

![](http://student.computing.dcu.ie/~nugenc12/pref.PNG)
This is the class digram explaing user preference are saved and axcessed within the program. The preferences are created in the first launch activity when the application first launches then it is axcessed and modified in mainMap and chargerInfo.

![](http://student.computing.dcu.ie/~nugenc12/downloadChargerInfo.png)
This is a use case diagram here the program downloads the correct file from  the server via sftp and save to the device.

![](http://student.computing.dcu.ie/~nugenc12/setupusecase.PNG)

The setup use case diagram is where the user makes a route by clicking on the search bar then selecting one of the routes displayed as a button then opening the route in google in google maps

#### 4 Problems and Resoloutions

Getting sftp working to transfer the files from the server to the device was an issue at first as using a sftp on a setup on a laptop in the lab was not working as there was a firewall in place. This was resolved by sshing into dcu servers to start the bash script to update the data then using sftp on the phone to get the data.

The split.py and parse.py were part of one larger program but there was an issue where chaddemo_output.txt would be concanated onto ccs_output.txt. This was resolved after some time by putting the split and parse functions into seprate progams.

Selecting diffrent routs was at one point accomplissed by clicking on the destnation flag and then selecting the charger then selecting the destnation in a seprate acctivity. This solution was not realisticly compatable with multible stops on as there would be upto 4 buttons per charger. There was a better solution propoed wehre a buttion whould be drawn on the main page when a route is drawn and then use waypoints in google maps.


There was a issue where on the volkswagen e golf 24 Kwh was being selected was reporeted by a tester as causing the app to crash. This was because the string "e golf 24 Kwh" was being parsed and the program did not expect the model being the model being 4 words long rather than 3 words long this was resoled by replacing e golf with e-golf.

On two occasions using large images caused the app to crash. The hamburger icon on the navagaton drawer was 1000 x 1000 which caused it to crash on one phone lag on another and run just fine on another phone. This was resolved by making the image smaller. A simalar thing occoured when the help page was running very slowly as the images has to be rescalled.

Getting location of the user took several days as it was complicated as it required checking for premissions and having several catch and accept statements that took about a week to get in order.


We tryed to estmate the arrival time by just assuming an average speed of 90 Kph but this was found to overestmate the time of arrival for short journey and underestmate the arrival time for short journeys sometimes by a factorof two so this feture was droped. Using the google maps api would have worked but there was not enfough time.


#### 5 Installation Guide

An android phone running Lollipop(api version 21) or above is required to run the application. The application is 4mb in size and needs about 1gb of ram to run smothley.

Enable installs form unknown sorces in settings > security.

![apk on gitlab](http://student.computing.dcu.ie/~nugenc12/trusted.png)

This is a guide to install Charge Guide application.

Locate the APK file on the gitlab project.

![apk on gitlab](http://student.computing.dcu.ie/~maddenj8/images/install_1.png)

Download the APK and place on an android device.

Locate this APK in the devices downloads manager.

![apk in downloads](http://student.computing.dcu.ie/~maddenj8/images/install_2.png)

Select the APK and press the install option.

![apk in downloads](http://student.computing.dcu.ie/~maddenj8/images/install_3.png)

Once installed you have the option to open the application.
You will then be prompted to allow ChargeGuide to use location. Click alow as it is necessary for the app to function corretely.






 


