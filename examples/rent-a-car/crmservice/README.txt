Rent-a-Car Description Example 
=======================================

For this tutorial we have chosen a common business use case scenario: a simplified 
real-world example from the domain of car rental companies.
In this scenarios there are two services: CRMService and ReservationService.


Building the Demo
---------------------------------------

This sample consists of 11 parts:
common/   - This directory contains the CRMService.wsdl which is used to generate the initial code. 
            
service/  - This is where a CRMService service implementation shared by JAX-WS endpoints is located

client/   - This is a sample client application that shows how CXF JAX-WS proxies are invoking on remote 
            JAX-WS endpoints represented by CRMService interface 
client-locator/   - This directory contains the locator enabled crmservice client
client-sam/   - This directory contains the sam enabled crmservice client
client-sts/   - This directory contains the sts enabled crmservice client
service-endpoint/   - This directory contains the basic crmservice endpoint
service-endpoint-locator1/   - This directory contains the locator enabled crmservice endpoint
service-endpoint-locator2/   - This directory contains the locator enabled crmservice endpoint
service-endpoint-sam/   - This directory contains the sam enabled crmservice endpoint
service-endpoint-sts/   - This directory contains the sts enabled crmservice endpoint

From the base directory of this sample (i.e., where this README file is
located), the maven pom.xml file can be used to build and run the demo. 


Using either UNIX or Windows:

mvn clean install                 (for basic crmservice)
mvn clean install -Plocator       (for Service Locator enabled crmservice)
mvn clean install -Psam           (for Service Activity Monitoring enabled crmservice)
mvn clean install -Psts           (for Security Token Service enabled crmservice)

Running this command will build the demo and create an OSGi bundle 
for deploying the service to OSGi containers.

There are 11 OSGi bundles will be created:
common/target/crmservice-common-4.0.jar
service/target/crmservice-service-4.0.jar
client/target/crmservice-client-4.0.jar
client-locator/target/crmservice-client-locator-4.0.jar
client-sam/target/crmservice-client-sam-4.0.jar
client-sts/target/crmservice-client-sts-4.0.jar
service-endpoint/target/crmservice-service-endpoint-4.0.jar
service-endpoint-locator1/crmservice-service-endpoint-locator1-4.0.jar
service-endpoint-locator2/crmservice-service-endpoint-locator2-4.0.jar
service-endpoint-sam/crmservice-service-endpoint-sam-4.0.jar
service-endpoint-sts/crmservice-service-endpoint-sts-4.0.jar

Starting the service
---------------------------------------
 * From within the Talend Service Factory OSGi container:

1.Install Talend Service Factory Community Edition which include OSGi container or higher from http://www.talend.com/download.php.

You can find out how to get started with OSGi container here: http://karaf.apache.org/

1.Start OSGi:
run <takend-sf>/container/bin/start
2.Deploy services into OSGi conteyner.

copy follow bundles into folder <Talend ESB Runtime>/container/deploy
for basic:
crmservice-common-4.0.jar
crmservice-service-4.0.jar
crmservice-client-4.0.jar
crmservice-service-endpoint-4.0.jar

for locator enabled:
crmservice-common-4.0.jar
crmservice-service-4.0.jar
crmservice-service-endpoint-locator1-4.0.jar
crmservice-service-endpoint-locator2-4.0.jar
crmservice-client-locator-4.0.jar

for sam enabled:
crmservice-common-4.0.jar
crmservice-service-4.0.jar
crmservice-service-endpoint-sam-4.0.jar
crmservice-client-sam-4.0.jar

for sts enabled:
crmservice-common-4.0.jar
crmservice-service-4.0.jar
crmservice-service-endpoint-sts-4.0.jar
crmservice-client-sts-4.0.jar

3.Type in console command "list".
You will see (for example)
[ 114] [Active     ] [            ] [       ] [   60] CRMService Common (4.0.0)
[ 116] [Active     ] [            ] [Started] [   60] CRMService Client (4.0.0)
[ 123] [Active     ] [            ] [Started] [   60] CRMService Service (4.0.0)


