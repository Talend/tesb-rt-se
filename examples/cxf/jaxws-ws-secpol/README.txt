WS-SecurityPolicy Demo
==================================================

This sample demonstrates a CXF SOAP client invoking a CXF Web Service
provider (WSP), where the services are secured using WS-SecurityPolicy. In the
first invocation, authentication is performed via a wsse UsernameToken, which
is secured using a SymmetricBinding policy. In the second invocation, an
AsymmetricBinding policy is used to secure the message exchange, where a SAML
Assertion is also required by the WSP. 


Important Note:  By default, this example uses strong encryption which is 
recommended for use in production systems.  To run this example "out of the
box", you MUST have the "Java(TM) Cryptography Extension (JCE) Unlimited 
Strength Jurisdiction Policy Files" installed into your JRE.  See your
JRE provider for more information.   (For Oracle JDK6, the download is available here:
http://www.oracle.com/technetwork/java/javase/downloads/index.html, see the README
file from the download for installation instructions.)   

Alternatively, you can change to using a lower end encyption algorithm 
by editing the security policies in:
common/src/main/resources/ws-secpol-wsdl/greeter.wsdl

to change from "Basic256" to "Basic128". If you receive an error like 
"Illegal key length" when running the demo, you need to change to Basic128 or
install the Unlimited Strength encryption libraries.


This sample consists of 3 parts:
common/   - This directory contains the code that is common
            for both the client and the server.  It contains
	    the WSDL and the artifacts that are generated 
	    from that WSDL.  The wsdl contains the 
	    WS-SecurityPolicy descriptions that is used to
	    secure the messages. It also contains the certs and
	    properties files used for the encryption.

service/  - This is the service.   

client/   - This is a sample client application that uses
            the JAX-WS API's to create a proxy client and
	    makes several calls with it.


Usage
===============================================================================
Note: Please follow the parent README.txt first for common build and container
setup instructions.


Building the Demo
---------------------------------------
  
Using either Linux or Windows:

    mvn install



Starting the Service
---------------------------------------
  * From the command line:
     cd service ; mvn exec:java

  * From within the OSGi container:
     karaf@tsf> features:install tsf-example-jaxws-ws-secpol-server     

   (Make sure you've first installed the examples features repository as described in the
   parent README.)


Running the Client
---------------------------------------
  * From the command line:
     cd client ; mvn exec:java

  * From within the OSGi container:
     karaf@tsf> features:install tsf-example-jaxws-ws-secpol-client


Cleaning up
---------------------------------------
To remove the code generated from the WSDL file and the .class
files, run "mvn clean".



