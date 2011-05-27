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

rent-a-car/reservationservice: Reservation service for Rent a Car Demo Example.

Building the Demo
---------------------------------------

This sample consists of 11 parts:
common/   - This directory contains the ReservationService.wsdl which is used to generate the initial code. 
            
service/  - This is where a ReservationService service implementation shared by JAX-WS endpoints is located

client/   - This is a sample client application that shows how CXF JAX-WS proxies are invoking on remote 
            JAX-WS endpoints represented by ReservationService interface 
client-locator/   - This directory contains the locator enabled reservationservice client
client-sam/   - This directory contains the sam enabled reservationservice client
client-sts/   - This directory contains the sts enabled reservationservice client
service-endpoint/   - This directory contains the basic reservationservice endpoint
service-endpoint-locator/   - This directory contains the locator enabled reservationservice endpoint
service-endpoint-sam/   - This directory contains the sam enabled reservationservice endpoint
service-endpoint-sts/   - This directory contains the sts enabled reservationservice endpoint

From the base directory of this sample (i.e., where this README file is
located), the maven pom.xml file can be used to build and run the demo. 


Using either UNIX or Windows:

mvn clean install                 (for basic reservationservice)
mvn clean install -Plocator       (for Service Locator enabled reservationservice)
mvn clean install -Psam           (for Service Activity Monitoring enabled reservationservice)
mvn clean install -Psts           (for Security Token Service enabled reservationservice)

Running this command will build the demo and create an OSGi bundle 
for deploying the service to OSGi containers.

There are 11 OSGi bundles will be created:
common/target/reservationservice-common-4.0.jar
service/target/reservationservice-service-4.0.jar
client/target/reservationservice-client-4.0.jar
client-locator/target/reservationservice-client-locator-4.0.jar
client-sam/target/reservationservice-client-sam-4.0.jar
client-sts/target/reservationservice-client-sts-4.0.jar
service-endpoint/target/reservationservice-service-endpoint-4.0.jar
service-endpoint-locator/reservationservice-service-endpoint-locator-4.0.jar
service-endpoint-sam/reservationservice-service-endpoint-sam-4.0.jar
service-endpoint-sts/reservationservice-service-endpoint-sts-4.0.jar

Starting the service
---------------------------------------
 * From within the Talend Service Factory OSGi container:

1.Install Talend Service Factory Community Edition which include OSGi container or higher from http://www.talend.com/download.php.

You can find out how to get started with OSGi container here: http://karaf.apache.org/

1.Start OSGi:
run <takend-sf>/container/bin/start
2.Deploy service into OSGi container.

copy follow bundles into folder <Talend ESB Runtime>/container/deploy
for basic:
reservationservice-common-4.0.jar
reservationservice-service-4.0.jar
reservationservice-client-4.0.jar

for locator enabled:
reservationservice-common-4.0.jar
reservationservice-service-4.0.jar
reservationservice-service-endpoint-locator-4.0.jar
reservationservice-client-locator-4.0.jar

for sam enabled:
reservationservice-common-4.0.jar
reservationservice-service-4.0.jar
reservationservice-service-endpoint-sam-4.0.jar
reservationservice-client-sam-4.0.jar

for sts enabled:
reservationservice-common-4.0.jar
reservationservice-service-4.0.jar
reservationservice-service-endpoint-sts-4.0.jar
reservationservice-client-sts-4.0.jar

3.Type in console command "list".
You will see (for example)
[ 117] [Active     ] [            ] [       ] [   60] ReservationService Common (4.0.0)
[ 118] [Active     ] [            ] [Started] [   60] ReservationService Service (4.0.0)
[ 119] [Active     ] [            ] [Started] [   60] ReservationService Client (4.0.0)