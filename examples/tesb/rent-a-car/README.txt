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
    - Reservation Service which implements getAvailableCars(), submitCarReservation() and
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
mvn clean install -Pslsam         (for building Service Locator and Service Activity Monitoring enabled Rent-a-Car example)
mvn clean install -Psts           (for building Security Token Service enabled Rent-a-Car example)
mvn clean install -Pall           (for building Rent-a-Car example that enabled with all tesb features: SL, SAM, STS)

Install/Deploy the Example
--------------------------
1. Start the TESB container
2. Install Rent-a-Car features to the TESB container
   For basic Rent-a-Car example:
      feature:repo-add mvn:org.talend.esb.examples.rent-a-car/features/<version>/xml
      feature:install tesb-rac-services
      feature:install tesb-rac-app
   For Service Locator and Service Activity Monitoring enabled Rent-a-Car example:
      feature:repo-add mvn:org.talend.esb.examples.rent-a-car/features-sl-sam/<version>/xml
      feature:install tesb-rac-services-sl-sam
      feature:install tesb-rac-app-sl-sam
      (Note: Zookeeper server and SAM server should be started before install features for this scenario.)
   For Security Token Service enabled Rent-a-Car example:
      feature:repo-add mvn:org.talend.esb.examples.rent-a-car/features-sts/<version>/xml
      feature:install tesb-rac-services-sts
      feature:install tesb-rac-app-sts
      (Note: STS server should be started before install features for this scenario.)
   For all features enabled Rent-a-Car example:
      feature:repo-add mvn:org.talend.esb.examples.rent-a-car/features-all/<version>/xml
      feature:install tesb-rac-services-all
      feature:install tesb-rac-app-all
      (Note: Zookeeper server, SAM server and STS server should be started before install features for this scenario.)
Running the Example
-------------------
From the TESB container console, these commands are available after Rent-a-Car example installed.
   car:gui (Show GUI)
   car:search <user> <pickupDate> <returnDate>
   (Search for cars to rent, date format yyyy/mm/dd)
   car:rent <pos>
   (Rent a car listed in search result of carSearch)


More detailed information, please refer to the GettingStartedGuide doc.
