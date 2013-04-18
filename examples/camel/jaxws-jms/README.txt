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
Example for publishing CXF services using Camel JMS
===============================================================================

This example shows how to publish and call a CXF service using SOAP/JMS. While this can also be done
using pure CXF the example shows how to do this using Camel as a CXF transport with a Camel
route defining the transport. 

The advantage of this method over a pure CXF approach is that you can easily do Camel transformations 
and routings using Camel routes. Camel also offers other CXF integrations. The Camel transport for 
CXF was chosen here as it offers the easiest integration for an existing CXF endpoint as you only need
to switch the transport and can leave the rest of the CXF configuration as-is.

This architecture features a standalone JMS broker handling the routing of the SOAP requests and responses.
The service provider is hosted on the TESB container, while the SOAP over JMS client is a standalone
Java application.


Usage
===============================================================================

Note: Please follow the parent README.txt first for common build and container setup instructions.

1) Building the Demo
-------------------------------------------------------------------------------
  
Using either UNIX or Windows, from the command line, run:
> mvn install


2) Running the JMS Broker
-------------------------------------------------------------------------------

The sample requires a JMS broker to be running:

  * From the command line, run:
> mvn -Pjms.broker

  That will create a new broker (using the default configuration) and will start it.

Alternatively, you can start a broker from within the TESB OSGi container, see below for the instructions.

3) Starting the Service
-------------------------------------------------------------------------------

3.1) Standalone
  * From the command line, run:
> cd server ; mvn exec:java

3.2) In Jetty
  * From the command line, run:
> cd war ; mvn jetty:run

3.3) From within the TESB OSGi container command line, run:

Start the broker if not already started:

karaf@trun> features:install activemq-spring
karaf@trun> activemq:create-broker

Install and start the demo server bundle:
karaf@trun> features:install talend-camel-example-jaxws-jms 

(Make sure you've first installed the examples features repository as described in the
parent README.)


4) Running the Client
-------------------------------------------------------------------------------

  * From the command line:
     cd client ; mvn exec:java


5) Cleaning up
-------------------------------------------------------------------------------
  * Press ^C in the server and broker windows to stop the running processes
  * To remove the code generated from the WSDL file and the .class files, run:
> mvn clean


Known Issues
===============================================================================

You will see the exception:
java.lang.NoClassDefFoundError: javax/ws/rs/core/Response

This is a known issue in camel where a converter for jaxrs is loaded even if no jaxrs is in use. It can be ignored.

