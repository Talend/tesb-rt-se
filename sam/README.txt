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
Service Activity Monitoring
---------------------------

Supports monitoring and central collection of service requests and responses on client and server side

Subprojects
-----------
sam-common: 
  Currently contains shared code between Agent and Server. 
  
sam-agent: 
  Runs together with CXF on service client and provider. The monitoring events are processed asynchronously to the main message flow. Filters and
  Handlers allow to decide which messages and what parts are monitored. The monitoring event will then be sent to the monitoring service.

sam-server: 
  Receives monitoring events and stores them into a database.

sam-server-war: 
  The sam-server war package which can be deployed into Servlet container.
derby-all:
  The new derby-all bundle which ONLY can be used to installed on OSGI container as dependency bundle of derby-starter.
derby-starter: 
  The derby-starter bundle which ONLY can be used to installed on OSGI container to start Derby Database server.