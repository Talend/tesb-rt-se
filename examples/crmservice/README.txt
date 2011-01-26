Rent-a-Car Description Example 
=======================================

For this tutorial we have chosen a common business use case scenario: a simplified 
real-world example from the domain of car rental companies.
In this scenarios there are two services: CRMService and ReservationService.


Building the Demo
---------------------------------------

This sample consists of 3 parts:
common/   - This directory contains the CRMService.wsdl which is used to generate the initial code. 
            
service/  - This is where a CRMService service implementation shared by JAX-WS endpoints is located

client/   - This is a sample client application that shows how CXF JAX-WS proxies are invoking on remote 
            JAX-WS endpoints represented by CRMService interface 


From the base directory of this sample (i.e., where this README file is
located), the maven pom.xml file can be used to build and run the demo. 


Using either UNIX or Windows:

    mvn install

Running this command will build the demo and create an OSGi bundle 
for deploying the service to OSGi containers.

There are three OSGi bundles will be created:
...\common\target\crmservice-common-1.0.jar
...\server\target\crmservice-server-1.0.jar
...\client\target\crmservice-client-1.0.jar

Starting the service
---------------------------------------
 * From within the Talend Service Factory OSGi container:

1.Install Talend Service Factory Community Edition which include OSGi container version 2.3.2 or higher from http://www.talend.com/download.php.

You can find out how to get started with OSGi container here: http://karaf.apache.org/

1.Start OSGi:
run <takend-sf>/conteiner/bin/start
2.Deploy service into OSGi conteyner.

copy follow bundles into folder <takend-sf>/conteiner/deploy
crmservice-common-1.0.jar
crmservice-server-1.0.jar
crmservice-client-1.0.jar

3.Type in console command "list".
You will see 
[ 114] [Active     ] [            ] [       ] [   60] CRMService Common (1.0.0)
[ 116] [Active     ] [            ] [Started] [   60] CRMService Client (1.0.0)
[ 123] [Active     ] [            ] [Started] [   60] CRMService Service (1.0.0)


