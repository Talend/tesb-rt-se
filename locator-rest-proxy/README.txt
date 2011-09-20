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

Provides REST interface and implementation for the Service Locator Server

Subprojects
-----------

locator-rest-proxy-service: 
  Provides the service endpoint to Service Locator Server. Uses WADL, types and interface from locator-proxy-common subproject.
  
  To build this project: locator-proxy and locator projects should be build
-----------

locator-proxy-common:
  locator-proxy-common is bundle which ONLY can be used to installed on OSGI container as dependency bundle for locator-rest-proxy-service.
locator-proxy-service: 
  The locator-rest-proxy-service bundle which ONLY can be used to installed on OSGI container and it requires Service Locator Server available in container.