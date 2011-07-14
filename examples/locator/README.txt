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
This example illustrates the usage of Service Locator for CXF based participants.
Service Locator is a technical service which provides service consumers with a mechanism to 
discover service endpoints at runtime, thus isolating consumers from the knowledge about the 
physical location of the endpoint. Additionally, it allows service providers to automatically 
register and unregister their service endpoints. 
In this way, the providers actively advertise the availability of their service endpoints to consumers. 

Prerequisite
------------

To build and run this example, you must install the J2SE Development Kit (JDK) 5.0 or above.


The Service Locator Server (zookeeper) should be running.

1)
To start the Service Locator Server (zookeeper) you need to provide a configuration file.
Create the new config file for a standalone Service Locator Server (zookeeper): 
talend-esb-<version>/zookeeper/conf/zoo.cfg with the following content:

tickTime=2000 
dataDir=./var/locator 
clientPort=2181

2)
Under Linux, ensure execution rights for the locator startup scripts:

chmod a+x zookeeper/bin/*.sh

Now, the Service Locator server can be started and stopped with the scripts from the zookeeper/bin directory. 
From directory talend-esb-<version>, the Locator can be started with the following command: 

Linux:
 ./zookeeper/bin/zkServer.sh start 

Windows: 
.\zookeeper\bin\zkServer.cmd start


Following command can be used to stop the Locator server:
Linux:
./zookeeper/bin/zkServer.sh stop 

Windows:
.\zookeeper\bin\zkServer.cmd stop


Building the Demo
-----------------

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

Running the Demo
----------------
	

Starting the Demo service

 * starting Demo service in the embeded servlet container (Jetty):

    cd war; mvn jetty:run

 * starting Demo service in the TESB OSGi container:
 
    cd talend-esb-<version>/container/bin
	Linux: ./tesb
	Windows: tesb.bat
	
	then enter the following command in the console:
    karaf@tesb> features:install tesb-locator-client
    karaf@tesb> install -s file:///directory talend-esb-<version>/examples/talend/tesb/locator/common/target/locator-demo-common-5.0-SNAPSHOT.jar
    karaf@tesb> install -s file:///directory talend-esb-<version>/examples/talend/tesb/locator/service/target/locator-demo-service-5.0-SNAPSHOT.jar
    
    
Running the Demo client
 
* From the command line

   cd client; mvn exec:java


