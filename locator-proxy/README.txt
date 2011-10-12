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

Provides both SOAP interface and RESTful interface for the Service Locator Proxy Service

Subprojects
-----------
locator-proxy-common: 
    *   Contains WSDL file, and relvant XSD file which define SOAP interface for the Locator Proxy service.
    *   Contains WADL file, and relvant XSD file which define RESTful interface for the Locator Proxy service.

locator-soap-proxy-service: 
      Provides the SOAP Proxy Service implementation  for Service Locator Service.

locator-rest-proxy-service: 
      Provides the RESTful Proxy Service implementation  for Service Locator Service.


To enable and deploy the Proxy Services into Talend runtime, to do the following steps:

1.  Generate and deploy SOAP and RESTful interface (using relvant WSDL and WADL file) and delpoy into Talend runtime:
      
         cd locator-proxy-common
         run "mav clean install"
	 install the generated bundle "locator-proxy-service-common-5.0-SNAPSHOT.jar" into Talend runtime.

2.  Install SOAP Proxy service interface implementation and deploy into Talend runtime. 

         cd locator-soap-proxy-service
         run "mav clean install"
	 install the generated bundle "locator-proxy-service-5.0-SNAPSHOT.jar" into Talend runtime.

3.  Install RESTful Proxy service interface implementation and deploy into Talend runtime. 

         cd locator-rest-proxy-service
         run "mav clean install"
	 install the generated bundle "locator-rest-proxy-service-5.0-SNAPSHOT.jar" into Talend runtime.


Both SOAP based Service Locator Proxy service and RESTful Service Locator Proxy service require  
Service Locator server (Zookeeper Server) to be available in the Talend runtime.

