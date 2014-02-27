###############################################################################
#
# Copyright (c) 2011 - 2013 Talend Inc. - www.talend.com
# All rights reserved.
#
# This program and the accompanying materials are made available
# under the terms of the Apache License v2.0
# which accompanies this distribution, and is available at
# http://www.apache.org/licenses/LICENSE-2.0
#
###############################################################################

Library Tutorial Example
=================================
This example demonstrates using of different message exchange patterns on the base of Library service.

Example contains single module: library-tutorial.
Deployment:
- web container for service
- standalone for client

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

Running the Example
-------------------
a) Without Service registry

b) With Service Registry
   1. Prepare TESB container
   - start TESB container
   - start Service Registry server tesb:start-registry
   - import Library WSDL: tregistry:create wsdl <base-dir>/tesb-rt-se/examples/tesb/library
-tutorial/src/main/resources/Library.wsdl
   2. Run example
   -
   -  
   
