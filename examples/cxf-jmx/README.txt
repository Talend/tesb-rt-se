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
CXF-JMX example
============================================
cxf-jmx example illustrate how to enable CXF for
JMX (For war file, deployed in Tomcat, and jar OSGI bundle, deployed in TESB OSGi container).
Examples provide sayHi and doubleIt web methods.
Additionally, after deploying this samples you can see CXF MBeans and their Attributes
(actually attributes are the metrics which we will monitor with help of HypericHQ), 
that can be monitored using jconsole.

Enable CXF samples for JMX
============================================
To enable CXF for JMX two beans are added to Spring context

<bean id="org.apache.cxf.management.InstrumentationManager"
		class="org.apache.cxf.management.jmx.InstrumentationManagerImpl">
		<property name="bus" ref="cxf" />
		<property name="usePlatformMBeanServer" value="true" />
		<property name="enabled" value="true" />
</bean>
	
<bean id="CounterRepository" class="org.apache.cxf.management.counters.CounterRepository">
		<property name="bus" ref="cxf" />
</bean>

Creating CXF MBeans for monitoring Attributes
============================================
It is important to make the first invocation of the deployed CXF services
, using WebService clients. Only after this step MBeans with Attributes will be created for CXF.
If you don`t make the invocation of CXF service, you won`t see CXF MBeans 
and their Attributes, as they won`t be created. 
(Using SimpleClient the first invocation can be done )


To build and run this example, you must install the J2SE Development Kit (JDK) 5.0 or above.

Building the cxf-jmx
============================================
This sample consists of 4 parts:
common/   - This directory contains the code that is common
            for both the client and the server. 
            
service/  - This is the CXF service packaged as an OSGi bundle.
             
war/      - This module creates a WAR archive containing the code from common and service modules.   

client/   - This is a sample client application that uses
            the CXF JAX-WS API to create client and makes several calls with it.

From the base directory of this sample (i.e., where this README file is
located), the maven pom.xml file can be used to build and run the demo. 

Using either UNIX or Windows:

    mvn clean install

Running this command will build the demo and create a WAR archive and an OSGi bundle 
for deploying the service either to servlet or OSGi containers.

Starting the service
============================================
To enable Tomcat for jmx:
for Windows:
open command prompt and set temporary environment variable CATALINA_OPTS with command:
set CATALINA_OPTS=-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=6969 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false

for Linux:
export CATALINA_OPTS="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=6969 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"

* In servlet container (Tomcat):
1) Copy war file from the cxf-jmx/war/target folder to webapp folder in Tomcat.
2) Start Tomcat (use the same command prompt to start tomcat)
3) You can find wsdl at http://localhost:8080/simpleService/simpleService?wsdl

* In Talend ESB OSGi container:
1) Start TESB container.
2) Type command in TESB container: 		
features:addurl mvn:org.talend.esb.examples/cxf-jmx-feature/5.0-SNAPSHOT/xml
4) Type command in TESB container
features:install cxf-jmx-service
5) You can find wsdl at http://localhost:8040/services/simpleService?wsdl

Running the client
============================================
* For TESB container:
    From cxf-jmx folder run:
    mvn exec:java -pl client
	
Build will fail, but this is expected behavior to see how Hyperic will show exception.
You will see:
[ERROR] Failed to execute goal org.codehaus.mojo:exec-maven-plugin:1.2:java (default-cli)
on project simple-service-bundle: An exception occured while executing the Java class. null:
InvocationTargetException: Incorrect name
Also you'll see exception in TESB container window.

*For servlet container:
    From cxf-jmx folder run:
	mvn exec:java -pl client -Pwar

Build will fail, but this is expected behavior to see how Hyperic will show exception.
You will see:
[ERROR] Failed to execute goal org.codehaus.mojo:exec-maven-plugin:1.2:java (default-cli)
on project simple-service-bundle: An exception occured while executing the Java class. null:
InvocationTargetException: Incorrect name
Also you'll see exception in Tomcat window.

Using jconsole to find MBean Attributes
============================================
* Tomcat:
1) run jconsole
2) put service:jmx:rmi:///jndi/rmi://localhost:6969/jmxrmi into Remote Process field.
3) connect
4) choose Mbean Tab
5) find org.apache.cxf
6) If the first invocation of the service is done, you can find Performance folder, 
where CXF MBeans with Attributes can be found

*Talend ESB OSGi container:
1) run jconsole
2) put service:jmx:rmi://localhost:44444/jndi/rmi://localhost:1099/karaf-tesb into Remote Process field.
Username: karaf Password: karaf
3) connect
4) choose Mbean Tab
5) find org.apache.cxf
6) If the first invocation of the service is done, you can find Performance folder, 
where CXF MBeans with Attributes can be found.