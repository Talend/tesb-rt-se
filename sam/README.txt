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
Service Activity Monitoring
---------------------------

Supports monitoring and central collection of service requests and responses on
both the client and server side.

Subprojects
-----------
sam-common:
  Currently contains shared code between the agent and server.

sam-agent:
  Runs together with CXF on both the service client and provider sides. The monitoring
  events are processed asynchronously to the main message flow. Filters and
  Handlers allow for deciding which messages and parts are monitored. Each monitoring
  event will then be sent to the monitoring service.

sam-server:
  Receives monitoring events and stores them into a database.

sam-server-war:
  The sam-server war package which can be deployed into Servlet container.

derby-starter:
  The derby-starter bundle which ONLY can be used to install on the OSGI container for starting
the Derby Database server.

datasource-service:
  There are 7 datasources provided to connect SAM server to Derby, MySQL, H2, Oracle, DB2, PostgreSQL and
  SQLServer database.

sam-service-common:
  The common codes used for SAM Service (Restful api).

sam-service-rest:
  The Restful api which can be used to retrieve Events data from SAM server.
