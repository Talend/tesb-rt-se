Example for Service Activity Monitoring(SAM)
============================================
This example illustrates how to deploy and configure Service Activity Monitoring on CXF based project. 

Prerequisite
------------

To build and run this example, you must install the J2SE Development Kit (JDK) 5.0 or above.
A servlet container is Tomcat 5.5 or above.

Building and running the example using Maven
--------------------------------------------

From the base directory of this sample (i.e., where this README file is
located), the pom.xml file is used to build and run this sample. 

Using either UNIX or Windows:

  mvn install   (builds the example)

To remove the generated target/*.* files, run "mvn clean".  

Configurations
--------------
./sam-example-service/src/main/resources/agent.properties
./sam-example-service2/src/main/resources/agent.properties

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

Deploy into Tomcat
------------------
copy ./sam-example-service/target/sam-example-service.war to $TOMCAT_HOME/webapps/sam-example-service.war
copy ./sam-example-service2/target/sam-example-service2.war to $TOMCAT_HOME/webapps/sam-example-service2.war

start Tomcat.

Testing with Soap messages
--------------------------
From SoapUI, send soap messages to endpoint: http://localhost:8080/sam-example-service2/services/CustomerServicePort

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:cus="http://customerservice.example.com/">
   <soapenv:Header/>
   <soapenv:Body>
      <cus:getCustomersByName>
         <name>jacky</name>
      </cus:getCustomersByName>
   </soapenv:Body>
</soapenv:Envelope>

you can monitor the console log from Tomcat.

