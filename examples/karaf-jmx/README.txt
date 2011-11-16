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

Karaf-jmx Demo
==================================================

This sample demonstrates a client application whose resources 
can be monitored using JMX technology.  The client application uses
an MBean server connection by creating an MBean proxy, and then
accesses the Karaf MBean server.  This MBean proxy is local to the 
client, and emulates the remote Karaf MBean server.

This sample consists of 2 parts:

common/   - This directory contains the code that is common
            for clients you would like to create for remote management.

            This implementation allows you to:
            - connect to remote MBean server
            - create MBean proxies to access an MBean through a Java interface
            - add/remove repository to required instance of Karaf container
            - install/uninstall Karaf features in remote container
            - start/stop bundles in remote Karaf container
			
client/   - This is a sample client application that uses an
            implementation of the common project and demonstrates 
            all of the previously described operations


Usage
===============================================================================

Building the Demo
---------------------------------------
Using either Linux or Windows:
    mvn clean install

Configure the Client
---------------------------------------
We expect that the remote MBean server is already running for TESB
and we are able connect to it. To do this we must define the settings
on client side Spring configuration (client-beans.xml):
 - JMX service URL (by default = "service:jmx:rmi://localhost:44444/jndi/rmi://localhost:1099/karaf-trun")
 - user (by default = "tadmin")
 - password (by default = "tadmin")

The above settings are already configured in the client-beans.xml and so will
work as-is if you started the default TESB container on localhost.


Running the Client
---------------------------------------
  * From the command line:
    cd client ; mvn exec:java

