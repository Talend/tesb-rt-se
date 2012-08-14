###################################################################################
# Copyright (C) 2011 - 2012 Talend Inc. - www.talend.com
# This file is part of Talend ESB

# Talend ESB is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as published by
# the Free Software Foundation.
#
# Talend ESB is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.

# You should have received a copy of the GNU General Public License
# along with Talend ESB.  If not, see <http://www.gnu.org/licenses/>.
###################################################################################

Welcome to Talend ESB Adapters!
=====================================
This package contains plugins folder with content,
related to HypericHQ plugins and Nagios templates for TESB monitoring.

HypericHQ plugins
=========================================
hyperic folder contains two plugins:
- camel-plugin.xml
     camel-plugin.xml can be used to monitor Camel routes in Tomcat and TESB container by HypericHQ.
- cxf-plugin.xml
     cxf-plugin.xml can be used to monitor CXF services in Tomcat and TESB container by HypericHQ.

Plugins need to be deployed to HypericHQ Agent and HypericHQ Server.
Copy plugins to such folders:
<HypericServer>/hq-engine/hq-server/webapps/ROOT/WEB-INF/hq-plugins
<HypericAgent>/bundles/agent-<version>/pdk/plugins

and then run HypericHQ Server and HypericHQ Agent.

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