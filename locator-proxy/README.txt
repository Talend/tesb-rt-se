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
Service Locator Proxy Service
---------------------------

Provides both a SOAP interface and RESTful interface for the Service Locator Proxy Service

Subprojects
-----------
locator-proxy-common: 
    *   Contains WSDL and schema files which define the Locator Proxy service's SOAP interface
    *   Contains WADL and schema files which define the Locator Proxy service's RESTful interface

locator-soap-proxy-service: 
      Provides the SOAP Proxy Service implementation for Service Locator Service.

locator-rest-proxy-service: 
      Provides the RESTful Proxy Service implementation for Service Locator Service.


To enable and deploy the Proxy Services into Talend runtime, do the following steps:

1.  Generate and deploy SOAP and RESTful interface (using the relevant WSDL and WADL file) and deploy into the Talend runtime:
      
       cd locator-proxy-common
       run "mvn clean install"

    Install the generated bundle "locator-proxy-service-common-5.0-SNAPSHOT.jar" into the Talend runtime.

2.  Install SOAP Proxy service interface implementation and deploy into Talend runtime. 

         cd locator-soap-proxy-service
         run "mvn clean install"
    
    Install the generated bundle "locator-proxy-service-5.0-SNAPSHOT.jar" into Talend runtime.

3.  Install RESTful Proxy service interface implementation and deploy into Talend runtime. 

         cd locator-rest-proxy-service
         run "mvn clean install"
    
    Install the generated bundle "locator-rest-proxy-service-5.0-SNAPSHOT.jar" into Talend runtime.


Both the SOAP based and RESTful based Service Locator Proxy service require
the Service Locator server (Zookeeper Server) to be available.

