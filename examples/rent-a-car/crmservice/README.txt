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

CRMService of Rent-a-Car Example
=======================================

Modules list:
common/   
    - This module contains the CRMService.wsdl which is used to generate code for other modules.
	
service/  
    - This is where a CRMService service implementation shared by JAX-WS endpoints is located.

client/   
    - This is a client application that shows how CXF client invoking to the CXF endpoint.

client-locator/   
    - This directory contains the locator enabled crmservice client.

client-sam/   
    - This directory contains the sam enabled crmservice client.

client-sts/   
    - This directory contains the sts enabled crmservice client.

service-endpoint/   
    - This directory contains the basic crmservice endpoint.

service-endpoint-locator/   
    - This directory contains the locator enabled crmservice endpoint.

service-endpoint-sam/   
    - This directory contains the sam enabled crmservice endpoint.

service-endpoint-sts/   
    - This directory contains the sts enabled crmservice endpoint.

service-endpoint-jmx/   
    - This directory contains the jmx enabled crmservice endpoint.

	
Building the Example
---------------------------------------
From the base directory of this example (i.e., where this README file is
located), the maven pom.xml file can be used to build the example. 

Using maven commands on either UNIX/Linux or Windows:
(JDK 1.6.0 and Maven 3.0.3 or later required)

mvn clean install                 (for basic crmservice)
mvn clean install -Plocator       (for Service Locator enabled crmservice)
mvn clean install -Psam           (for Service Activity Monitoring enabled crmservice)
mvn clean install -Psts           (for Security Token Service enabled crmservice)
mvn clean install -Pjmx           (for JMX enabled crmservice)

