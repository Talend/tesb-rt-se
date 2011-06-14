###############################################################################
#
# Copyright (c) 2011 Talend Inc. - www.talend.com
# All rights reserved.
#
# This program and the accompanying materials are made available
# under the terms of the Apache License v2.0
# which accompanies this distribution, and is available at
# http://www.apache.org/licenses/LICENSE-2.0
#
###############################################################################
Example for Service Locator
============================================
This example illustrates the usage of Service locator for CXF based participants.
Service Locator is a technical service which provides service consumers with a mechanism to 
discover service endpoints at runtime, thus isolating consumers from the knowledge about the 
physical location of the endpoint. Additionally, it allows service providers to automatically 
register and unregister their service endpoints. 
In this way, the providers actively advertise the availability of their service endpoints to consumers. 

Prerequisite
------------

To build and run this example, you must install the J2SE Development Kit (JDK) 5.0 or above.
A servlet container is Tomcat 5.5 or above.


The Service Locator server should be running.

1)
To start the Service Locator you need to provide a configuration file.
Create the new config file for a standalone Service Locator: 
talend-esb-<version>/zookeeper/conf/zoo.cfg with the following content:

tickTime=2000 
dataDir=./var/locator 
clientPort=2181

2)
Change the current directory to talend-esb-<version> and create a data directory for the locator

Linux: 
mkdir var; 
mkdir var/locator

Windows:
md var\locator 

3)
Under Linux, ensure execution rights for the locator startup scripts:

chmod a+x zookeeper/bin/*.sh

Now, the Service Locator server can be started and stopped with the scripts from the zookeeper/bin directory. 
From directory talend-esb-<version>, the Locator can be started with the following command: 

Linux:
 ./zookeeper/bin/zkServer.sh start 

Windows: 
.\zookeeper\bin\zkServer.cmd


Following command can be used to stop the Locator server:
Linux:
./zookeeper/bin/zkServer.sh stop 

Windows:
by pressing Ctrl-C


Building the Demo
---------------------------------------

This sample consists of 3 parts:
common/   - This directory contains the code that is common
            to both the client and the server. 
            
service/  - This is a JAX-WS service enabled for Service Locator and packaged as an OSGi bundle.
             
war/      - This module creates a WAR archive for the JAX-WS service enabled for Service Locator.   

client/   - This is a sample client application that uses
            the Locator server to dynamically discover service endpoint and invokes the service.


From the base directory of this sample (i.e., where this README file is
located), the maven pom.xml file can be used to build and run the demo. 


Using either UNIX or Windows:

    mvn install

Running this command will build the demo and create a WAR archive and an OSGi bundle 
for deploying the service either to servlet or OSGi containers.

Usage
===============================================================================


Starting the service
---------------------------------------
 * In the servlet container

    cd war; mvn jetty:run

 * From within the Talend Service Factory OSGi container:

 * From the OSGi command line, run:
    karaf@tsf> features:install tsf-example-jaxrs-attachments

   (Make sure you've first installed the examples features repository as described in the
   parent README.)

    
Running the client
---------------------------------------
 
* From the command line
   - cd client
   - mvn exec:java

By default, the client will use the http port 8080 for constructing the URIs.
This port value is set during the build in the client.properties resource file. If the server is listening on an alternative port then you can use an 'http.port' system property during the build :
   
- mvn install -Dhttp.port=8181

