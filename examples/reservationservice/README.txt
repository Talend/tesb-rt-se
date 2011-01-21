Rent-a-Car Description Example 
=======================================

For this tutorial we have chosen a common business use case scenario: a simplified 
real-world example from the domain of car rental companies.
In this scenarios there are two services: CRMService and ReservationService.


Building the Demo
---------------------------------------

This sample consists of 3 parts:
common/   - This directory contains the ReservationService.wsdl which is used to generate the initial code. 
            
service/  - This is where a ReservationService service implementation shared by JAX-WS endpoints is located

client/   - This is a sample client application that shows how CXF JAX-WS proxies are invoking on remote 
            JAX-WS endpoints represented by ReservationService interface 


From the base directory of this sample (i.e., where this README file is
located), the maven pom.xml file can be used to build and run the demo. 


Using either UNIX or Windows:

    mvn install

Running this command will build the demo and create an OSGi bundle 
for deploying the service to OSGi containers.

Starting the service
---------------------------------------
 * From within the Talend Service Factory OSGi container:

    TODO

Running the client
---------------------------------------

    TODO

