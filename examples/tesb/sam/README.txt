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

Example for Service Activity Monitoring(SAM)
============================================
This example illustrates how to deploy and configure Service Activity Monitoring on CXF based projects.


Prerequisite
------------

To build and run this example, you must install:
    J2SE Development Kit (JDK) 1.6 or above
    Apache Maven 3.x or above
    Servlet container (Tomcat 6.0 or above)
    OSGI Container (TESB Container or Karaf 2.2.x or above)

Start Derby database and sam-server
-----------------------------------

There are two ways to start Derby database and sam-server alternatively.

Start Derby database and sam-server using sam-server-jetty

	cd Talend-ESB-<version>/examples/talend/tesb/sam/sam-server-jetty
	mvn jetty:run-war

	By default the Jetty server runs on 9080 port and the sam-server can be accessible under this url:
	http://localhost:9080/services/MonitoringServiceSOAP?wsdl

Start Derby database and sam-server in OSGI Container

	* starting the TESB OSGi container:

	cd Talend-ESB-<version>/container/bin
	Linux: ./trun
	Windows: trun.bat

	* starting Derby database and sam-server in TESB OSGi container:

	Enter the following command on the OSGI console:
	karaf@trun> tesb:start-sam

	By default the TESB OSGI Container runs on 8040 port and the sam-server can be accessible under this url:
	http://localhost:8040/services/MonitoringServiceSOAP?wsdl


Configurations
--------------

Edit the following files:
./sam-example-service/src/main/resources/agent.properties
./sam-example-service2/src/main/resources/agent.properties

change the service.url property to the following:
    service.url=http://localhost:9080/services/MonitoringServiceSOAP
    (if Start Derby database and sam-server using sam-server-jetty)

    service.url=http://localhost:8040/services/MonitoringServiceSOAP
    (if Start Derby database and sam-server in OSGI Container)



Building and running the example using Maven
--------------------------------------------

From the base directory of this sample (i.e., where this README file is
located), the pom.xml file is used to build and run this sample.

Using either UNIX or Windows:

  mvn install   (builds the example)

To remove the generated target/*.* files, run "mvn clean".


Deploy examples into Tomcat
---------------------------

copy ./sam-example-service/target/sam-example-service.war to $TOMCAT_HOME/webapps/sam-example-service.war
copy ./sam-example-service2/target/sam-example-service2.war to $TOMCAT_HOME/webapps/sam-example-service2.war

then start Tomcat.


Testing with Soap messages
--------------------------
From SoapUI Tool, send soap messages to endpoint: http://localhost:8080/sam-example-service2/services/CustomerServicePort

Example soap message:
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cus="http://customerservice.example.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cus:getCustomersByName>
         <name>jacky</name>
      </cus:getCustomersByName>
   </soapenv:Body>
</soapenv:Envelope>

then, you will see the SAM events generated and stored into Derby database (or from Tomcat logs).


Please find more information from help.talend.com.
