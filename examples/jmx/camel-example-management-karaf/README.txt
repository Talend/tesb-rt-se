###############################################################################
#
# Copyright (C) 2011, Talend Inc. – www.talend.com
# This file is part of Talend ESB
#
# Talend ESB is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License version 2 as published by
# the Free Software Foundation.
#
# Talend ESB is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Talend ESB.  If not, see <http://www.gnu.org/licenses/>.
#
###############################################################################


Camel JMX Management with Karaf
====================

An example which shows how to use the Camel management with Karaf.

This example based on camel sample 'camel-example-management' for more
information about camel sample please 
see http://camel.apache.org/management-example.html


It can be installed in local repository using Maven.
 
You will need to install this example first:
  mvn install

To add example in Karaf container type in Karaf console window
  features:addurl mvn:org.apache.camel/camel-example-management-karaf/2.7.1/xml

To run example in Karaf container type in Karaf console window 
  features:install camel-example-management-karaf
  
To make sure that examples is running see Karaf log in log directory
It should contains messages like:
18:39:08,889 | INFO  | sConsumer[stock] | ache.camel.processor.CamelLogger
87 | 55 - org.apache.camel.camel-core - 2.7.1 | Received: 12500 messages so far. 
Last group took: 10390 millis which is: 9,625 messages per second. average: 9,609
....

To check availability of JMX you may use jconsole
To use jconsole type
  jconsole

Set remote JMX URL service:jmx:rmi://localhost:44444/jndi/rmi://localhost:1099/karaf-tesb
by selecting remote process and click on Connect.This opens the Java
Monitoring & Management console.
 


