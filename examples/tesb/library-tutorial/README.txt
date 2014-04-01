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
- web container for service
- standalone for client

Running the Example
-------------------
From the base directory of this example (i.e., where this README file is
located), the maven pom.xml file can be used to build this example. 

Using maven commands on either UNIX/Linux or Windows:
(JDK 1.6.0 and Maven 3.0.3 or later required)


a) Without Service registry:
   mvn -Pservice -Duse.service.registry=false
   mvn -Pclient -Duse.service.registry=false

b) *** This option is only applicable to the users of Talend Enterprise ESB *** 
   With Service Registry:
   1. Prepare TESB container
      - start TESB container
      - start Service Registry server: "tesb:start-registry"
      - start STS: "tesb:start-sts"
      - switch STS to use jaas (local user.properties file): "tesb:switch-sts-jaas"
      - import Library WSDL: 
        tregistry:create wsdl <resources-dir>/Library.wsdl
      - import policies:
   	 	tregistry:create ws-policy <resources-dir>/policies/saml.policy
		tregistry:create ws-policy <resources-dir>/policies/saml-ut.policy
		tregistry:create ws-policy <resources-dir>/policies/usernameToken.policy
      - import policy attachments:	 
     	tregistry:create ws-policy-attach <resources-dir>/policy-attachments/LibraryServicePolicyAttachment.policy
     	tregistry:create ws-policy-attach <resources-dir>/policy-attachments/LibraryConsumerPolicyAttachment-ut.policy
     	tregistry:create ws-policy-attach <resources-dir>/policy-attachments/LibraryConsumerPolicyAttachment-saml.policy

   2. Run service:
   mvn -Pservice -Duse.service.registry=true
   
   3. Run client:
   	 a) UserNameToken authentication:
        mvn -Pclient -Duse.service.registry=true  -Dconsumer.policy.alias=utLibraryConsumerPolicy
        
	 b) Saml Authentication:
		mvn -Pclient -Duse.service.registry=true  -Dconsumer.policy.alias=samlLibraryConsumerPolicy
  

To run client from eclipse:
copy resource "client-applicationContext.xml" from "filtered-resources" to "resources" folder
and set ${use.service.registry}, ${consumer.policy.alias} variables in this file manually

   
