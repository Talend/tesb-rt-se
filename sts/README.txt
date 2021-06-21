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
Security Token Service
----------------------

Security Token Services are defined formally within the OASIS WS-Trust specification. They help immensely in
decoupling authentication and authorization maintenance from the web service clients and providers that need that
information. Using the STS eliminates the need for the Web Service Provider (WSP) and Web Service Clients (WSC)
to have a direct trust relationship, freeing WSPs from needing to maintain WSC UsernameToken passwords or X509
certificates from acceptable clients. Instead, it is just necessary for the WSP to trust the STS and for the STS
to be able to validate the WSC's credentials prior to making the STS call.

For more information, please find in STSUserGuide document.

Sub modules
-----------
sts-config:
  STS service bundle which can be deployed into TESB container out of the box.

sts-war:
  STS service war file which can be deployed into Tomcat.


