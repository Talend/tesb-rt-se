###############################################################################
#
# Copyright (c) 2011 - 2014 Talend Inc. - www.talend.com
# All rights reserved.
#
# This program and the accompanying materials are made available
# under the terms of the Apache License v2.0
# which accompanies this distribution, and is available at
# http://www.apache.org/licenses/LICENSE-2.0
#
###############################################################################

Library Tutorial OSGi Example
=================================
This is an example of OSGi applications.
This example demonstrates using of different message exchange patterns on the base of Library service.

Example contains 3 modules: 
- server    Contains code of the service provider
- client    Contains code of the service consumer
- common    Contains code common for provider and consumer      


Deployment:
- TESB container

Running the Example
-------------------
From the base directory of this example (i.e., where this README file is
located), the maven pom.xml file can be used to build this example. 

Using maven commands on either UNIX/Linux or Windows:
(JDK 1.7.0 and Maven 3.0.3 or later required)


a) Without Service Registr
   1. Build the example:
   
       mvn clean install
       
   2. Prepare TEST container
      - start TESB container
      - run following commands in container:
      
      tesb:start-aux-store
      install mvn:org.talend.esb.mep/request-callback/5.6.0-SNAPSHOT
      install mvn:org.talend.esb/transport-jms/5.6.0-SNAPSHOT
      features:chooseurl activemq 5.10.0
      features:install activemq-client
      install mvn:org.talend.esb.examples.library-service-osgi/library-common/5.6.0-SNAPSHOT
      
    3. Start service provider
       - run following commands in container:
       
       install -s mvn:org.talend.esb.examples.library-service-osgi/library-service/5.6.0-SNAPSHOT
       
    4. Start service consumer
       - run following commands in container:
       
       install -s mvn:org.talend.esb.examples.library-service-osgi/library-client/5.6.0-SNAPSHOT


b) *** This option is only applicable to the users of Talend Enterprise ESB *** 
   With Service Registry:
   
    1. Build the example:
   
       mvn -Pservice-registry
   
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
        
        tesb:start-aux-store
        install mvn:org.talend.esb.mep/request-callback/5.6.0-SNAPSHOT
        install mvn:org.talend.esb/transport-jms/5.6.0-SNAPSHOT
        features:chooseurl activemq 5.10.0
        features:install activemq-client
        install mvn:org.talend.esb.examples.library-service-osgi/library-common/5.6.0-SNAPSHOT
      
     2. Start service provider
        - run following commands in container:
       
          install -s mvn:org.talend.esb.examples.library-service-osgi/library-service/5.6.0-SNAPSHOT
       
     3. Start service consumer
        - run following commands in container:
       
          install -s mvn:org.talend.esb.examples.library-service-osgi/library-client/5.6.0-SNAPSHOT
