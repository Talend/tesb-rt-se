# Talend ESB

[![alt text](https://www.talend.com/wp-content/uploads/2016/07/talend-logo.png "Talend")](http://www.talend.com/)


Talend ESB bundles Service Factory and Integration Factory,
Service Locator, Service Activity Monitoring and demo applications
to a stable, production ready distribution based on the industry leading
open source frameworks Apache CXF and Apache Camel.
It includes Web Service support based on JAX-WS and enables simple and scalable
message based systems using well known Enterprise Integration Patterns.
The Service Locator maintains a repository of service endpoints which
can be used by Service Consumers to support simple failover and load
balancing scenarios. Service Activity Monitoring allows to log and
monitor service calls. A documented Car Rental demo application illustrates
how to use these features.

## Getting Started

The Getting Started Guide can be found in a separate documentation package in the docs
folder. It illustrates all features of Talend ESB using example applications.

The examples are documented individually and include instructions for building
and running each example with just a few command lines. See below for obtaining
the examples.

If you need more help try talking to us on our [forums](http://talendforge.org/forum).

You can find more information about Apache CXF at http://cxf.apache.org/

You can find more information about Apache Camel at http://camel.apache.org/

Please submit bug reports using one of the following JIRAs:

* CXF bug reports: https://issues.apache.org/jira/browse/CXF
* Camel bug reports: https://issues.apache.org/jira/browse/CAMEL
* Talend ESB (including Service and Integration Factory) bug reports: http://www.talendforge.org/bugs/


## Examples

Talend ESB provides several examples in a separate documentation package in
the examples folder. The example applications and tutorials demonstrate
functionality and advanced features of Talend ESB.
The examples demonstrate how to use different functionality including:

*    Advanced web services with Camel
*    Security configuration within OSGi
*    Use of blueprint to define routes
*    Rent a Car demo
*    Locator sample
*    SAM sample

## OSGi Container

The container subdirectory contains a preconfigured OSGi container that
contains all the required OSGi bundles for the CXF third party dependencies.
It also includes Apache Karaf to provide easy administration and
configuration. For more information about OSGi and Apache
Karaf see http://karaf.apache.org/ .

OSGi provides a mature, open standards based, highly modular framework for
managing component dependencies, service invocation, and lifecycles. It is
the basis for Eclipse and provides a lightweight alternative to more
monolithic JEE containers while still retaining the powerful management
features necessary for the enterprise.

## Service Locator

The Service Locator in the add-ons directory is a service that provides its
consumers with a mechanism to discover service endpoints at run time. The
Service Locator consists of two parts: An Endpoint repository and a CXF feature
used to configure the use of the Service Locator from both the provider and
consumer sides. Like any standard CXF feature, the ServiceLocator Feature is
configured separately for the service provider and service consumer. The
provider side Locator Feature extension registers and deregisters service
endpoints in the endpoint repository when the provider becomes available or
unavailable. The consumer side Locator Feature extension transparently retrieves
service endpoint addresses from the endpoint repository when a service call to a
provider is to be made.

## Service Activity Monitoring

The Service Activity Monitoring (SAM) in the add-ons directory allows to
log / monitor service calls done with the Apache CXF Framework. Typical use
cases are usage statistics and fault monitoring. The solution consists of two
parts: Agent (sam-agent) and Monitoring Server (sam-server). The Agent creates
events out of the requests and replies on service consumer and provider side.  The
events are first collected locally and then sent to the monitoring server
periodically to not disturb the normal message flow. The Monitoring Server
receives events from the Agent, optionally filters/handlers events and stores them
into a database.

## License

Copyright (c) 2006-2021 Talend Inc. - www.talend.com

Licensed under the [Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0.txt)
