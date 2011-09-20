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

Example for REST Service Locator Proxy

============================================
This example illustrates the usage of REST Locator Proxy methods.
 
 example of client that uses proxy
 client/
 -one service endpoint is registered to Service Locator.
 -lookup the registered endpoint
 -unregister endpoint from Service locator
 -lookup if the endpoint still registered.
 
 example of client that uses WebClient
 webclient/
 -register first endpoint for the service with systemTimeout=200
 -register second endpoint for the service with systemTimeout=400
 -lookup endpoints
 -lookup endpoint with systemTimeout=200
 -unregister first endpoint
 -unregister second endpoint
 -lookup endpoint 
 
This example consists of the following components:

client/
	- This is a sample client application that uses REST Locator proxy to dynamically lookup/register/unregister service endpoints.

webclient/
	- This is a sample client application that uses REST Locator proxy to dynamically lookup/register/unregister service endpoints.
soapui/   
	- This directory contains soapUI project that allows to invoke methods of REST Locator Proxy. 
	
Prerequisite
---------------------------------------
To run this example successfully, Karaf  should be running. 
you must install the J2SE Development Kit (JDK) 5.0 or above.

The Service Locator Server (zookeeper) should be running in any mode.

Executing a sample
---------------------------------------
1) Run a command in TESB container:  
features:install  tesb-locator-rest-proxy
2) Change directory to client
cd client or webclient
3) Run maven execute plugin
mvn exec:java

In console you will see the output of the example.