JAX-RS JMS HTTP Demo 
===============================================

This sample demonstrates how a JAX-RS HTTP server can be enhanced to receive JMS messages

This sample consists of 4 parts:
common/   - This directory contains the code that is common
            for both the client and the server.  It contains
	    a single Book class.

service/  - This is the service.   At startup, it will
            connect to the JMS broker.

client/   - This is a sample client application that uses
            the CXF JAX-RS API and pure JMS API's

war/      - This is a war achive.

This demo uses ActiveMQ as the JMS implementation for 
illustration purposes only. 

Usage
===============================================================================
Note: Please follow the parent README.txt first for common build and container 
setup instructions.


Building the Demo
---------------------------------------
Using either Linux or Windows:
    mvn install


Running the JMS Broker
---------------------------------------
The sample requires a JMS broker to be running.  There are two
ways to get a JMS broker running:

* From the command line
     In separate command windows/shells:
     mvn -Pjms.broker

* From within the Talend Service Factory OSGi container:
     From the OSGi command line, run:
         activemq:create-broker 
     That will create a new broker broker with the defaults and 
     will then start it.


Starting the service
---------------------------------------
* In the servlet container
   cd war; mvn jetty:run

* From within the Talend Service Factory OSGi container
   karaf@tsf> features:install tsf-example-jaxrs-jms-http

   (Make sure you've first installed the examples features repository as described in the
   parent README.)


Running the Client
---------------------------------------
  * From the command line:
     cd client ; mvn exec:java

Note that the client will do both HTTP and JMS based invocations.

By default, the HTTP client will use the http port 8080 for constructing the URIs.
This port value is set during the build in the client.properties resource file. If the server 
is listening on the alternative port then you can use an 'http.port' system property during the build:
   
- mvn install -Dhttp.port=8181

Demo Description
---------------------------------------

This demo demonstrates how JAX-RS HTTP servers can be enhanced to get JMS messages.

JAX-RS is the HTTP centric specification and it is likely developers will be starting from
working upon JAX-RS HTTP servers. However given the popularity of JMS the new requirements for the the existing
JAX-RS servers receive messages over JMS will likely need to be met.

This demo intentionally uses a war archive to emulate the possible development process where a JAX-RS HTTP server
is created first and at the next stage a CXF JAX-RS JMS endpoint is created with the same service instance being shared
between the two endpoints.

The demo demonstrates how both CXF JAX-RS WebClients and pure JMS clients can interact with HTTP and JMS endpoints.
It also shows how oneway HTTP requests can be further routed to JMS destinations.

Note that :

 - a JMS consumer in the JMSHttpClient sets "org.apache.cxf.request.uri" and "org.apache.cxf.request.method"
 JMS properties to get a specific JAX-RS method being invoked on the server. These properties are optional, 
 default values are "/" and "POST" respectively. For example, JMSHttpClient.addGetBookOverJMS invocation which
 adds a new Book does not set these properties and thus JMSHttpBookStore.addBook() method will be invoked.
 
 - JMS JAX-RS Server will also default to "text/xml" for Content-Type and Accept values.
 JMS Consumers can override it by setting "Content-Type" and "Accept" properties. 
 
 - JMSHttpBookStore.oneWayRequest() uses a org.apache.cxf.jaxrs.ext.Oneway annotation.
 JMSHttpClient.addOnewayOverHttpGetOverJMS uses CXF JAX-RS WebClient to post a new Book oneway 
 and eventually get it echoed back via JMS.  
 
 - JMSHttpBookStore uses a CXF JAX-RS extension ProtocolHeaders which can be used to get to the headers
 available either in the current HTTP or JMS request.    
   
