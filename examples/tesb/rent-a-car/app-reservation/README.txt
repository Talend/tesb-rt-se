###############################################################################
#
# Copyright (c) 2006-2021 Talend Inc. - www.talend.com
# All rights reserved.
#
# This program and the accompanying materials are made available
# under the terms of the Apache License v2.0
# which accompanies this distribution, and is available at
# http://www.apache.org/licenses/LICENSE-2.0
#
###############################################################################

Application of Rent-a-Car Example
=======================================

Use Case Scenario to Rent a Car
-------------------------------
1.A customer enters prerequisite data into a form such as her name, the pick-up and return dates, the preferred type of rental rates, and submits her query.
2.Next, the application calls the CRMService to retrieve the customer data and customer status information.
3.The customer is presented with a list of the available cars and selects one from the presented list of results.
4.Finally, the application calls the ReservationService to submit the reservation request and then displays a reservation confirmation notice to the customer.

Building the Example
---------------------------------------
From the base directory of this example (i.e., where this README file is
located), the maven pom.xml file can be used to build the example.

Using maven commands on either UNIX/Linux or Windows:
(JDK 1.6.0 and Maven 3.0.3 or later required)

mvn clean install


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




