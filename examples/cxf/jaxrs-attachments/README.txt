JAX-RS Attachments Example 
===========================

This demo shows how JAX-RS consumers and providers can read and write
multipart attachments. 

XOP attachments (http://www.w3.org/TR/xop10/) with the root part linking to other
parts and regular multipart attachments are supported.  

Additionally, the demo shows how a custom CXF EndpointProperty annotation can be
used to enable the XOP support for CXF JAX-RS endpoints and proxy consumers.

Building the Demo
---------------------------------------

This sample consists of 3 parts:
common/   - This directory contains the code that is common
            to both the client and the server. 
            
service/  - This is the JAX-RS service with multiple root resources packaged as an OSGi bundle.
             
war/      - This module creates a WAR archive containing the code from common and service modules.   

client/   - This is a sample client application that uses
            the CXF JAX-RS API to create HTTP-centric and proxy clients and
     	    makes several calls with them.


From the base directory of this sample (i.e., where this README file is
located), the maven pom.xml file can be used to build and run the demo. 


Using either UNIX or Windows:

    mvn install

Running this command will build the demo and create a WAR archive and an OSGi bundle 
for deploying the service either to servlet or OSGi containers.

Usage
===============================================================================
Note: Please follow the parent README.txt first for common build and container 
setup instructions.


Starting the service
---------------------------------------
 * In the servlet container

    cd war; mvn jetty:run

 * From within the Talend Service Factory OSGi container:

 * From the OSGi command line, run:
    karaf@tsf> features:install tsf-example-jaxrs-attachments

   (Make sure you've first installed the examples features repository as described in the
   parent README.)

 * From the command line :
   cd service; mvn -Pserver
    
Running the client
---------------------------------------
 
* From the command line
   - cd client
   - mvn exec:java

By default, the client will use the http port 8080 for constructing the URIs.
This port value is set during the build in the client.properties resource file. If the server is listening on an alternative port then you can use an 'http.port' system property during the build :
   
- mvn install -Dhttp.port=8181

Demo Description
----------------

The JAX-RS server has two root resource classes. One root resource is
reading and writing XOP attachments, the other one - multipart/mixed attachments.

The demo RESTClient reads and writes XOP and multipart/mixed attacments using 
CXF JAX-RS WebClient and Proxy API. 

A custom CXF EndpointProperty annotation is used to enable the XOP support 
for the JAX-RS server and the proxy consumer. The WebClient is setting
an "mtom-enabled" property explicitly.

XOP attachments are multipart/related attachments with the root XML part linking to
other parts. Usually, the JAXB provider is responsible for writing a given JAXB bean
according to the XOP packaging rules and reading the XOP payload into a JAXB bean.

Working with XOP attachments can be particularly useful when both JAX-RS and JAX-WS 
services are combined.

The demo also shows how a CXF JAX-RS MultipartBody can be used to build and read 
a multipart body with many parts in a variety of ways. MultipartBody allows to access
individual parts using the Content-ID value and access the part data via the underlying
InputStream or in a type-safe way.
