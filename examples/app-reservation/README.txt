###############################################################################
#
# Copyright (c) 2011 Talend Inc. - www.talend.com
# All rights reserved.
#
# This program and the accompanying materials are made available
# under the terms of the Apache License v2.0
# which accompanies this distribution, and is available at
# http://www.apache.org/licenses/LICENSE-2.0
#
###############################################################################
Rent-a-Car Description Example 
=======================================

For this example we have chosen a common business use case scenario: a simplified real-world example from the domain of car rental companies. It contains two services: CRMService and ReservationService.

Use Case Scenario to Rent a Car
-------------------------------
1.A customer enters prerequisite data into a form such as her name, the pick-up and return dates, the preferred type of rental rates, and submits her query. 
2.Next, the application calls the CRMService to retrieve the customer data and customer status information. 
3.The customer is presented with a list of the available cars and selects one from the presented list of results. 
4.Finally, the application calls the ReservationService to submit the reservation request and then displays a reservation confirmation notice to the customer. 

Running app-reservation 
-----------------------
Start CRMService and ReservationService as it described in 
...\crmservice\README.txt and ...\reservationservice\README.txt

From the base directory of this sample (i.e., where this README file is located), the maven pom.xml file can be used to build and run the demo. 
Using either UNIX or Windows:

    mvn install

Running this command will build the demo and create an OSGi bundle for deploying the service to OSGi containers.

The OSGi bundle will be created:
...\app-reservation\target\app-reservation-1.0.jar

Starting the application
------------------------

1.Start OSGi console
2.Copy app-reservation-1.0.jar into folder <Talend ESB Runtime>/container/deploy.

In console you will see:

---TALEND ESB: Rent a Car (OSGi) Commands---
        car:GUI                                  (Show GUI)
        car:search <user> <pickupDate> <returnDate>
 (Search for cars to rent, date format yyyy/mm/dd)
        car:rent   <pos>
 (Rent a car listed in search result of carSearch)

Using TALEND ESB Rental Client GUI
----------------------------------
1. To start the GUI, type car:GUI  in the console window. 
2. Select aebert from the drop-down box. 
3. Click Find to see the results.  You will see list of available cars 
4. Click the highlighted line or select any other option and click Reserve . 
5. You can now see confirmation page. 
6. Click Close to stop the GUI. 

Using search command
--------------------
1.In console type:

car:search aebert 2011/01/26 2011/01/26

2.You will see list of available cars:
Found 5 cars.
Car details
   1  VW         Golf     Standard            50.00         75.00      40.00
   2  BMW        320i     Sport               60.00         90.00      45.00
   3  Mazda      MX5      Sport               65.00         95.00      50.00
   4  Lexus      LX400    SUV                 85.00        120.00     100.00
   5  Mercedes   E320     Delux               95.00        140.00     100.00

Using  rent command
-------------------
1.In console type:

car:rent 2

2.You can now see confirmation

Reservation ID SGA-686277

Customer details
----------------
 Name:   Andrea Ebert
 eMail:  info@sopera.de
 City:   Muenchen
 Status: PLATIN

Car details
-----------
 Brand: BMW
 Model: 320i

Reservation details
-------------------
 Pick up date: 2011/01/26
 Return date:  2011/01/26
 Daily rate:        60.00
 Weekend rate:      90.00
 Credits:          210

Thank you for renting a car with Talend ESB :-)




