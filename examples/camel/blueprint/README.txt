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
Blueprint
=========
The camel-blueprint component allows you to deploy Camel routes as an OSGi bundle in the TESB container.

The Camel DSL is embedded in a blueprint (http://camel.apache.org/using-osgi-blueprint-with-camel.html) context.

Simple
------
The simple.xml is a blueprint definition allowing you to define a route with a timer.
Every 5 seconds, the timer creates an event and uses a constant "Hello World" body string.
This string is sent to a stream endpoint which displays the string in the TESB console.

Recipient List
--------------
The recipientlist.xml is a blueprint definition quite similar to the simple one.
But messages are sent to both a file and a stream endpoint.

Usage
===============================================================================

Note: Please follow the parent README.txt first for common build and container setup instructions.

Hot deployment
--------------
Start the TESB container

> container/bin/trun

Install the required features

> feature:install camel-stream

You should be able to see the OSGi bundles for your Camel blueprint using

> bundle:list

Next, simply drop the src/main/resources/simple.xml or src/main/resources/recipientlist.xml 
in the container/deploy directory.

Every 5 seconds, you will see the "Hello World" message in the TESB console.  If you used
recipientlist.xml, you'll also see files created in a new result folder located under the 
container directory.

To uninstall the example delete recipientlist.xml or simple.xml from the container/deploy directory, 
type ^D at the TESB container console and restart container with container/bin/trun.


