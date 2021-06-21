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

Camel JMX Example
============================================
The camel-jmx example illustrates how to enable Apache Camel for JMX within either
a war file deployed in Tomcat or an OSGI bundle deployed in the TESB OSGi container.

Examples are based on the camel-example-management sample provided with Camel.
This example has three routes:

    -A route that produces a file with 100 stock quotes every five seconds.
   This is done using a timer endpoint.

    -A route that uses a file consumer to read files produced from the previous route.
   This route then splits the file and extracts each stock quote and send every
   quote to a JMS queue for further processing. However to avoid exhausting the
   JMS broker Camel uses a throttler to limit how fast it sends the JMS
   messages. By default it's limited to the very low value of 10 msg/second.

    -The last route consumes stock quotes from the JMS queue and simulates
   CPU processing (by delaying 100 milliseconds). Camel then transforms the
   payload to another format before the route ends using a logger which reports
   the progress. The logger will log the progress by logging how long time it
   takes to process 100 messages.

By default, Camel routes already have JMX enabled, no special configuration is
necessary.

After deploying the samples you can see Camel MBeans and their attributes which
can be monitored using the JDK's JConsole.  Attributes also form the metrics
that we will monitor with help of HypericHQ.

See also:
http://camel.apache.org/camel-jmx.html
http://download.oracle.com/javase/1.5.0/docs/guide/management/jconsole.html

To build and run these examples, you must install the J2SE Development Kit (JDK) 5.0 or above.

Building the camel-jmx
============================================
This sample consists of 2 parts:

service/  - This is the CXF service packaged as an OSGi bundle.

war/      - This module creates a WAR archive containing the service module.
            Servlet container use only, not used in OSGi deployment.

From the base directory of this sample (i.e., where this README file is
located), the Maven pom.xml file can be used to build and run the demo.

Using either UNIX or Windows:

    mvn clean install

Running this command will build the demo and create a WAR archive and an OSGi bundle
for deploying the service either to servlet or OSGi containers.


Starting the service
============================================
To enable Tomcat for JMX:

for Windows:
open command prompt and set temporary environment variable CATALINA_OPTS with command:
set CATALINA_OPTS=-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=6969 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false

for Linux:
export CATALINA_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=6969 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"

* In servlet container (Tomcat):
1) Copy war file from the camel-jmx/war/target folder to webapp folder in Tomcat.
Alternatively, if your Tomcat installation is configured to work with the Tomcat Maven Plugin (http://tinyurl.com/4yxzjna)
you can also start Tomcat and then deploy the war by entering "mvn tomcat:deploy" for Tomcat 7 or "mvn tomcat:deploy -PTomcat6"
for Tomcat 6.

2) Start Tomcat

* In Talend ESB OSGi container:
1) Start TESB container.
2) Type command in TESB container:
feature:repo-add mvn:org.talend.esb.examples/camel-jmx-feature/<version>/xml
4) Type command in TESB container
feature:install camel-jmx-service

Using JConsole to find MBean Attributes
============================================
1) run JConsole: {JAVA_HOME}/bin/jconsole from a command prompt

2) If you're deploying the Camel route on Tomcat:
put service:jmx:rmi:///jndi/rmi://localhost:6969/jmxrmi into Remote Process field.

If you're using the TESB OSGi container:
put service:jmx:rmi://localhost:44444/jndi/rmi://localhost:1099/karaf-trun into Remote Process field,
with (default) username of "tadmin	" and password of "tadmin"

3) connect
4) choose Mbean tab, and look in the org.apache.camel and org.apache.activemq
items in the left-side treeview

