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

CRMService of Rent-a-Car Example
=======================================

Modules list:
common/
    - This module contains the CRMService.wsdl which is used to generate code for other modules.

service/
    - This is where a CRMService service implementation shared by JAX-WS endpoints is located.

client/
    - This is a client application that shows how CXF client invoking to the CXF endpoint.

service-endpoint/
    - This directory contains the crmservice endpoint built as a WAR file.


Building the Example
---------------------------------------
From the base directory of this example (i.e., where this README file is
located), the maven pom.xml file can be used to build the example.

Using maven commands on either UNIX/Linux or Windows:
(JDK 1.6.0 and Maven 3.0.3 or later required)

mvn clean install                 (the same as the next one)
mvn clean install -Pslsam         (for Service Locator and Service Activity Monitoring enabled crmservice)
mvn clean install -Psreg          (for Talend Registry enabled crmservice)

