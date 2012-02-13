JAX-RS OAuth Example 
===========================

Introduction
---------------------------------------

The demo demonstrates the complete OAuth 1.0 flow as described at [1].
Here is the shorter and slightly modified version of the Abstract section [1]:

"OAuth provides a method for clients to access server resources on
 behalf of an end-user. It also provides a process for end-users to authorize third-
 party access to their server resources without sharing their
 credentials (typically, a username and password pair), using user-
 agent redirections." 

This demo shows a simple Social.com social application which maintains a 
calendar for every registered user.  Effectively, a user's calendar is the 
private resource which only this user can access.  

Social.com has a partner, Restaurant Reservations, which offers an online 
service to Social.com users which can be used to book a dinner at a 
specific hour.  In order to be able to complete the reservation this 
third-party service needs to check a user calendar to make sure that the 
user is actually free at the requested hour at the moment of making the 
booking.  

Social.com is OAuth-protected and thus the user has to explicitly 
authorize this service for it to be able to read the calendar.  After the 
third-party service gets the confirmation it can access the user's 
calendar and interact with its own partner, Restaurant service, in order 
to make the booking.  

Please see the "Demo Description" section below for more information. 

[1] http://tools.ietf.org/html/rfc5849 

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
    
Running the client
---------------------------------------
 
* From the command line
   - cd client
   - mvn exec:java

By default, the client will use the http port 8080 for constructing the URIs.
This port value is set during the build in the client.properties resource 
file. If the server is listening on an alternative port then you can use an 
'http.port' system property during the build:
   
- mvn install -Dhttp.port=8181


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
- Press "Register With Social.com" to complete the reservation.  
- Follow the link in the bottom of the returned User Registration 
  Confirmation page in order to view the personal UserAccount page, 
  note that the Calendar has no reserved events.
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
OAuth 1.0 flow.  (Also review the IETF example at 
http://tools.ietf.org/html/rfc5849#section-1.2 to see sample OAuth
HTTP requests and responses.)

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
org.apache.cxf.rs.security.oauth.services.AuthorizationRequestService 
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

In a real world application, one would expect a user to register with the 
third-party service such as Restaurant Reservations first.  Alternatively 
a token such as OpenID identifier may be used to login.  

In this demo the user uses the same name to login to 
Restaurant Reservations it uses to login to Social.com.  This is only 
to simplify the demo itself for Restaurant Reservations be able to indicate to Social.com at the resource request time which account it needs to access.  

The demo will be enhanced in time to show a more realistic approach with 
respect to authenticating with multiple services and avoiding reusing the same username across multiple services.  

Now, once the user has logged in to Restaurant Reservations, the service 
initiates a temporary OAuth token request.  It creates an HTTP 
Authorization header as required by OAuth and signs it using the client 
key and secret it obtained during the earlier registration.  It also 
includes a callback URI - this is where an authorization key will be 
delivered to - more on it below.  Note that this happens in the scope of 
processing a current user's request.  An instance of 
org.apache.cxf.rs.security.oauth.services.RequestTokenService processes 
this request.  

After getting a temporary token and secret back, the third-party service 
needs to get the temp token key authorized by the current user.  The 
service redirects the user back to the 
org.apache.cxf.rs.security.oauth.services.AuthorizationRequestService 
endpoint available at Social.com and appends a temporarily token as a 
query parameter to the URI of AuthorizationRequestService.  

The service uses the temp token passed along 
to retrieve the information about Restaraunt Reservations and returns an 
authorization form to the user.  This form will include a randomly generated 
key in a hidden field to prevent a so called Cross-Site Request Forgery 
attack: http://tools.ietf.org/html/rfc5849#section-4.13.  

When the user submits the form back, this key will be matched against the 
one stored in the current HTTP session instance.  

The user has a choice to Deny or Allow the access to its calendar.  If 
Allow is pressed then Authorization service will append a temp token and a 
randomly generated authorization key as query parameters to the callback URI 
provided by Restaurant Reservations and will redirect the user back to the 
client (the Reservation Service) listening on the callback URI.  

Now, the client has got a confirmation that it is allowed to access
the current user's calendar, it has the temporary token and the 
authorization key.  It now exchanges this pair for an access token by 
sending an OAuth request to an instance of 
org.apache.cxf.rs.security.oauth.services.AccessTokenService.  The access 
token request is similar to the temporary token one, except that the 
temp token and authorization key pair is also included in the Authorization 
header and client key + secret and access token + secret pairs are used 
to sign the request.  

After an access token key and secret is received back, Restaurant 
Reservations requests a current user's calendar in order to verify the 
user is actually free at the requested hour.  It signs all the requests 
using its consumer key + secret and access token + secret pairs.  

This raises a design challenge of its own.  We have a Social.com application 
and the registered users can access their own calendars.  The security 
filter ensures that the users are valid Social.com users.  And now we have 
a third party service being granted a permission to access a given user's 
calendar.  Should a third-party service be allowed to use the same URI 
that the regular Social.com users use to access their calendars ?  For 
example, should both Social.com users and third-party services be allowed 
to do "GET http://localhost:8080/services/social/calendar" ?  

That is possible in which case the security filter has to be smarter in the 
way it processes Authorization requests.  If it is "Authorization: Basic 
..." then it must be a regular user, and if it is "Authorization: OAuth 
..." then proceed with first checking the OAuth request.  This approach can be 
implemented by having a demo Social.com security filter extending 
org.apache.cxf.rs.security.oauth.filters.OAuthRequestFilter and delegating 
to it if it's an OAuth request and proceeding as usual otherwise.  But 
other challenges such as how to prevent third party users from 
executing updates such as "POST 
http://localhost:8080/services/social/calendar" have to be addressed in 
such cases - given that only read access has been granted by the user - 
please consult the CXF documentation on how to handle this and other 
possible issues.  

In this demo a much simpler yet valid option has been chosen.  Social.com 
decided to provide a JAX-RS service dedicated to handling the OAuth client 
requests only, oauth.manager.ThirdPartyAccessService.  It only supports 
GET requests, for example, "GET 
http://localhost:8080/services/thirdparty/calendar".  This makes 
it simple security-wise, as only registered OAuth clients 
having a valid access token can go via this route and thus 
org.apache.cxf.rs.security.oauth.filters.OAuthRequestFilter can be used to 
protect the resource owner's Social.com data, while only registered Social.com
users can access or update their calendars using 
"http://localhost:8080/services/social/calendar" URI which can be 
protected by a regular Social.com filter.  

So Restaurant Reservations service has finally managed to get to a current 
user's calendar via oauth.manager.ThirdPartyAccessService.  It gets a 
Calendar back, checks if a user is free at a given hour and if yes then it 
asks its private partner, Restaurant Service to get a table reserved for a 
user and returns a confirmation page with the address of the restaurant.  

Note that if the user decided to Deny the calendar access request earlier 
on, then Restaurant Reservations will get back a temp token to its 
callback URI, but no authorization key.  In this case the service will 
redirect the user yet again using a JAX-RS Response.seeOther technique to 
its handler managing the failed reservations which will return a failure 
report to the user.  

Note that a typical OAuth 1.0 server consists of three services which in 
case of CXF are: 

org.apache.cxf.rs.security.oauth.services.RequestTokenService
org.apache.cxf.rs.security.oauth.services.AccessTokenService
org.apache.cxf.rs.security.oauth.services.AuthorizationRequestService

All of these services rely on 
org.apache.cxf.rs.security.oauth.provider.OAuthDataProvider to manage 
request and access tokens associated with a given third-party client.  In 
this demo it is oauth.manager.OAuthManager which provides a primitive 
OAuthDataProvider implementation and also manages the third-party 
application registration.  


CXF's efforts to reduce OAuth complexity
----------------------------------------

As you can see from the demo description, OAuth can be quite elaborate 
though the end user should not be affected in any way, the main burden 
being on OAuth developers.  CXF strives to make producing a quality
OAuth server as straightforward as possible - please check 
the demo code to confirm that is indeed the case - there's virtually no 
OAuth-related code in the demo itself except for the required custom 
org.apache.cxf.rs.security.oauth.provider.OAuthDataProvider implementation. 
Note the oauth.thirdparty.OAuthClientManager uses CXF OAuth client utility
code to completely encapsulate the complexities of OAuth from the 
implementation of the Restaurant Reservations application.  

With OAuth 2.0 the process will become even simpler.  

