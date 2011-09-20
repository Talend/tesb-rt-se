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

Provides a SOAP interface for the Service Locator Server

Subprojects
-----------
locator-proxy-client: 
  Contains a client for the Locator Proxy service. 
  
locator-proxy-common: 
  Contains WSDL, XML Schema types and the service interface for the Locator Proxy service.

locator-proxy-service: 
  Provides the service endpoint to Service Locator Server.

locator-proxy-common:
  The new locator-proxy-common bundle which ONLY can be used to install on an OSGi container 
as a dependency bundle of the locator-proxy-service.

locator-proxy-service: 
  The locator-proxy-service bundle which ONLY can be used to install on an OSGI container.  It 
requires the Service Locator Server to be available in the container.
