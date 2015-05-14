###############################################################################
#
# Copyright (c) 2011 - 2013 Talend Inc. - www.talend.com
# All rights reserved.
#
# This program and the accompanying materials are made available
# under the terms of the Apache License v2.0
# which accompanies this distribution, and is available at
# http://www.apache.org/licenses/LICENSE-2.0
#
###############################################################################
Camel Spring Security Example
===============================================================================

This example shows how to leverage Spring Security to secure camel routes in general and specifically when combining with 
CXF JAX-WS and JAX-RS endpoints. 

The example consists of a JaxWsClient that calls a service using SOAP/HTTP + basic auth and a RestClient that 
calls a service using http + basic auth. The clients do not use Spring security.

The server part runs in a servlet container or in OSGi. Spring security is configured to 
require basic auth for all requests. The credentials are hardcoded for this simple example.  A camel 
http servlet serves all requests. In the camel context there is one route from the servlet to a JAX-WS
endpoint and one route to a JAX-RS endpoint. Both endpoints authorize users on the method level using
JSR-250 annotations (@RolesAllowed).


Usage
===============================================================================

Note: Please follow the parent README.txt first for common build and container setup instructions.


1) Building the Demo
-------------------------------------------------------------------------------

The example can be built using Maven.

> mvn clean install


2) Start the server
-------------------------------------------------------------------------------

2.1) In a Jetty Container using Maven

> cd war; mvn jetty:run

2.2) In any web container

Deploy the .war file into your container. Depending on your container settings you may have to adjust the 
URL the clients use.

2.3) In the TESB container (OSGi)

> cd to the container dir

> bin/trun.bat

karaf@trun> feature:install talend-camel-example-spring-security

(Make sure you've first installed the examples features repository as described in the
parent README.)


When you do "list | grep spring-security" you should see the Spring security server reported as started.

3) Start the client
-------------------------------------------------------------------------------

> cd client

> mvn exec:java -Dexec.mainClass=client.JaxWsClient

The Client should report some service calls that were successful and some that were denied as expected.
In the end Maven should report "BUILD SUCCESSFUL"

> mvn exec:java -Dexec.mainClass=client.RESTClient

This client should also report some service calls and Maven should report "BUILD SUCCESSFUL"

Note: By default, the HTTP client will use the http port 8080 for constructing the URIs.
If the server is listening on the alternative port (e.g. 8040 for TESB container) then you should add 
an additional 'http.port' system property during start the client:
for example: 
> mvn exec:java -Dhttp.port=8040 -Dexec.mainClass=client.JaxWsClient
and
> mvn exec:java -Dhttp.port=8040 -Dexec.mainClass=client.RESTClient
