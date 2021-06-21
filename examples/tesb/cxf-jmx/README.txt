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

CXF-JMX example
============================================
The cxf-jmx example illustrates how to enable CXF for JMX for web service
providers contained either within a war file deployed in Tomcat or an OSGI bundle
deployed in the TESB OSGi container.

The web service provides simple sayHi and doubleIt operations.

After deploying the samples you can see CXF MBeans and their attributes that
can be monitored using the Sun JDK's JConsole.  Attributes also form the metrics
that we will monitor with help of HypericHQ.

See also:
https://cwiki.apache.org/CXF20DOC/jmx-management.html
http://download.oracle.com/javase/1.5.0/docs/guide/management/jconsole.html


Enabling CXF samples for JMX
============================================
To enable CXF for JMX, one bean and one feature bean need to be added to the Spring context:

<bean id="org.apache.cxf.management.InstrumentationManager"
    class="org.apache.cxf.management.jmx.InstrumentationManagerImpl">
    <property name="bus" ref="cxf" />
    <property name="usePlatformMBeanServer" value="true" />
    <property name="enabled" value="true" />
</bean>

<bean class="org.apache.cxf.metrics.MetricsFeature" />

Creating CXF MBeans for monitoring attributes
=============================================
After deploying the web service it is important to make SOAP calls against it.  Only
after doing so will CXF create MBeans with attributes for that web service.  (The SOAP
calls can easily be made with the provided client discussed below.)

To build and run this example, you must install the J2SE Development Kit (JDK) 5.0 or above.

Building the cxf-jmx examples
============================================
This sample consists of 4 parts:

service/  - This is the CXF web service provider packaged as an OSGi bundle.

client/   - This is a sample client application that uses the CXF JAX-WS API
            to create a SOAP client and make several calls with it.

common/   - This directory contains the code that is common
            for both the client and the server.

war/      - This module creates a WAR archive containing code from common and
            service modules.  Servlet container use only, not used in OSGi deployment.


Using either UNIX or Windows:

    From the example parent directories (i.e., Talend-ESB-<version>/examples/talend, Talend-ESB-<version>/examples/talend/tesb),
    run the following command to install the example parent pom files: talend-esb-examples-<version>.pom and talend-esb-examples-parent-<version>.pom into local maven repo.

    mvn install --non-recursive

    From the base directory of this sample (i.e., where this README file is located),
    the maven pom.xml file can be used to build and run the demo.

    mvn install


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
1) Copy war file from the cxf-jmx/war/target folder to webapp folder in Tomcat.
Alternatively, if your Tomcat installation is configured to work with the Tomcat Maven Plugin (http://tinyurl.com/4yxzjna)
you can also start Tomcat and then deploy the war by entering "mvn tomcat:deploy" for Tomcat 7 or "mvn tomcat:deploy -PTomcat6"
for Tomcat 6.

2) Start Tomcat

3) Be sure you can see the WSDL at http://localhost:8080/simpleService/simpleService?wsdl before
   continuing.

* In Talend ESB OSGi container:
1) Start TESB container.
2) Type this command in TESB container:
feature:repo-add mvn:org.talend.esb.examples/cxf-jmx-feature/<version>/xml
3) Type this command in TESB container:
feature:install cxf-jmx-service
4) You can find wsdl at http://localhost:8040/services/simpleService?wsdl

Running the client
============================================
* For TESB container:
    From cxf-jmx folder run:
    mvn exec:java -pl client

* For servlet container:
    From cxf-jmx folder run:
	mvn exec:java -pl client -Pwar

For either case, after making many successful SOAP calls, the execution will intentionally
fail to show how Hyperic reports exceptions.  You should see:

javax.xml.ws.soap.SOAPFaultException: Incorrect name

You'll also see this exception in the console window.

Using JConsole to find MBean Attributes
============================================
1) run JConsole: {JAVA_HOME}/bin/jconsole from a command prompt

2) If you're deploying the web service on Tomcat:
put service:jmx:rmi:///jndi/rmi://localhost:6969/jmxrmi into Remote Process field.

If you're using the TESB OSGi container:
put service:jmx:rmi://localhost:44444/jndi/rmi://localhost:1099/karaf-trun into Remote Process field,
with (default) username of "tadmin" and password of "tadmin".

3) connect
4) choose Mbean tab, and open the org.apache.cxf item in the left-side treeview
5) After SOAP calls on the web service have been done, you'll see the "Metrics.Server" folder,
where CXF MBeans with their attributes will be listed.

