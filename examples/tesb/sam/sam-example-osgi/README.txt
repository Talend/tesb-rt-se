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
Example how to use Service Activity Monitoring in OSGi
======================================================

Prerequisite
------------
You need an installation of the TESB OSGI container and an Apche Tomcat installation.

Install
-------
Set the http port for TIF to 9090 in the file org.ops4j.pax.web.cfg.

Start TESB Container

> startup.bat

> features:addurl mvn:org.talend.esb/features/4.0/xml
> features:install tesb-example-sam-osgi

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
