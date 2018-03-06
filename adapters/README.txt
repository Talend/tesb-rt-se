###############################################################################
#
# Copyright (c) 2011 - 2018 Talend Inc. - www.talend.com
# All rights reserved.
#
# This program and the accompanying materials are made available
# under the terms of the Apache License v2.0
# which accompanies this distribution, and is available at
# http://www.apache.org/licenses/LICENSE-2.0
#
###############################################################################

Welcome to Talend ESB Adapters!
=====================================
This package contains plugins folder with content,
related to Nagios templates for TESB monitoring.

Nagios configuration files
=========================================
nagios folder contains configuration template files and sample files for monitoring CXF, Camel and Activemq using Nagios.
- template/jmx_commands.cfg (Do NOT need make change)
  it's a command template file used to define the commands to monitor CXF, Camel and Activemq.
- template/cxf.cfg, template/camel.cfg, template/activemq.cfg (Do NOT need make change)
  they are template files used to define checks for CXF, Camel and Activemq metrics.
- sample/cxf_host.cfg, sample/camel_host.cfg, sample/activemq_host.cfg
  they are sample configuration files used to define host and service for monitoring CXF, Camel and Activemq using Nagios.
  You can define your own xxx_host.cfg for monitoring specific metrics and specific resources(CXF services, Camel routes, etc.).
- readme.txt
  how to use these configuration files for monitoring CXF, Camel and Activemq with Nagios.
