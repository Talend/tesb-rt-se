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

Rent-a-Car Example
=================================
For this example we have chosen a common business use case scenario: a simplified real-world example from
the domain of car rental companies. This uses the functionality of the Customer Relationship Management (CRM)
service to supply information about the customer and the reservation service to reserve a car.

For more information about this example, please refer to the GettingStartedGuide doc.

Modules list:
crmservice/
    - CRM Service which implements the getCRMInformation() and getCRMStatus() operations.

reservationservice/
    - Reservation Service which implemants getAvailableCars(), submitCarReservation() and 
	  getConfirmationOfReservation() operations.

app-reservation/
    - Commands and simple UI client which let user invoke the services step-by-step.

features/
    - the feature files which will be used to install Rent-a-Car example to TESB container.

Building the Example
--------------------
From the base directory of this example (i.e., where this README file is
located), the maven pom.xml file can be used to build this example. 

Using maven commands on either UNIX/Linux or Windows:
(JDK 1.6.0 and Maven 3.0.3 or later required)

mvn clean install                 (for building basic Rent-a-Car example)
mvn clean install -Plocator       (for building Service Locator enabled Rent-a-Car example)
mvn clean install -Psam           (for building Service Activity Monitoring enabled Rent-a-Car example)
mvn clean install -Psts           (for building Security Token Service enabled Rent-a-Car example)
mvn clean install -Pjmx           (for building JMX enabled Rent-a-Car example)

Install/Deploy the Example
--------------------------
1. Start the TESB container
2. Install Rent-a-Car features to the TESB container
   For basic Rent-a-Car example:
      features:addurl mvn:org.talend.esb.examples.rent-a-car/features/5.0.0/xml
      features:install tesb-rac-services
      features:install tesb-rac-app
   For Service Locator enabled Rent-a-Car example:
      features:addurl mvn:org.talend.esb.examples.rent-a-car/features-locator/5.0.0/xml
      features:install tesb-rac-services-locator
      features:install tesb-rac-app-locator
   For Service Activity Monitoring enabled Rent-a-Car example:
      features:addurl mvn:org.talend.esb.examples.rent-a-car/features-sam/5.0.0/xml
      features:install tesb-rac-services-sam
      features:install tesb-rac-app-sam
   For Security Token Service enabled Rent-a-Car example:
      features:addurl mvn:org.talend.esb.examples.rent-a-car/features-sts/5.0.0/xml
      features:install tesb-rac-services-sts
      features:install tesb-rac-app-sts
   For JMX enabled Rent-a-Car example:
      features:addurl mvn:org.talend.esb.examples.rent-a-car/features-jmx/5.0.0/xml
      features:install tesb-rac-services-jmx
      features:install tesb-rac-app-jmx

Running the Example
-------------------
From the TESB container console, these commands are available after Rent-a-Car example installed.
   car:gui (Show GUI)
   car:search <user> <pickupDate> <returnDate>
   (Search for cars to rent, date format yyyy/mm/dd)
   car:rent <pos>
   (Rent a car listed in search result of carSearch)
   
   
More detailed information, please refer to the GettingStartedGuide doc.