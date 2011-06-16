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

Camel Management and Apache Tomcat example
=======================================

An example which shows how to use the Camel management with Apache Tomcat.

This example based on camel sample 'camel-example-management' for more
information about camel sample please 
see http://camel.apache.org/management-example.html

It can be run using Maven.

You will need to package this example first:
  mvn package

To run the example deploy it in Apache Tomcat by copying the .war to the
deploy folder of Apache Tomcat.
After that you will see some messages from camel routing in Tomcat console
something like that:
 
2011-05-12 11:10:57,624 [Consumer[stock]] INFO  stocks                         
- Received: 5600 messages so far. Last group took: 10407 millis which is: 9,609
 messages per second. average: 9,562
2011-05-12 11:11:08,219 [Consumer[stock]] INFO  stocks
- Received: 5700 messages so far. Last group took: 10595 millis which is: 9,438
messages per second. average: 9,559

To check availability of JMX you may use jconsole
To use jconsole type
  jconsole
  
Set remote JMX URL service:jmx:rmi:///jndi/rmi://localhost:6969/jmxrmi
by selecting remote process and click on Connect.This opens the Java
Monitoring & Management console.


