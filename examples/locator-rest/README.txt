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

Example for REST Service Locator enabled
============================================
This example illustrates the usage of Locator Feature for REST Service

This example consists of the following components:

service/
	- Order service which is registered with the Locator Feature after the starting.
	
common/   
	- This directory contains the code that is common to both the client and the Order server. 
	
client/
	- This is a sample client application that uses the Locator Feature to dynamically discover service endpoint and invokes the service.


Prerequisite
---------------------------------------
To build and run this example, you must install the J2SE Development Kit (JDK) 5.0 or above.

The Service Locator Server (zookeeper) should be running.


Building the Demo
---------------------------------------
Using either Linux or Windows:

    mvn install

Starting the Service
---------------------------------------
  * Add maven URL into karaf:
features:addurl mvn:org.talend.esb.examples.locator-rest/features/5.0.0/xml

  * Install example feature in container:
features:install tesb-locator-rest

Running the Client
---------------------------------------
  * From the command line:
     cd client ; mvn exec:java
