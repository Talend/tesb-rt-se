###############################################################################
#
# Copyright (c) 2011 - 2013 Talend Inc. - www.talend.com
# All rights reserved.
#
# This program and the accompanying materials are made available
# under the terms of the Apache License v2.0
# which accompanies this distribution, and is available at
# http://www.apache.org/licenses/LICENSE-2.0
#
###############################################################################

Library Tutorial Example
=================================
This is a web application example.
This example demonstrates using of different message exchange patterns on the base of Library service.

Example contains single module: library-tutorial.
Deployment:
- tomcat or jetty web container for service
- standalone for client

Running the Example
-------------------
From the base directory of this example (i.e., where this README file is
located), the maven pom.xml file can be used to build this example. 

Using maven commands on either UNIX/Linux or Windows:
(JDK 1.6.0 and Maven 3.0.3 or later required)


a) Without Service Registry:
   mvn -Pservice
   mvn -Pclient

b) *** This option is only applicable to the users of Talend Enterprise ESB *** 
   With Service Registry:
   1. Prepare TESB container
      - start TESB container
      - run following commands in container:

        tesb:start-registry
        tesb:start-sts
        tesb:start-sam
        tesb:switch-sts-jaas

        tregistry:create wsdl <sr-resources-dir>/Library.wsdl
        tregistry:create ws-policy <sr-resources-dir>/policies/ws-policy-saml.xml
        tregistry:create ws-policy <sr-resources-dir>/policies/ws-policy-sam-enabling.xml
     	tregistry:create ws-policy-attach <sr-resources-dir>/policies/ws-policy-attach-sam-enabling.xml
     	tregistry:create ws-policy-attach <sr-resources-dir>/policies/ws-policy-attach-saml.xml


   2. Run service:
   mvn -Pservice-sr
   
   3. Run client:
   mvn -Pclient-sr
  

To run client/service from eclipse:
a) Without Service Registry:
   mvn eclipse:eclipse
   Use LibraryServer.java LibraryClient.java main() methods to start service and client.
b) With Service Registry:
   mvn eclipse:eclipse -Pservice-sr
   Use LibraryServer.java LibraryClient.java main() methods to start service and client.
   
