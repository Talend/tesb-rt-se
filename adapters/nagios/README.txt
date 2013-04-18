###################################################################################
# Copyright (C) 2011 - 2013 Talend Inc. - www.talend.com
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

How to monitoring CXF, Camel and Activemq resource with Nagios
==============================================================

Note: The Nagios installed directory assumed to be "/usr/local/nagios", the path which 
      jmx4perl plugin installed assumed to be "/usr/local/src/jmx4perl/jmx4perl-1.04/scripts".


Install configuration template files for CXF, Camel and Activemq monitoring
===========================================================================

1. Copy configuration template files below into the folder /usr/local/nagios/etc/objects/
----------------------------------------------------------------------------------------
template/jmx_commands.cfg
template/cxf.cfg
template/camel.cfg
template/activemq.cfg

2. Define Macros in the /usr/local/nagios/etc/resource.cfg which will be used by jmx_commands.cfg
-------------------------------------------------------------------------------------------------
# set the path which jmx4perl plugin installed
$USER5$=/usr/local/src/jmx4perl/scripts
# set the path to where to find configuration files
$USER6$=/usr/local/nagios/etc/objects

3. Add one line below (for command configuration file) to the /usr/local/nagios/etc/nagios.cfg
----------------------------------------------------------------------------------------------
cfg_file=/usr/local/nagios/etc/objects/jmx_commands.cfg



Monitoring CXF resource with cxf-jmx example
============================================

On TESB container side:

1. Build the cxf_jmx example
----------------------------
  cd examples/talend/tesb/cxf-jmx
  mvn clean install

2. Install cxf_jmx example feature to TESB container
----------------------------------------------------
  features:addurl mvn:org.talend.esb.examples/cxf-jmx-feature/<version>/xml
  features:install cxf-jmx-service

3. Run the client of cxf_jmx example
------------------------------------
  cd examples/talend/tesb/cxf-jmx
  mvn exec:java -pl client

On Nagios server side:

1. Copy sample/cxf_host.cfg into the folder /usr/local/nagios/etc/objects/

2. Add one line below (for monitoring CXF) to the /usr/local/nagios/etc/nagios.cfg
----------------------------------------------------------------------------------
cfg_file=/usr/local/nagios/etc/objects/cxf_host.cfg

3. Restart Nagios service
-------------------------
# service nagios restart

Then, the sample resource can be monitored from Nagios web UI.



Monitoring Camel,ActiveMQ resource with camel-jmx example
=========================================================

On TESB container side:

1. Build the camel_jmx example
----------------------------
  cd examples/talend/tesb/camel-jmx
  mvn clean install

2. Install camel_jmx example feature to TESB container
------------------------------------------------------
  features:addurl mvn:org.talend.esb.examples/camel-jmx-feature/<version>/xml
  features:install camel-jmx-service

On Nagios server side:

1. Copy the sample host files into the folder /usr/local/nagios/etc/objects/
----------------------------------------------------------------------------
sample/camel_host.cfg
sample/activemq_host.cfg

2. Add two line below (for monitoring Camel,Activemq) to the /usr/local/nagios/etc/nagios.cfg
---------------------------------------------------------------------------------------------
cfg_file=/usr/local/nagios/etc/objects/camel_host.cfg
cfg_file=/usr/local/nagios/etc/objects/activemq_host.cfg

3. Restart Nagios service
-------------------------
# service nagios restart

Then, the sample resource can be monitored from Nagios web UI.



Monitoring your own resource of CXF,Camel and Activemq
======================================================

Please find more information from SystemManagementGuide document.

