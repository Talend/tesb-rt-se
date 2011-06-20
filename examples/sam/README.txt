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
Example for Service Activity Monitoring(SAM)
============================================
This example illustrates how to deploy and configure Service Activity Monitoring on CXF based project. 

Prerequisite
------------

To build and run this example, you must install the J2SE Development Kit (JDK) 5.0 or above.
A servlet container is Tomcat 5.5 or above.


Start sam-server and Derby database in OSGI Container
-----------------------------------------------------

 * starting the TESB OSGi container:
 
    cd talend-esb-<version>/container/bin
	Linux: ./tesb
	Windows: tesb.bat

 * starting sam-server and Derby database in TESB OSGi container:
 	
	Enter the following command on the OSGI console:
    karaf@tesb> features:install tesb-derby-starter
    karaf@tesb> features:install tesb-sam-server

By default the TESB OSGI Container runs on 8080 port and the sam-server is accessible under that port.


Configurations
--------------
Adopt the agent.properties file for the sam-server url.

Edit the following files:
./sam-example-service/src/main/resources/agent.properties
./sam-example-service2/src/main/resources/agent.properties

and if you are using the default port for the TESB OSGI container, change the service.url property to the following:

service.url=http://localhost:8080/services/MonitoringServiceSOAP

Find below the detailed description of the properties defined in agent.properties file:

<!-- Default interval for scheduler. Start every X milliseconds a new scheduler -->
collector.scheduler.interval=60000
<!-- Number of events within one service call. This is a maximum number. 
	If there are events in the queue, the events will be processed. -->
collector.maxEventsPerCall=200
<!-- Enable message content logging for event producer. true/false Default: false -->
log.messageContent=true
<!-- Configure url to monitoring service -->
service.url=http://localhost:8080/sam-server-war/services/MonitoringServiceSOAP
<!-- Number of retries to access monitoring service, Default: 5 -->
service.retry.number=3
<!-- Delay in milliseconds between the next attemp to send. Default: 1000 -->
service.retry.delay=5000


Building and running the example using Maven
--------------------------------------------

From the base directory of this sample (i.e., where this README file is
located), the pom.xml file is used to build and run this sample. 

Using either UNIX or Windows:

  mvn install   (builds the example)

To remove the generated target/*.* files, run "mvn clean".  


Deploy into Tomcat
------------------

TESB OSGI container by default runs on 8080 port, so make sure your tomcat is configured to run on a different port.

copy ./sam-example-service/target/sam-example-service.war to $TOMCAT_HOME/webapps/sam-example-service.war
copy ./sam-example-service2/target/sam-example-service2.war to $TOMCAT_HOME/webapps/sam-example-service2.war

start Tomcat.


Testing with Soap messages
--------------------------
From SoapUI, send soap messages to endpoint: http://localhost:<tomcat_port>/sam-example-service2/services/CustomerServicePort

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cus="http://customerservice.example.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cus:getCustomersByName>
         <name>jacky</name>
      </cus:getCustomersByName>
   </soapenv:Body>
</soapenv:Envelope>

You will see the events sent out on the console log from Tomcat.

