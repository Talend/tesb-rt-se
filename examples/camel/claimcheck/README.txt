EAI patterns example (Claim Check, Splitter, Resequencer, Delayer)
===============================================================================

Our business case for the example is a video producer that wants to send a video through an unreliable network (internet) 
that can not transport large messages and may scramble the sequence of messages.

1) The sender 
File => Splitter => CheckIn => Ordered Queue

The example starts with a directory where the file is dropped. The file is then split into smaller pieces, in our case by new lines, but 
in a real scenario it could be by a maximum packet size. The large data object (LOB) in a message is then checked into a DataStore and 
replaced by an claim tag (an ID). Then the claim tag message is sent to a queue.

2) Delayer
Ordered Queue => Delayer => Unordered Queue

This part is only necessary to ensure the messages get scrambled to simulate the effect of sending messages on an unreliable network.

3) Claim and Resequencer
Unordered Queue => Claim => Resequencer

The messages are received from the queue. The LOB correlated with the Claim tag is fetched again and messages are brought back in
the original sequence.


The Patterns used
===============================================================================

Claim Check
----------- 
This pattern shows how to improve message throughput and reduce load on your service bus.
The claim check pattern is one of the Enterprise Integration patterns explained at http://www.eaipatterns.com/StoreInLibrary.html.
Its name refers to the baggage check in and claim at airports. The passenger checks in his luggage where it is handled separately 
and reclaims it at the destination. 

Another nice article about the claim check pattern can be found here:
http://www.ibm.com/developerworks/websphere/library/techarticles/1006_kharlamov/1006_kharlamov.html

Splitter
---------

A splitter splits one large message into several smaller ones
See: http://camel.apache.org/splitter.html

Resequencer
-----------

If your messages come out of order the resequencer allows to bring them into sequence again
See: http://camel.apache.org/resequencer.html

Delayer
-------

A delayer stops a message and forwards it after some time. In the example we write the delayer 
using a bean to achieve a random delay
See: http://camel.apache.org/delayer.html


Usage
===============================================================================

Note: Please follow the parent README.txt first for common build and container setup instructions.

Start Standalone
----------

> cd server; mvn camel:run

Start in jetty
--------

> cd war; mvn jetty:run

Start in the OSGI container
---------------------

karaf@trun> features:install talend-camel-example-claimcheck

(Make sure you've first installed the examples features repository as described in the
parent README.)

Process a file
--------------

Next, copy a text file (e.g. the ReadMe.txt) into the "in" directory
that will be created under the TESB "container" folder after the route 
is started.  Then check the log files:

Each line of the file will be output for each stage in the route. It looks like this:
route4 INFO  claimed 3 Our business case for the example
- "claimed means that the LOB has been claimed again
- "3" is the part / line number
- "Our business case ... " is the content of the body of the message

Unordered:
   The lines starting with unordered show the state after splitting and check 
   in of the LOB. The line numbers will be scrambled and the real content 
   replaced by the "claim tag" (a uuid to later retrieve the data)

Claimed:
   The message content has been retrieved again but the lines are still scrambled
   
Ordered:
   The messages are now also ordered. So the original content should be readable in the log


