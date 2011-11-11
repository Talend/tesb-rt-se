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

Example for Locator SOAP Service 
============================================
This example illustrates the usage of the Locator SOAP Service

This example consists of the following components:

service/
   - Greeter service which is registered with the locator soap service after the starting.
	
common/   
   - This directory contains the code that is common to both the client and the Greeter server. 
	
client/
   - This is a sample client application that uses the Locator Soap Service to dynamically discover the service endpoint and invoke the service.


Starting the Demo

- Start zookeeper in container
    features:install tesb-zookeeper-server
    
- Start locator soap service in container
	features:install tesb-locator-soap-service

- Start service on tomcat

- Start client (from the command line cd client; mvn exec:java)
