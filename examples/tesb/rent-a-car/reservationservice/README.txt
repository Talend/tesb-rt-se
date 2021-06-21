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

ReservationService of Rent-a-Car Example
========================================

Modules list:
common/
    - This module contains the ReservationService.wsdl which is used to generate code for other modules.

service/
    - This is where a ReservationService implementation shared by JAX-WS endpoints is located.

client/
    - This is a client application that shows how CXF client invoking to the CXF endpoint.

client-sl-sam/
    - This directory contains the locator and sam enabled ReservationService client.

client-sts/
    - This directory contains the sts enabled ReservationService client.

client-all/
    - This directory contains the all features enabled ReservationService client.

service-endpoint-jmx/
    - This directory contains the basic ReservationService endpoint.

service-endpoint-sl-sam/
    - This directory contains the locator and sam enabled ReservationService endpoint.

service-endpoint-sts/
    - This directory contains the sts enabled ReservationService endpoint.

service-endpoint-all/
    - This directory contains the all features enabled ReservationService endpoint.


Building the Example
---------------------------------------
From the base directory of this example (i.e., where this README file is
located), the maven pom.xml file can be used to build the example.

Using maven commands on either UNIX/Linux or Windows:
(JDK 1.6.0 and Maven 3.0.3 or later required)

mvn clean install                 (for basic ReservationService)
mvn clean install -Pslsam         (for Service Locator and Service Activity Monitoring enabled ReservationService)
mvn clean install -Psts           (for Security Token Service enabled ReservationService)
mvn clean install -Pall           (for all features enabled ReservationService)
