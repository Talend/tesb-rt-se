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
Examples to enable CXF for JMX
============================================
simple-service-bundle and simple-service-war illustrate how to enable CXF for
JMX (For war file, deployed in Tomcat, and jar OSGI bundle, deployed in TESB container).
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


To build and run these examples, you must install the J2SE Development Kit (JDK) 5.0 or above.

Building the simple-service-war
============================================
To enable tomcat for jmx:
 
set CATALINA_OPTS=-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=6969 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false
should be added to startup script of tomcat.

To build and deploy this example:
1) run: 	mvn clean install
2) copy war file from the target folder to webapp folder in tomcat.
3) start tomcat
4) run SimpleClient with command:
mvn exec:java -Dexec.mainClass="org.talend.esb.examples.SimpleClient". 
Build will fail, but this is expected behavior to see how Hyperic will show exception.
You will see:
[ERROR] Failed to execute goal org.codehaus.mojo:exec-maven-plugin:1.2:java (default-cli)
on project simple-service-bundle: An exception occured while executing the Java class. null:
InvocationTargetException: Incorrect name
Also you'll see exception in Tomcat window.

Using jconsole to find MBean Attributes
============================================
1) run jconsole
2) put service:jmx:rmi:///jndi/rmi://localhost:6969/jmxrmi into Remote Process field.
3) connect
4) choose Mbean Tab
5) find org.apache.cxf
6) If the first invocation of the service is done, you can find Performance folder, 
where CXF MBeans with Attributes can be found

Building the simple-service-bundle
============================================
To build and deploy this example:
1) run: 	mvn clean install
2) start TESB container
3) type command in TESB container: 		
features:addurl mvn:org.talend.esb.examples/simple-service-bundle/4.2-SNAPSHOT/xml
4) type command in TESB container
features:install simple-service-bundle
5) run SimpleClient with command: 
mvn exec:java -Dexec.mainClass="org.talend.esb.examples.SimpleClient". 
Build will fail, but this is expected behavior to see how Hyperic will show exception.
You will see:
[ERROR] Failed to execute goal org.codehaus.mojo:exec-maven-plugin:1.2:java (default-cli)
on project simple-service-bundle: An exception occured while executing the Java class. null:
InvocationTargetException: Incorrect name

Using jconsole to find MBean Attributes
============================================
1) run jconsole
2) put service:jmx:rmi://localhost:44444/jndi/rmi://localhost:1099/karaf-tesb into Remote Process field.
Username: karaf Password: karaf
3) connect
4) choose Mbean Tab
5) find org.apache.cxf
6) If the first invocation of the service is done, you can find Performance folder, 
where CXF MBeans with Attributes can be found


Examples to enable Camel for JMX
============================================
camel-example-management-karaf and camel-example-management-tomcat illustrate how to enable Camel for
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

Building the camel-example-management-tomcat
============================================
To enable tomcat for jmx:
 
set CATALINA_OPTS=-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=6969 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false
should be added to startup script of tomcat.

To build and deploy this example:
1) run: 	mvn clean install
2) copy war file from the target folder to webapp folder in tomcat.
3) start tomcat

Using jconsole to find MBean Attributes
============================================
1) run jconsole
2) put service:jmx:rmi:///jndi/rmi://localhost:6969/jmxrmi into Remote Process field.
3) connect
4) choose Mbean Tab
5) find org.apache.camel
6) in the routes folder MBeans with Attributes can be found.

Building the camel-example-management-karaf
============================================
To build and deploy this example:
1) run: 	mvn clean install
2) start TESB container
3) type command in TESB container: 		
features:addurl mvn:org.talend.esb.examples/camel-example-management-karaf/4.2-SNAPSHOT/xml
4) type command in TESB container
features:install camel-example-management-karaf

Using jconsole to find MBean Attributes
============================================
1) run jconsole
2) put service:jmx:rmi://localhost:44444/jndi/rmi://localhost:1099/karaf-tesb into Remote Process field.
Username: karaf Password: karaf
3) connect
4) choose Mbean Tab
5) find org.apache.camel
6) in the routes folder MBeans with Attributes can be found.