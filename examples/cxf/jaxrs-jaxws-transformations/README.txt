JAX-RS JAX-WS Transformations Example 
=======================================

The demo shows how CXF can help with maintaining backward and forward compatibility between JAX-RS and JAX-WS consumers and endpoints.

Backward compatibility allows for newer endpoints to consume requests from older clients and newer clients to consume responses from older endpoints while forward compatibility allows for newer clients to call the older endpoints and newer endpoints to reply to older clients. 

Developers are usually concerned about mantaining the backward compatibility, however, the forward compatibility is also important as it allows for a more decentralized evolution of clients and endpoints. 

Backward compatibility is realistic when the newer endpoints can safely ignore the fact that older comsumers do not provide some additional
information which the updated endpoints are aware of. An example of such ignorable content can be a middle name added to the Customer object.
Newer endpoints can serve the requests without the middle name being provided.

Forward compatibility is realistic when it is safe for the older endpoints to ignore the information newer consumers provide. For example,
older endpoints will operate just fine without Customers having a middle name field populated. However, ignoring the info such as a credit card security code, etc, is not safe. 

CXF 2.4.2 introduces a Transformation feature (http://cxf.apache.org/docs/transformationfeature.html) which makes it easier to handle 
the complex issue of maintaining the compatibility.

Additionally, this demo shows how CXFServlet can redirect requests.

Building the Demo
---------------------------------------

This sample consists of 3 parts:
common/   - This directory contains the ConsumerService.wsdl and ConsumerServiceNew.wsdl used by old and new JAX-WS endpoints and clients
            and ConsumerService-jaxrs.xml which describes old and new JAX-RS endpoints.  
            
service/  - This is where new and old JAX-RS and JAX-WS endpoints are located

war/      - This module creates a WAR archive containing the code from common and service modules.   

client/   - This is a sample client application that shows how CXF JAX-RS and JAX-WS old and new consumers invoke on
            old and new JAX-RS and JAX-WS endpoints


From the base directory of this sample (i.e., where this README file is
located), the maven pom.xml file can be used to build and run the demo. 


Using either UNIX or Windows:

    mvn install

Running this command will build the demo and create a WAR archive and an OSGi bundle 
for deploying the service either to servlet or OSGi containers.

Starting the service
---------------------------------------
 * In the servlet container

    cd war; mvn jetty:run

 * From within the Talend Service Factory OSGi container:
   karaf@tsf> features:install tsf-example-jaxrs-jaxws-transformations

    (Make sure you've first installed the examples features repository as described in the parent README.)

Running the client
---------------------------------------

 * From the command line
   - cd client
 * Run the JAX-WS client 
   - mvn -Pjaxws 
 * Run the JAX-RS client 
   - mvn -Pjaxrs 
     

By default, the client will use the http port 8080 for constructing the URIs.
This port value is set during the build in the client.properties resource file. 
If the server is listening on the alternative port then you can use an 'http.port' system property when running the client:
   
- mvn -Pjaxws -Dhttp.port=8181
- mvn -Pjaxrs -Dhttp.port=8181

Demo Desciption
---------------

The goal of this demo is to show how CXF can facilitate writing JAX-WS and JAX-RS providers and consumers which can 
cope with changes better. The following scenario is demonstrated:

Old endpoints work with the schema containing 'customer' types in the "http://customer/v1" namespace. New endpoints work 
with the schema containing 'customer' types in the "http://customer/v2" namespace. The updated 'customer' type also has an 
additional 'briefDescription' simple element added. 

Note that the namespace has been changed. Usually, the breaking changes lead to the namespace changes, but this practice is not always followed.
In this case an element which can be ignored has been added but the namespace has been changed anyway. So the first step in ensuring the compatibility
is to have the "http://customer/v1" namespace converted to "http://customer/v2" on the input and "http://customer/v2" to "http://customer/v2" on the output. We will use the transform feature to convert between the namespaces.

Note that the newly added 'briefDescription' element has been made optional and the validation has been enabled. The backward compatibility is simpler to achieve because the older payloads without this element will be validated. The forward compatibility won't work though because the older endpoints won't validate the unrecognized 'briefDescription' tag included with the newer payloads. The transform feature will be used by the old endpoints to drop
tags such as 'briefDescription' for the validation to succeed.

The next issue to consider is whether the old clients know that they will invoke on new endpoints and new clients know that they will invoke on old endpoints. This knowledge will let admins configure transformation rules on the client side. However, this demo also shows a more advanced case, where old endpoints have been replaced by the new endpoints and requests to the old endpoints are redirected to the new endpoints.

Please check the beans.xml file in the war module. The first 4 endpoints with addresses "/rest", "/new-rest", "/soap", "/new-soap" are old and new JAX-RS and JAX-WS endpoints. Also check web.xml, CXFServlet with the name "CXFServlet" and the uri path "/direct/*" manages these endpoints.

Example, an "http://localhost:8080/services/direct/rest" URI will be handled by the JAX-RS endpoint with the address "/rest", "http://localhost:8080/services/direct/new-soap" - by the JAX-WS endpoint with the address "/new-soap", etc. 

Please also see RESTClient and SOAPClient in the client module. Old and new clients invoke on old/new endpoints respectively as usual. The transformation feature is used when new clients have to invoke on the old endpoints and when old clients have to invoke on the new endpoints.
 
The use of the Transform feature (see) the forward compatibility is met by dropping a 'briefDescription' element whem new clients invoke on old endpoints and when old clients get responses from new endpoints. Also, the namespace is converted as described above in both cases.


RESTClient.useOldRESTService(), RESTClient.useNewRESTService(), RESTClient.useOldRESTServiceWithNewClient() and RESTClient.useNewRESTServiceWithOldClient() show how it works for JAX-RS. SOAPClient.useOldSOAPService(), SOAPClient.useNewSOAPService(), SOAPClient.useOldSOAPServiceWithNewClient() and SOAPClient.useNewSOAPServiceWithOldClient() show how it works for JAX-WS.

Also, RESTClient.useOldRESTServiceWithNewClientAndXPath() shows how XPath can be applied to get the typed objects from the response.

The other two endpoints in the beans.xml, with addresses "/rest-endpoint" and "/soap-endpoint" are new endpoints which also have requests from older consumers redirected to them with the help of CXFServlet. The Transform feature is added to these endpoints and is configured such that it only applies in case of redirections, this is needed for it not to affect the new consumers talking to these new endpoints. 

A CXFServlet with the name "CXFServletOldEndpoints" and the uri path "/old/*" which used to serve old REST and SOAP endpoints has been reconfigured to redirect all requests containing '/rest-endpoint' and '/soap-endpoint' URI parts to anothjer servlet, "CXFServletNewEndpoints" with the uri address "new/*" which does allow for the invocation to proceed.

Note that the CXFServletNewEndpoints has been configured to disable the address overwrites for the redirected requests be handled properly.

Example, a request URI such as "http://localhost:8080/services/old/rest-endpoint" will be transformed at the servlet level to "http://localhost:8080/services/new/rest-endpoint" and the new endpoint will handle this request, by applying the transform feature to the input and output payloads but only in case of redirects.

RESTClient.useNewRESTServiceWithOldClientAndRedirection() and SOAPClient.useNewSOAPServiceWithOldClientAndRedirection() show this case.


