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

Example for Locator SOAP Service
============================================
This example illustrates the usage of the Locator SOAP Service

This example consists of the following components:

service/
   - Greeter service which is registered with the locator soap service after the starting.

common/
   - This directory contains the code that is common to both the client and the Greeter server.

client/
   - This is a sample client application that uses the Locator Soap Service to dynamically
     discover the service endpoint and invoke the service.


Building the Demo
-----------------

Using either UNIX or Windows:

    From the example parent directories (i.e., Talend-ESB-<version>/examples/talend, Talend-ESB-<version>/examples/talend/tesb),
    run the following command to install the example parent pom files: talend-esb-examples-<version>.pom and talend-esb-examples-parent-<version>.pom into local maven repo.

    mvn install --non-recursive

    From the locator-service sample root directory (i.e., talend-esb-<version>/examples/talend/tesb/examples/locator-service),
    run the following command to install the locator-service sample parent pom file: locator-service-parent-<version>.pom
    into local maven repo.

    mvn install --non-recursive

    From the base directory of this sample (i.e., where this README file is located),
    the maven pom.xml file can be used to build and run the demo.

    mvn install


Starting the Demo
-----------------

- Start zookeeper in container
    tesb:start-locator

- Start locator soap service in container
	feature:install tesb-locator-soap-service

- Start service
    cd war; mvn jetty:run
    (or deploy the war/target/services.war into Tomcat)
	(if uses jetty "serviceHost" parameter in soap-service/war/src/main/webapp/WEB-INF/web.xml
	should be changed to "http://localhost:8080/GreeterService")

- Start client (from the command line cd client; mvn exec:java)
