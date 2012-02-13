WS-Trust (JAX-WS Metro STS sample)
=================================

Provides an example of a CXF SOAP client (WSC) accessing a Metro STS for a
SAML assertion and then subsequently making a call to a CXF web service
provider (WSP).  Two WSC->STS calls are made, one using UsernameToken 
authentication and the other X.509 authentication, but in both cases the same
SAML assertion is provided to the WSP.  Sample keystores and truststores for
the WSC, WSP, and STS are provided in this project but are of course not meant
for production use.

Important Note:  By default, this example uses strong encryption which is 
recommended for use in production systems.  To run this example "out of the
box", you MUST have the "Java(TM) Cryptography Extension (JCE) Unlimited 
Strength Jurisdiction Policy Files" installed into your JRE.  See your JRE
provider for more information. (For Oracle JDK6, the download is available
here: http://www.oracle.com/technetwork/java/javase/downloads/index.html, see
the README file from the download for installation instructions.)
   
Alternatively, you can change to using a lower end encyption algorithm by
editing the security policies in:

service-war/src/main/webapp/WEB-INF/wsdl/DoubleIt.wsdl 
client/src/main/resources/DoubleItSTSService.wsdl 
sts-war/bin/src/main/webapp/WEB-INF/wsdl/DoubleItSTSService.wsdl 
sts-war/src/main/webapp/WEB-INF/wsdl/DoubleItSTSService.wsdl 
common/src/main/resources/ws-trust-common/DoubleIt.wsdl  
common/src/main/resources/ws-trust-common/DoubleItSTSService.wsdl

to change from "Basic256" to "Basic128".   If you receive an error like 
"Illegal key length" when running the demo, you need to change to Basic128 or
install the Unlimited Strength encryption libraries.

How to Deploy:

1.) The Metro STS requires the newer 2.2.x versions 
of JAX-WS and JAXB (included in the JAX-WS download) not provided by default 
in Java SE 6.  Download JAX-WS V 2.2.x from http://jax-ws.java.net/ and place
the jaxb-api.jar and jaxws-api.jar in your JDK's JDK_HOME/JRE/lib/endorsed
folder.

2.) The STS and WSP run on either Tomcat 7.x (default) or Tomcat 6.x. If not
already done, configure Maven to be able to install and uninstall the WSP and
the STS by following this section: 
http://www.jroller.com/gmazza/entry/web_service_tutorial#maventomcat. Also
start up Tomcat.

Note: If you wish to use Tomcat 6, use the -PTomcat6 flag when running the mvn
tomcat commands (tomcat:deploy, tomcat:redeploy, tomcat:undeploy). (-PTomcat7
is active by default so does not need to be explicitly specified.)

3.) From the root jaxws-metro-sts folder, run "mvn clean install". If no
errors, can then run "mvn tomcat:deploy" (or tomcat:undeploy or tomcat:redeploy
on subsequent runs as appropriate), either from the same folder (to deploy the
STS and WSP at the same time) or separately, one at a time, from the
service-war and sts folders.

Before proceeding to the next step, make sure you can view the following WSDLs:
Metro STS WSDL located at:
http://localhost:8080/DoubleItSTS/DoubleItSTSServiceUT
CXF WSP: http://localhost:8080/doubleit/services/doubleitUT?wsdl

4.) Navigate to the client folder:

 * To run the client in a standalone manner, run mvn clean install exec:exec.
 * Alternatively, it is possible to run the client from within the OSGi
   container. One thing to be aware of is that the default port for Tomcat
   (8080) will conflict with the OPS4J Pax Web - Jetty bundle loaded by Karaf.
   Therefore, start Karaf, and stop the Pax Jetty bundle before starting Tomcat.

   From the OSGi command line, run:
      karaf@tsf> features:install tsf-example-jaxws-metro-sts-client

Either way, you should see the results of three web service calls, with the
client using UsernameToken in one call, and X.509 in the other to get the
SAML Assertion. The third web service call uses a SAML2 Assertion.

For DEBUGGING:

1.) Activate client-side logging by uncommenting the logging feature in the
client's resources/cxf.xml file. The logging.properties file in the same
folder can be used to adjust the amount of logging received.

2.) Check the logs directory under your Tomcat folder (catalina.log,
catalina(date).log in particular) for any errors reported by the WSP or the STS.

3.) Use Wireshark to view messages:
http://www.jroller.com/gmazza/entry/soap_calls_over_wireshark

