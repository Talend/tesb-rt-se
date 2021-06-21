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

Rent-a-Car on Talend Example
=================================
This example demonstrates how a service developed with Talend ESB can be deployed on Tomcat.
It is based on Rent-a-Car example which can be found in examples/tesb/rent-a-car folder.

Modules list:
crmservice/
    - CRM Service which implements the getCRMInformation() and getCRMStatus() operations.

reservationservice/
    - Reservation Service which implements getAvailableCars(), submitCarReservation() and
	  getConfirmationOfReservation() operations.

app-reservation/
    - Simple Web-UI client which lets user invoke the services step-by-step.

registry-artifacts/
    - Set of artifacts that have to be uploaded into Talend Registry in order to use
      Registry example.


Building the Example
--------------------
From the base directory of this example (i.e., where this README file is
located), the maven pom.xml file can be used to build this example.

Using maven commands on either UNIX/Linux or Windows:
(JDK 1.6.0 and Maven 3.0.3 or later required)

mvn clean install                 (the same as the next one)
mvn clean install -Pslsam         (for building Service Locator and Service Activity Monitoring enabled Rent-a-Car example)
mvn clean install -Psreg          (for building Talend Registry enabled Rent-a-Car example)


Uploading the Registry artifacts
--------------------------------
Registry-enabled example requires some artifacts to be uploaded into the Talend Registry
before the example can be deployed. For instructions, see README.txt in registry-artifacts folder.


Deployment prerequisites
------------------------
Talend ESB Container and Apache Tomcat are required to run this example. Before
deployment of the example, the Container must be running, and Locator server and
SAM server must be started (for Registry-enabled example, additionally Registry server
must be started).

It is also possible to configure Tomcat for automatic deployment of the WAR files
with maven. To achieve this it is necessary to create a user within Tomcat with name
'tomcat' and password 'tomcat', which has corresponding role (for Tomcat 7 it must have
at least 'manager-script' role).


Deploying the Example
---------------------
There are two ways to deploy the example on Tomcat: automatic and manual.
Note: Tomcat and Talend ESB Container must be running.

1. Automatic deployment: mvn tomcat:deploy

2. Manual deployment: copy the next files into <tomcat_home>/webapps folder:
- crmservice/service-endpoint/target/crmservice-service.war
- reservationservice/service-endpoint/target/reservationservice-service.war
- app-reservation/target/app-reservation.war


Running the Example
-------------------
Open http://localhost:8080/app-reservation/ in a browser.
The interface of the application is similar to normal Rent-a-Car example.
