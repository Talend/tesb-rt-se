JAX-RS OAuth2 Example 
===========================

Introduction
---------------------------------------

The demo demonstrates the complete OAuth 2.0 Authorization Code Flow as described at [1].
Here is the Abstract section [1]:

"The OAuth 2.0 authorization protocol enables a third-party
   application to obtain limited access to an HTTP service, either on
   behalf of a resource owner by orchestrating an approval interaction
   between the resource owner and the HTTP service, or by allowing the
   third-party application to obtain access on its own behalf.  This
   specification replaces and obsoletes the OAuth 1.0 protocol described
   in RFC 5849." 

This demo shows a simple Social.com social application which maintains a 
calendar for every registered user.  Effectively, a user's calendar is the 
private resource which only this user can access.  

Social.com has a partner, Restaurant Reservations, which offers an online 
service to Social.com users which can be used to book a dinner at a 
specific hour.  In order to be able to complete the reservation this 
third-party service needs to check a user calendar to make sure that the 
user is actually free at the requested hour at the moment of making the 
booking and optionally update the calendar with the reservation details.  

Social.com is OAuth-protected and thus the user has to explicitly 
authorize this service for it to be able to read and update its calendar.  After the 
third-party service gets the confirmation it can access the user's 
calendar and interact with its own partner, Restaurant service, in order 
to make the booking.  

Please see the "Demo Description" section below for more information. 

[1] http://tools.ietf.org/html/draft-ietf-oauth-v2-25

Additinally, please follow sso-saml/README.txt on how to run this demo with 
Social.com, Reservations and OAuth2.0 web applications running on different 
HTTP ports, with SAML Web Browser Single Sign-On enabled.   


Building the Demo
---------------------------------------

This sample consists of 3 parts:
common/   - This directory contains the code that is common
            to both the client and the server. 
            
service/  - This module contains the code for Social.com, Restaurant Registration and Restaurant services.
             
war/      - This module creates a WAR archive containing the code from common and service modules.   

client/   - This is a sample client application which emulates the typical OAuth flow with the end user confirmation encoded in the code. 


From the base directory of this sample (i.e., where this README file is
located), the maven pom.xml file can be used to build and run the demo. 


Using either Linux or Windows:

    mvn install

Running this command will build the demo and create a WAR archive and an OSGi 
bundle for deploying the service either to servlet or OSGi containers.

Usage
===============================================================================
Note: Please follow the parent README.txt first for common build and container 
setup instructions.

Starting the service
---------------------------------------
 * In the servlet container

    cd war; mvn jetty:run-war

 * From within the TESB OSGi container:

   This will be supported shortly
    
Running the client
---------------------------------------
 
* From the command line
   - cd client
   - mvn exec:java

By default, the client will use the http port 8080 for constructing the URIs.
This port value is set during the build in the client.properties resource 
file. If the server is listening on an alternative port then you can use an 
'http.port' system property during the build:
   
- mvn install -Dhttp.port=8040


* From the browser

- Go to "http://localhost:8080/services/forms/registerApp.jsp".  It is a 
  Consumer Application Registration Form. Select a 'smiley-face.jpg' located in
  the 'war/resources' folder of this example as an Application logo.
  Press "Register Your Application" button once you are ready.  
- Follow the link in the bottom of the returned Consumer Application 
  Registration Confirmation page in order to register a user with 
  Social.com.  
- The Social.com User Registration Form asks for a user name and password.  
  At the moment only a user name "barry@social.com" with the "1234" password 
  is supported 
- Press "Register With Social.com" to complete the account registration.  
- Follow the link in the bottom of the returned User Registration 
  Confirmation page in order to view the personal UserAccount page.
- When asked please authenticate with the service using the 
  "barry@social.com" and "1234" pair. 
- View the account page, Note that Calendar has no reserved events.
- Follow the link in the bottom of the User Account page in order to try
  the online Restaurant Reservations service.  
- The Restaurant Reservations Form offers an option to book a restaurant 
  table at a specific hour, press Reserve to start the process.  
- When asked please authenticate with the service using the 
  "barry@restaurant.com" and "5678" pair.
- The Restaurant Reservations will redirect to the Authorization service
  protecting Social.com, it will challenge the end user with the authorization form.
- The Third Party Authorization Form will ask if the Restaurant 
  Reservations can read the calendar and update it for a specific hour slot (7 in this demo)
  on behalf of its owner, "barry@social.com".  
- Press "Deny", and after receiving the Restaurant Failure Report page, 
  please follow the link at the bottom of the page to start the reservation 
  again.  
- Press Reserve at The Restaurant Reservations Form and this time choose 
  "Allow" at the The Third Party Authorization Form.  
- The Restaurant Reservation Confirmation form will be returned confirming 
  the reservation at the required hour. Follow the link in the bottom of the page
  to confirm that the calendar has been updated accordingly.  


Demo Desciption
---------------

The description of how to interact with the demo application using a 
browser in the previous section provides an overview of a typical complete 
OAuth 2.0 Authorization Code Flow.

For the third-party Restaurant Reservations service (the "client", 
in OAuth terminology) to be able to request access to a Social.com user's
calendar, it has to register itself first with the OAuth server which
protects Social.com.  Typically this is done out of band and is only
demonstrated here to highlight the fact that the third-party must
register to be able to participate in OAuth flows.  When the 
registration is complete, the third-party service gets back a consumer 
id and secret pair which it will use later on, when signing OAuth 
requests.  oauth.manager.ThirdPartyRegistrationService is used to 
emulate this process.  

The Social.com application keeps a list of registered users and their 
calendars.  Two JAX-RS services are used to implement it, 
oauth.service.UserRegistrationService which manages the user registration 
requests and oauth.service.SocialService which lets registered users 
access or update their private calendars.  

Social.com also relies on 
org.apache.cxf.rs.security.oauth2.services.AuthorizationRequestService 
which will be used to ask a Social.com user to authorize the third-party, 
more on this below.  

Access to UserRegistrationService and SocialService, as well as to 
CXF's AuthorizationRequestService is protected by a filter 
(oauth.service.SecurityContextFilter) enforcing that a currently 
logged in user is a valid Social.com user.  

When the registered Social.com user accesses the online Restaurant 
Reservations service in order to book a table, this service 
(oauth.thirdparty.RestaurantReservationService) initiates the OAuth flow 
in order to get permission to access a current user's calendar.  But 
first, it requires the user to login.  


Now, once the user has logged in to Restaurant Reservations, the service 
initiates an  OAuth Authorization Code Grant request.  It  
includes its registration id and redirect URI URI - this is where an authorization code will be 
delivered to.  Note that this happens in the scope of 
processing a current user's request.  An instance of 
org.apache.cxf.rs.security.oauth2.services.AuthorizationRequestService processes 
this request.  

AuthorizationRequestService uses the client id passed along 
to retrieve the information about Restaraunt Reservations and returns an 
authorization form to the user.  This form will include a randomly generated 
key in a hidden field to prevent a so called Cross-Site Request Forgery 
attack.  

When the user submits the form back, this key will be matched against the 
one stored in the current HTTP session instance.  

The user has a choice to Deny or Allow the access to its calendar.  If 
Allow is pressed then Authorization service will append a new authorization code
to the callback URI provided by Restaurant Reservations and will redirect the user back to the 
client (the Reservation Service) listening on the redirect URI.  

Now, the client has got a confirmation that it is allowed to access
the current user's calendar, as it has the authorization key.  
It now exchanges it for an access token by sending an OAuth request to an instance of 
org.apache.cxf.rs.security.oauth2.services.AccessTokenService.  

After an access token key is received back, Restaurant 
Reservations requests a current user's calendar in order to verify the 
user is actually free at the requested hour.


