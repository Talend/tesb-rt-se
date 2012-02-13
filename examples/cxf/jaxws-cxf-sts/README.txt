WS-Trust (JAX-WS CXF STS sample)
=================================

Provides an example of a CXF SOAP client (WSC) accessing a CXF STS for a SAML
assertion and then subsequently making a call to a CXF web service provider
(WSP).  X.509 authentication is used for the WSC->STS call. Sample keystores
and truststores for the WSC, WSP, and STS are provided in this project but
are of course not meant for production use.

Important Note:  By default, this example uses strong encryption which is 
recommended for use in production systems.  To run this example "out of the
box", you MUST have the "Java(TM) Cryptography Extension (JCE) Unlimited 
Strength Jurisdiction Policy Files" installed into your JRE.  See your JRE
provider for more information. (For Oracle JDK6, the download is available
here: http://www.oracle.com/technetwork/java/javase/downloads/index.html, see
the README file from the download for installation instructions.)
   
Alternatively, you can change to using a lower end encyption algorithm by
editing the security policies in:

client/src/main/resources/DoubleItSTSService.wsdl 
client/src/main/resources/DoubleIt.wsdl 
service/src/main/resources/DoubleIt.wsdl 
sts-war/src/main/webapp/WEB-INF/wsdl/DoubleItSTSService.wsdl 

to change from "Basic256" to "Basic128".   If you receive an error like 
"Illegal key length" when running the demo, you need to change to Basic128 or
install the Unlimited Strength encryption libraries.

Usage
===============================================================================
Note: Please follow the parent README.txt first for common build and container 
setup instructions.

How to Deploy:

1.) The STS and WSP run on either Tomcat 7.x (default) or Tomcat 6.x. If not
already done, configure Maven to be able to install and uninstall the WSP and
the STS by following this section:
http://www.jroller.com/gmazza/entry/web_service_tutorial#maventomcat. Also
start up Tomcat.

Note: If you wish to use Tomcat 6, use the -PTomcat6 flag when running the mvn
tomcat commands (tomcat:deploy, tomcat:redeploy, tomcat:undeploy). (-PTomcat7
is active by default so does not need to be explicitly specified.)

2.) From the root jaxws-cxf-sts folder, run "mvn clean install". If no errors,
run "mvn tomcat:deploy" (or tomcat:undeploy or tomcat:redeploy on
subsequent runs as appropriate) in the "sts" folder to deploy the STS.

Before proceeding to the next step, make sure you can view the following WSDL:
CXF STS WSDL located at: http://localhost:8080/DoubleItSTS/X509?wsdl

3.) Next we need to deploy the WSP, for which three options are provided:

 * To run the service in a standalone manner on port 9000, run mvn exec:java
   from the service folder.  Make sure you can view the WSP WSDL at
   http://localhost:9000/doubleit/services/doubleit?wsdl before proceeding.

 * To run the service from Tomcat, go to the WAR folder and run mvn tomcat:deploy
   (can also use mvn tomcat:undeploy and mvn tomcat:redeploy for subsequent installs)
   Make sure you can view the WSP WSDL at
   http://localhost:8080/doubleit/services/doubleit?wsdl before proceeding.

 * To run the WSP from within the OSGi container. One thing to be aware of is 
   that the default port for Tomcat (8080) will conflict with Karaf's OPS4J Pax Web - 
   Jetty bundle. Therefore, best to stop Tomcat, start Karaf, and stop Karaf's 
   Pax Jetty bundle before restarting Tomcat (to activate the STS).

   From the OSGi command line, run:
      karaf@tsf> features:install tsf-example-jaxws-cxf-sts-service

   (Make sure you've first installed the examples features repository as described in the
   parent README.)

   Make sure you can view the WSP WSDL at
   http://localhost:9000/doubleit/services/doubleit?wsdl before proceeding.

4.) Navigate to the client folder:

Note: If you've selected standalone or OSGi deployment of the WSP in the preceding
step, the WSP address in the client WSDL (client/src/main/resources/DoubleIt.wsdl) 
must be updated before invoking the WSP to "http://localhost:9000/doubleit/services/doubleit". 
This is because the WSP runs on port 9000 when run in a standalone manner, 
or in Karaf, to avoid clashing with the port that Tomcat is using (8080).

 * To run the client in a standalone manner, run mvn clean install exec:exec.

 * From the OSGi command line, run:
      karaf@tsf> features:install tsf-example-jaxws-cxf-sts-client

You should see the results of the web service call. 


For DEBUGGING:

1.) Activate client-side logging by uncommenting the logging feature in the
client's resources/cxf.xml file. The logging.properties file in the same
folder can be used to adjust the amount of logging received.

2.) Check the logs directory under your Tomcat (catalina.log,
catalina(date).log in particular) or Karaf (data/log) folder 
for any errors reported by the STS.

3.) Use Wireshark to view messages:
http://www.jroller.com/gmazza/entry/soap_calls_over_wireshark

