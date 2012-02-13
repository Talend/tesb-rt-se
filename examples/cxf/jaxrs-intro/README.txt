JAX-RS Intro Example 
===========================

The demo lists the Persons who are part of a membership.  It shows basic features of the JAX-RS 1.1 specification[1] and API[2]:

- JAX-RS root resource class (Membership)
- JAX-RS SubResources (Person under Membership)
- GET (to retrieve a single Member and all Members), PUT (for updates - both single-field and multiple-field), POST (for inserts). 

[1] http://jcp.org/aboutJava/communityprocess/mrel/jsr311/index.html
[2] https://jsr311.dev.java.net/nonav/releases/1.1/index.html

Building the Demo
---------------------------------------

This sample consists of 3 parts:
common/   - This directory contains the code that is common
            for both the client and the server. 
            
service/  - This is the JAX-RS service holding the Membership root resources packaged as an OSGi bundle.
             
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
Note: Please follow the parent README.txt first for common build and container setup instructions.

Starting the service
---------------------------------------
 * In the servlet container

    cd war; mvn jetty:run

 * From within the Talend Service Factory OSGi container:
   karaf@tsf> features:install tsf-example-jaxrs-intro

   (Make sure you've first installed the examples features repository as described in the
   parent README.)


Running the client
---------------------------------------
 
* From the command line
   - cd client
   - mvn exec:java


Demo Description
----------------

The JAX-RS Server provides one service via the registration of a root resource class, 
MembershipService which relies on within-memory data storage.

MembershipService provides a list of its members, individual Person objects containing name and age.

New persons can be added to the MembershipService, and individual members can have their information updated.

The RESTful client uses CXF JAX-RS WebClient to traverse all the information about an individual Person and also add a new child.

Please check the comments in the code for a detailed description on how client calls are made and 
how they are processed on the server side.

