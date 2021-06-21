###############################################################################
#
# Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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

Prerequisites
1.Generate SOAP and RESTful interface (using the relevant WSDL and WADL file):

       cd locator-service-common
       mvn clean install

2.Install the Service Locator server (Zookeeper Server) feature:

 In Talend ESB container execute command: feature:install tesb-zookeeper-server

Deploying of Service Locator SOAP Service.

 In Talend ESB container execute command: feature:install tesb-locator-soap-service

Deploying of Service Locator REST Service.

 In Talend ESB container execute command: feature:install tesb-locator-rest-service

Configuring authentication for Service Locator SOAP Service.

For enabling security for Locator SOAP Service open org.talend.esb.locator.service.cfg in etc directory of Talend ESB container.

Change locator.authentication property:
TOKEN - enabling username token scenario
SAML  - enabling SAML token scenario
NO    - no security

Policy configuration is located in etc directory of Talend ESB container in files:
SAML token policy     - org.talend.esb.locator.saml.policy
Username token policy - org.talend.esb.locator.token.policy
