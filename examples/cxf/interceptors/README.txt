Interceptors Example
==================================================================

This sample demonstrates how a message changes and is manipulated
as it passes through the various interceptors.

This sample consists of 3 parts:
common/   - This directory contains the code that is common
            for both the client and the server.  It contains
	    the WSDL and the artifacts that are generated 
	    from that WSDL.  It also contains a DemoInterceptor
	    that prints out various parts of the message
	    as part of its handleMessage method.

service/  - This is the service.   It adds a DemoInterceptor
            to every phase to show how the service reads
	    and writes the messages it processes.

client/   - This is a sample client application that uses
            the JAX-WS API's to create a proxy client and
	    makes a call with it.  It also adds a 
	    DemoInterceptor to every phase.


The best way to run this sample is from within Eclipse.  As the 
purpose of this example is to demonstrate how the message changes
as it proceeds through the interceptor chain, it's best to put
a breakpoint in the interceptor and "debug" the example.   This 
will allow inspecting the message and chain.


Usage
===============================================================================
Note: Please follow the parent README.txt first for common build and container 
setup instructions.

Building the Demo
---------------------------------------
Using either Linux or Windows:
    mvn install


Importing into Eclipse
---------------------------------------
To load this example into Eclipse, we first need to setup the eclipse
projects.   To do so, from the command line, run:

    mvn eclipse:eclipse

That will download the CXF sources jars, create the Eclipse project 
files, and wire everything together.   To import them into your Eclipse
workspace, from Eclipse, select:

   File -> Import -> General -> Existing Projects into Workspace

Navigate to the interceptors example folder and hit OK.  It should
find all three projects.  Hit finish and they should load into the
workspace.   At this point, it's recommended that you open the
DemoInterceptor class in the interceptors-common project and put
a breakpoint at the start of the handleMessage method.


Starting the Service
---------------------------------------
* From the command line:
     cd service ; mvn exec:java

* From within the Talend Service Factory OSGi container:
   karaf@tsf> features:install tsf-example-interceptors-server

   (Make sure you've first installed the examples features repository as described in the
   parent README.)

* From within Eclipse:
     Open the Server class in the interceptors-server project
     Right click and select "Run As -> Java Application" or 
          "Debug As -> Java Application"


Running the Client
---------------------------------------
* From the command line:
     cd client ; mvn exec:java

* From within the OSGi container
   karaf@tsf> features:install tsf-example-interceptors-client

* From within Eclipse:
     Open the Client class in the interceptors-client project
     Right click and select "Run As -> Java Application" or 
          "Debug As -> Java Application"

Interesting things to look at
---------------------------------------
As mentioned above, this example is best run from Eclipse by "debugging" 
it.  By putting a breakpoint at the first println in the handleMessage 
method of the DemoInterceptor, you can really "dig in" to the Message 
that is passed into it.  Some things to examine/notice:
  * The contents of the "List" content.  On the client side, the initial
    list will contain the parameters as passed to the method.  By the end
    of the pre-logical phase, that parameter will have been replaced
    with a wrapper object that represents the entire body contents.
  * At the end of the prepare-send phase, the OutputStream is now
    in the contents list. Thus, anything that needs to manipulate
    the raw stream can have access to it.
  * At the end of the pre-stream phase, the XMLStreamWriter is now
    there wrappering the OutputStream.  Also, a new interceptor 
    (StaxOutInterceptor$StaxOutEndingInterceptor) has been added 
    to the end of the chain.
  * You can also do a "Step Return" (F7) to return from the interceptor.
    The Eclipse setup above wires in the CXF source code so you can 
    debug right into the CXF code.   Thus, you can try tracing directly
    into some of the interesting CXF interceptors.



Cleaning up
---------------------------------------
To remove the code generated from the WSDL file and the .class
files, run "mvn clean".



