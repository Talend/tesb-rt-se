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
Example how to use Service Activity Monitoring in OSGi
======================================================

Prerequisite
------------
To build and run this example, you must install:
    J2SE Development Kit (JDK) 1.6 or above
    Apache Maven 3.x or above
    OSGI Container (TESB Runtime Container or Karaf container)


Building the example using Maven
--------------------------------------------

From the base directory of this sample (i.e., where this README file is
located), the pom.xml file is used to build and run this sample.

Using either UNIX or Windows:

  mvn install   (builds the example)

To remove the generated target/*.* files, run "mvn clean".


Deploy SAM Server into Tomcat
-----------------------------
Copy sam-server war to the Tomcat webapps directory. Make sure tomcat listens on Port 8080.
Start tomcat

> startup.bat

Check that the monitoring service can be reached on:
http://localhost:8080/sam-server-war/services/MonitoringServiceSOAP?wsdl


Install into TESB Runtime Container
-----------------------------------
Start TESB Runtime Container

>install -s mvn:org.talend.esb.examples/sam-example-osgi/<version>

or

deploy the sam-example-osgi-<version>.jar bundle into Container/deploy folder.

Check that the service can be reached on:
http://localhost:8040/services/CustomerServicePort?wsdl


Run the client
--------------

Run ExampleClientMainOSGI in the project sam-example-client

The client should run successfully. That logs should show that the events were written to
the SAM Server.
