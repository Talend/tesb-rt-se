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
Camel JMX Example
============================================
camel-jmx example illustrates how to enable Camel for
JMX (For war file, deployed in Tomcat, and jar OSGI bundle, deployed in TESB container).
Examples are based on standard Apache Camel camel-example-management.
This example have three routes:

    -A route that produces a file with 100 stock quotes every fifth second.
   This is done using a timer endpoint.

    -A route that uses a file consumer to read files produced from route 1.
   This route then splits the file and extract each stock quote and send every
   quote to a JMS queue for further processing. However to avoid exhausting the
   JMS broker Camel uses a throttler to limit how fast it send the JMS
   messages. By default its limited to the very low value of 10 msg/second.

    -The last route consumes stock quotes from the JMS queue and simulate some
   CPU processing (by delaying 100 milliseconds). Camel then transforms the
   payload to another format before the route ends using a logger which reports
   the progress. The logger will log the progress by logging how long time it
   takes to process 100 messages.

As a default, camel application doesn`t need any configuration 
to enable Camel routes for JMX.

After deploying this samples you can see Camel MBeans and their Attributes
(actually attributes are the metrics which we will monitor with help of HypericHQ), 
that can be monitored using jconsole.

To build and run these examples, you must install the J2SE Development Kit (JDK) 5.0 or above.

Building the camel-jmx
============================================
This sample consists of 2 parts:
            
service/  - This is the CXF service packaged as an OSGi bundle.
             
war/      - This module creates a WAR archive containing the code from common and service modules.   

From the base directory of this sample (i.e., where this README file is
located), the maven pom.xml file can be used to build and run the demo. 

Using either UNIX or Windows:

    mvn clean install

Running this command will build the demo and create a WAR archive and an OSGi bundle 
for deploying the service either to servlet or OSGi containers.

Starting the service
============================================
To enable Tomcat for jmx:
 
set CATALINA_OPTS=-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=6969 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false
should be added to startup script of tomcat.

* In servlet container (Tomcat):
1) Copy war file from the camel-jmx/war/target folder to webapp folder in Tomcat.
2) Start Tomcat

* In Talend ESB OSGi container:
1) Start TESB container.
2) Type command in TESB container: 		
features:addurl mvn:org.talend.esb.examples/camel-jmx-feature/5.0-SNAPSHOT/xml
4) Type command in TESB container
features:install camel-jmx-service

Using jconsole to find MBean Attributes
============================================
* Tomcat:
1) run jconsole
2) put service:jmx:rmi:///jndi/rmi://localhost:6969/jmxrmi into Remote Process field.
3) connect
4) choose Mbean Tab
5) find org.apache.camel

*Talend ESB OSGi container:
1) run jconsole
2) put service:jmx:rmi://localhost:44444/jndi/rmi://localhost:1099/karaf-tesb into Remote Process field.
Username: karaf Password: karaf
3) connect
4) choose Mbean Tab
5) find org.apache.camel