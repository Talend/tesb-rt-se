###############################################################################
#
# Copyright (c) 2011 - 2013 Talend Inc. - www.talend.com
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
related to HypericHQ plugins and Nagios templates for TESB monitoring.

HypericHQ plugins
=========================================
hyperic_plugins folder contains four plugins:
- camel-plugin.jar
     camel-plugin jar can be used to monitor Camel routes in Tomcat and TESB container by HypericHQ.
- cxf-plugin.jar
     cxf-plugin jar can be used to monitor CXF services in Tomcat and TESB container by HypericHQ.
- activemq-plugin.jar
     activemq-plugin can be used to monitor ActiveMQ resources by HypericHQ.
- trun-plugin.jar
     trun-plugin can be used to monitor Talend ESB Runtime container resources by HypericHQ.

Plugins need to be deployed to HypericHQ Agent and HypericHQ Server.
For Hyperic HQ version 4.6 or above (including 5.X) use Plugin Manager in Hyperic HQ Console
(Administration -> HQ Server Settings -> Plugin manager).
For older versions of Hyperic, use manual deployment - copy plugins to such folders:
<HypericServer>/hq-engine/hq-server/webapps/ROOT/WEB-INF/hq-plugins
<HypericAgent>/bundles/agent-<version>/pdk/plugins

and then run HypericHQ Server and HypericHQ Agent.

Building issues
=========================================
- Build should be started from 'tesb-rt-se' folder (the folder with the root pom file). Starting build from any other
  folder can be partially successful, but the main target eventually fails.
- Three dependencies from Hyperic Server should be added to local Maven repository manually:
    - hq-common-4.6.6.jar
    - hq-pdk-4.6.6.jar
    - hq-util-4.6.6.jar

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
