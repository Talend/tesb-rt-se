JAX-RS Advanced Example 
===========================

The demo shows some of the major features that the JAX-RS 1.1 specification[1] and API[2] provide:

- Multiple JAX-RS root resource classes
- Recursive JAX-RS sub-resources
- Resource methods consuming and producing data in different formats (XML and JSON)
- Various HTTP verbs in action
- How to use JAX-RS Response[2] to return status, headers and optional entities
- How to use JAX-RS UriInfo[2] and UriBuilder[2] for returning the links to newly created resources
- JAX-RS ExceptionMappers[2] for handling exceptions thrown from the application code

Additionally the HTTP Centric and Proxy-based Apache CXF JAX-RS client API is demonstrated.

[1] http://jcp.org/aboutJava/communityprocess/mrel/jsr311/index.html
[2] https://jsr311.dev.java.net/nonav/releases/1.1/index.html

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


Using either Linux or Windows:

    mvn install

Running this command will build the demo and create a WAR archive and an OSGi bundle 
for deploying the service either to servlet or OSGi containers.

Usage
===============================================================================
Note: Please follow the parent README.txt first for common build and container setup instructions.

Starting the service
---------------------------------------
 * In the servlet container

    cd war; mvn jetty:run

 * From within the Talend Service Factory OSGi container:

   Install and start the demo server bundle:
   karaf@tsf> features:install tsf-example-jaxrs-advanced-server

   (Make sure you've first installed the examples features repository as described in the
   parent README.)

 * From the command line :
   cd service; mvn -Pserver
    
Running the client
---------------------------------------
 
* From the command line
   - cd client
   - mvn exec:java
* From within the OSGi container
   karaf@tsf> features:install tsf-example-jaxrs-advanced-client

By default, the client will use the http port 8080 for constructing the URIs.
This port value is set during the build in the client.properties resource file. If the server 
is listening on an alternative port then you can use an 'http.port' system property during the build:
   
- mvn install -Dhttp.port=8181

Demo Desciption
---------------

The JAX-RS Server provides two services via the registration of multiple (two) root resource classes, 
PersonService and SearchService with both services sharing the data storage.

PersonService provides information about all the persons it knows about, about individual persons and their relatives:
- ancestors - parents, grandparents, etc.
- descendants - children, etc
- partners

Additionally it can help with adding the information about new children 
to existing persons and update the age of the current Person.

SearchService is a simple service which shares the information about Persons with the PersonService. 
It lets users search for individual people by specifying one or more names as query parameters. The interaction with 
this service also verifies that the JAX-RS server is capable of supporting 
multiple root resource classes.

Note that :

- Person class can act as either a JAXB bean or a JAX-RS sub-resource.
For example, a method such as Person.getMother() is a sub-resource locator because it delegates
to the actual resource method Person.getState() by returning a Person sub-resource instance.
Person.getState() will return a Person JAXB bean instance.

- Person class has a class-level JAX-RS Produces annotation which means that all the JAX-RS resource 
methods will inherit it unless they provide their own Produces annotation.

- PersonServiceImpl and Person return explicit collections while SearchService returns
PersonCollection wrappers.

- Person.updateAge method may throw a PersonUpdateException which will be caught by 
JAX-RS PersonExceptionMapper provider and translated into an HTTP 400 status.

- PersonApplication is a JAX-RS Application implementation and it also has an AppPath annotation.
It is used for starting a service from the command line.
 

The RESTful client uses CXF's JAX-RS WebClient to traverse all the information about an individual Person and also adds a new child.
It also shows how to use a simple proxy.

Finally a simple proxy is created and is used to make the calls.

Please check the comments in the code for a detailed description on how client calls are made and 
how they are processed on the server side. 

