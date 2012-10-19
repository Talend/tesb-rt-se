Example for publishing CXF JAXRS services using Camel
===============================================================================

The example shows how a simple JAX-RS service called BookStore can be offered and used using
camel transports. The example also shows some best practices how to decouple your business logic from
the Camel and CXF frameworks.

The client project contains a standalone client that calls several service methods using the BookStore
interface.


Usage
===============================================================================

Note: Please follow the parent README.txt first for common build and container setup instructions.

1) Building the Demo
-------------------------------------------------------------------------------

Using either UNIX or Windows, from the command line, run:
> mvn clean install


2) Running the JMS Broker
-------------------------------------------------------------------------------

The sample requires a JMS broker to be running:

  * From the command line, run:
> mvn -Pjms.broker

  That will create a new broker (using the default configuration) and will start it.
Alternatively, you can start a broker from within the TESB OSGi container, see below for the instructions.

3) Starting the Service
-------------------------------------------------------------------------------

3.1) In Jetty
> cd war ; mvn jetty:run

3.2) From within the TESB OSGi container

Make sure you've first installed the examples features repository
as described in the parent README.

Start the broker if not already started:
karaf@trun> features:install activemq-spring
karaf@trun> activemq:create-broker

Install and start demo bundles:
karaf@trun> features:install talend-camel-example-jaxrs-jms-http


4) Running the Client
-------------------------------------------------------------------------------

From the command line:
> cd client ; mvn exec:java


5) Cleaning up
-------------------------------------------------------------------------------
  * Press ^C in the server and broker windows to stop the running processes
  * To remove the code generated from the WSDL file and the .class files, run:
> mvn clean

