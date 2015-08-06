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
Talend Camel Examples
===============================================================================

blueprint
   Deploying Camel routes using a simple blueprint descriptor
   
claimcheck
   Splitting of a file, moving the mass data over a separate channel and resequencing the parts
   
jaxrs-jms-http
   Publishing and using the same jaxrs implementation with both http and jms

jaxws-jms
   Publishing and using a CXF JAX-WS service using SOAP/JMS

Spring-security
   Securing Camel routes and CXF endpoints using Spring security

Build
===============================================================================

The examples can be built using Maven.
> mvn clean install

Prepare the TESB Container for the examples
===============================================================================

Start TESB. From the TESB root directory:

> cd container
> bin/trun

In the TESB shell:

> feature:repo-add mvn:org.talend.camel-examples/osgi/1.0/xml/features

This command adds the features repository for the examples to the container.  (See 
http://karaf.apache.org/manual/latest-2.2.x/users-guide/provisioning.html for
more details.)  This only needs to be done once for all examples.


Getting Help
===============================================================================

If the examples don't work as expected or you have problem adapting them to your projects do not hesitate 
to ask for help.

Talend provides free support on the CXF and Camel mailing lists, IRC channels and on the Talend Forums.
Additionally you can request support from the professional services team.

Apache Support Channels (each site has links to mailing list, irc and issue tracker):
http://camel.apache.org
http://karaf.apache.org
http://cxf.apache.org


Talend Support Channels:
http://www.talendforge.org/forum/
http://www.talend.com/professional-support/support.php
http://jira.talendforge.org//browse/TESB

Please use the contact channel that is most appropriate for your problem. General problems that apply 
to the Apache projects are best placed on Apache channels. Talend product specific questions
should rather be placed on Talend channels.


Design Notes / Common Patterns in the examples
===============================================================================

Most examples use a hierarchical Maven project with a top level parent pom with several 
submodules:

- client : Client code that can typically be called using "mvn exec:java"
- common : Model objects or generated code shared between client and server
- server: Server implementation and eventually starters
- war : Packages the common and server modules into a .war archive to be deployed on a servlet container

The structure and patterns used in the examples incorporate many best and well-known practices for Maven projects. Some of them are described below for those with little or no Maven background knowledge.


Running standalone
-------------------------------------------------------------------------------

To run the standalone examples mvn exec:java is used. If there is only one Starter class then this command will be configured 
to activate that class in the Maven pom file so no extra parameters will be needed. If there are multiple starter classes then 
the class you would like to run will have to be specified using mvn exec:java -Dexec.mainClass=<fully qualified name of the starter class>.
The starter classes typically load a Spring Context. The server starters start directly from the Spring context. The client
starters retrieve the client from the context and start it.


Running in a Servlet Container
-------------------------------------------------------------------------------

The server project contains only the server implementation and the Spring context. The war project contains the web.xml and
all other web resources needed to create a complete .war archive. In the pom.xml the Maven Jetty plugin is configured so the war can
be easily deployed and started using "mvn jetty:run".

Additionally the /src/test/java contains a JettyStarter.java that can be started from Eclipse as a Java application for easy debugging.


Running in OSGi
-------------------------------------------------------------------------------

The common and server projects are packaged as OSGI bundles so they can be used either inside or outside of an OSGI container.
There is typically one Spring file that is used for non-OSGI deployments. For OSGI deployment Spring OSGI is used, requiring an 
additional Spring config file in META-INF/Spring.  This file is used to start the bundle in an OSGI environment, contains all 
beans that are OSGI specific and imports the non-OSGI Spring context.

To create the bundles the Maven bundle plugin is used. This plugin by default imports all Java packages that are imported in
Java code and exports all packages from the bundle. As some code is only referenced from Spring these packages or
bundles have to be specified in the configuration of the Maven bundle plugin. 

Generally OSGi containers remember their state, so you may wish to reset the container before subsequent runs of any example for 
which you wish to see the same behavior as the first run.  In TESB Container (Karaf) this can be easily done by deleting 
the data directory.

NOTE: While running examples using camel-cxf you may notice exceptions related to Camel loading TypeConverter(s). The exceptions
do not affect the functionality of the example and can be safely ignored. This issue will also be fixed at Apache in the next release.

