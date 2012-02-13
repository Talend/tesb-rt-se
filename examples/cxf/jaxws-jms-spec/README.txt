SOAP/JMS Specification Transport Demo using Document-Literal Style
==================================================================

This sample demonstrates use of the Document-Literal style 
binding over the SOAP/JMS Specification Transport.

This sample consists of 3 parts:
common/   - This directory contains the code that is common
            for both the client and the server.  It contains
	    the WSDL and the artifacts that are generated 
	    from that WSDL.  The WSDL uses the SOAP/JMS 
	    specification extensors and URL to define
	    how to connect to the broker and the various
	    QOS parameters to use.

service/  - This is the service.   At startup, it will
            connect to the broker based on the information
	    in the WSDL and register the greeter service.

client/   - This is a sample client application that uses
            the JAX-WS API's to create a proxy client and
	    makes several calls with it.



This demo uses ActiveMQ as the JMS implementation for 
illustration purposes only. 

Usage
===============================================================================
Note: Please follow the parent README.txt first for common build and container setup instructions.


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


Starting the Service
---------------------------------------
* From the command line:
     cd service ; mvn exec:java

* From within the OSGi container
   karaf@tsf> features:install tsf-example-jaxws-jms-spec-server

   (Make sure you've first installed the examples features repository as described in the
   parent README.)


Running the Client
---------------------------------------
* From the command line:
   cd client ; mvn exec:java
* From within the OSGi container:
   karaf@tsf> features:install tsf-example-jaxws-jms-spec-client



Cleaning up
---------------------------------------
To remove the code generated from the WSDL file and the .class
files, run "mvn clean".



