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

If Shibboleth is used and has already been set-up, do
     start Kerberos server (assuming Kerberos is used, see idp/shibboleth/README.txt) 
     cd ${tomcat.home}
     bin/catalina.sh run

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
- Press "Register With Social.com" to complete the account registration.  
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

The way the application works when Social.com and Reservations applications, as well as OAuth2 services, are deployed in different HTTP containers is very similar to the way the simpler application demonstrated in the main jaxrs-oauth2 demo works (which is referred to as a 'simpler mode' of operation in the follow-up text).

The main differences, apart from the fact that Social.com, Reservations and OAuth2 services are deployed into their own web applications, are as follows:
1. All the communication between the user, Social.com, Reservations and OAuth2 services is done via HTTPS.
2. In a 'simpler mode', OAuth 2.0 Authorization Service is collocated with Social.com application - to simplify the way the user authentication to both Social.com and OAuth2 service is managed; OAuth2 AccessToken service is also collocated with all the other services.  
Now, OAuth 2.0 Authorization and AccessToken Services are both deployed into a separate 'oauth' web application.
3. In a 'simpler mode', OAuth 2.0 Security filter protecting Social.com by authorizing third-party Reservations clients simply delegates to CXF OAuth2 OAuthDataProvider implementation to get the required information. Now the filter has to delegate to the remote AccessTokenValidation service due to the fact that OAuth 2.0 AccessTokenService is running in a separate oauth web application. 
 
See jaxrs-oauth2/sso-saml/social-app-war/src/main/webapp/WEB-INF/thirdPartyToSocialApp.xml.

4. In a 'simpler mode', Social.com, Reservations and OAuth2 Authorization services are all protected by a single security filter enforcing that a user (account owner) has actually authenticated (in addition to OAuth2 filters authorizing the Reservations application) 
Now, all of the these services are protected by two security filters, the first one enforcing that the Single Sign-on Security Context is valid, redirecting the user to IDP to authenticate if not, letting the request the continue if yes, and the second filter checking that the security context has a valid principal. 

See jaxrs-oauth2/sso-saml/social-app-war/src/main/webapp/WEB-INF/socialApp.xml (jaxrs:endpoint with id='socialServer'), jaxrs-oauth2/sso-saml/oauth-war/src/main/webapp/WEB-INF/oauthManager.xml (jaxrs:endpoint with id='oauthAuthorize'), jaxrs-oauth2/sso-saml/reservations-war/src/main/webapp/WEB-INF/restaurantReserve.xml (jaxrs:endpoint with id='reservationsServer').   

5. ServiceProvider RequestAssertionConsumerService (RACS) is deployed into its own samlp-racs-war application. IDP redirects all the users to this service after they authenticate with IDP.
RACS redirects the users back to the original target URI after validating the IDP response data and setting an active security context.
6. The configuration is similar to the one used in a 'simpler' mode, with Social.com, Reservations and OAuth2 configuration contexts also including the CXF HTTP Conduit configuration for enabling HTTPS.
7. Given that RACS service and SSO filters protecting Social.com, Reservations and OAuth2 Authorization endpoints need to share the SP SSO state but run as part of different web applications, the need to have a distributed state management solution in place is needed. The demo uses a simple HTTP-based client-server mechanism for keepingh the state shared. 
CXF offers an org.apache.cxf.rs.security.saml.sso.state.HTTPSPStateManager. 
sampl-racs-war application, in addition to having a RACS endpoint itself, is also hosting an HTTPSPStateManager endpoint. 
All the SSO filters in Social.com, Reservations and OAuth2 configuration contexts are injected with CXF JAX-RS client HTTPSPStateManager proxies.
Alternative distributed cache-management solutions can be implemented as needed. A simpler option which might do for simple applications is to have RACS endpoints collocated with all the web applications where SSO is required. 

   
