Introduction
---------------------------------------

When Social.com, Reservations and OAuth2.0 web applications are running on different 
HTTP ports, having users to authenticate with every web application is not ideal.
This is an advanced variant of the jaxrs-oauth2 demo where SAML Web Single Sign-On Profile
is activated to let users authenticate with IDP only once.

Shibboleth IDP is used as a default IDP provider. Users are encouraged to experiment with
alterntive IDPs they are likely to work with in their production environments.


Building the Demo
---------------------------------------

The web applications in this directory are built as part of the complete 
jaxrs_oauth2 demo build, but all the web applications can be built individually
too.

The following applications are built:
1. social-app-war: Social.com application, 
   runs on HTTP port 9995
2. oauth-war: OAuth 2.0 application, hosts Authorization and Access Token services
   runs on HTTP port 9996
3. reservations-war: Restaraunt Reservations application,
   runs on HTTP port 9997
4. samlp-racs-war: SAML SSO SP Request Assertion Consumer Service,
   runs on HTTP port 9998

Starting the web applications
---------------------------------------
 * In the servlet container

 1.   cd sso-saml/social-app-war; 
      mvn jetty:run-war
 2.   cd sso-saml/oauth-war; 
      mvn jetty:run-war
 3.   cd sso-saml/reservations-war; 
      mvn jetty:run-war
 4.   cd sso-saml/samlp-racs-war; 
      mvn jetty:run-war
    
Starting IDP
---------------------------------------

Please see idp/shibboleth/README.txt on how to install and run Shibboleth IDP.
Alternative IDP providers can be installed if preferred.

Running the client
---------------------------------------
 
* From the browser

- Go to "https://localhost:9556/oauth/forms/registerApp.jsp" and register
  a custom third party application.  
- Follow the link in the bottom of the returned Consumer Application 
  Registration Confirmation page in order to register a user with 
  Social.com.
- The Social.com User Registration Form asks for a user name and password.  
  Enter "barry@social.com" (name), "barry" (account alias),  "1234" (password).
  Note that setting an alias is optional, if not set then the account name 
  ("barry@social.com") will be used as an alias. 
- Press "Register With Social.com" to complete the acount registration.  
- Follow the link in the bottom of the returned User Registration 
  Confirmation page in order to view the personal UserAccount page 
- You will be redirected to the IDP Authentication page.
- When asked please authenticate with the IDP service using the 
  "barry" and "1234" pair.
- View the account page, Note that Calendar has no reserved events.
- Follow the link in the bottom of the User Account page in order to try
  the online Restaurant Reservations service.  
- The Restaurant Reservations Form offers an option to book a restaurant 
  table at a specific hour, press Reserve to start the process.  
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

TODO
