Karaf-jmx Demo
==================================================

This sample demonstrates a client application, manageing resources using 
the JMX technology. Client applicvation use MBean server connection by 
creating an MBean proxy. It implement the access to the Karaf MBean server.
This MBean proxy is local to the client, and emulates the remote Karaf MBean server.


This sample consists of 2 parts:
common/   - This directory contains the code that is common
            for clients you would like to create for remote managment.
			This implementation allows you to:
				- connect to remote MBean server
				- create MBean proxies to access an MBean through a Java interface
				- add/remove repository to required instance of Karaf container
				- install/uninstall Karaf features in remote container
				- start/stop bundles in remote Karaf container
			
client/   - This is a sample client application that uses
			implementation of common project and demonstrate 
			all of the previously described operations work


Usage
===============================================================================

Building the Demo
---------------------------------------
  
Using either Linux or Windows:

    mvn install

Configure the Client
---------------------------------------
We expect that the remote MBean server is already running somewhere,
and we are able connect to it. To do this we must define the settings
on client side spring configuration:
 - JMX service URL (by default = "service:jmx:rmi://localhost:44444/jndi/rmi://localhost:1099/karaf-tesb")
 - user (by default = "karaf")
 - password (by default = "karaf")
You do not need to define this settings if you have started default TESB container on localhost.

Running the Client
---------------------------------------
  * From the command line:
     cd client ; mvn exec:java
	 
