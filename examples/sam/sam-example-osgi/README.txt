Example how to use Service Activity Monitoring in OSGi
======================================================

Prerequisite
------------

You need an installation of the TIF container and an Apche Tomcat installation.
You should have built the tesb from source with 
> mvn clean install

Install
-------

Set the http port for TIF to 9090 in the file org.ops4j.pax.web.cfg.

Start TESB Container

> startup.bat

> features:install war
> features:install camel-cxf
> install mvn:commons-lang/commons-lang/2.6
> install mvn:org.talend.esb/sam-common/4.0
> install mvn:org.talend.esb/sam-agent/4.0

Copy the sam-example-osgi-4.0.jar to the deploy directory

> list

List should show that the example was started

Check that the service can be reached on:
http://localhost:9090/services/CustomerServicePort?wsdl

Copy sam-server.war to the Tomcat webapps directory. Make sure tomcat listens on Port 8080.
Start tomcat

> startup.bat

Check that the monitoring service can be reached on:
http://localhost:8080/sam-server-war/services/MonitoringServiceSOAP?wsdl

Run the client
--------------

Run ExampleClientMainOSGI in the project sam-example-client

The client should run successfully. That logs should show that the events were written to the server.
