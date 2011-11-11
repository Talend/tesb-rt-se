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
Locator Service
---------------------------

Provides both a SOAP interface and RESTful interface for the Locator Service

Subprojects
-----------
locator-service-common: 
    *   Contains WSDL and schema files which define the Locator Service's SOAP interface
    *   Contains WADL and schema files which define the Locator Service's RESTful interface

locator-soap-service: 
      Provides the SOAP Service implementation for Locator Service.

locator-rest-service: 
      Provides the RESTful Service implementation for Locator Service.


To enable and deploy the Locator Services into Talend runtime, do the following steps:

1.  Generate and deploy SOAP and RESTful interface (using the relevant WSDL and WADL file) and deploy into the Talend runtime:
      
       cd locator-service-common
       mvn clean install

    Install the generated bundle "locator-service-common-5.0-SNAPSHOT.jar" into the Talend runtime.

2.  Install Locator SOAP Service interface implementation and deploy into the Talend runtime: 

         cd locator-soap-service
         mvn clean install
    
    Install the generated bundle "locator-service-5.0-SNAPSHOT.jar" into the Talend runtime.

3.  Install Locator RESTful Service interface implementation and deploy into the Talend runtime: 

         cd locator-rest-service
         mvn clean install
    
    Install the generated bundle "locator-rest-service-5.0-SNAPSHOT.jar" into the Talend runtime.


Both the SOAP based and RESTful based Locator Service require
the Service Locator server (Zookeeper Server) to be available.

