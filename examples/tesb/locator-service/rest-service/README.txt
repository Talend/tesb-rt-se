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

Example for Locator REST Service

============================================
This example illustrates the usage of Locator REST Service methods.

 example of client that uses REST Service
 client/
 -first service endpoint is registered to Service Locator.
 -second service endpoint is registered to Service Locator.
 -third service endpoint is registered to Service Locator.
 -lookup for registered endpoints
 -lookup for random registered endpoint.
 -lookup for random registered endpoint.
 -lookup for random registered endpoint.
 -unregister first endpoint from Service locator.
 -unregister second endpoint from Service locator.
 -unregister third endpoint from Service locator.
 -lookup if the endpoints still registered.

 example of client that uses WebClient
 webclient/
 -register first endpoint for the service with systemTimeout=200
 -register second endpoint for the service with systemTimeout=400
 -lookup endpoints
 -lookup endpoint with systemTimeout=200
 -lookup endpoint with systemTimeout=400
 -unregister first endpoint
 -unregister second endpoint
 -lookup endpoint

This example consists of the following components:

client/
	- This is a sample client application that uses the Locator REST Service to dynamically lookup/register/unregister service endpoints.

webclient/
	- This is a sample webclient application that uses the Locator REST Service to dynamically lookup/register/unregister service endpoints.

soapui/
	- This directory contains soapUI project that allows for easily invoking methods of the Locator REST Service.

Prerequisite
---------------------------------------
To run this example successfully, Karaf  should be running.
you must install the J2SE Development Kit (JDK) 6.0 or above.

The Service Locator Server (zookeeper) should be running in any mode.

Building the Demo
---------------------------------------
Using either Linux or Windows:

    mvn install

Starting the Locator REST Service
---------------------------------------
   * Run a command in TESB container:
feature:install tesb-locator-rest-service

Executing client application
---------------------------------------
   * From the command line:
cd client; mvn exec:java
In the console you will see the output of the example.

Executing webclient application
---------------------------------------
   * From the command line:
cd webclient; mvn exec:java
In the console you will see the output of the example.
