/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package client;

import java.util.Properties;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import oauth2.common.Calendar;
import oauth2.common.ReservationConfirmation;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.rs.security.oauth2.common.OAuthAuthorizationData;

/**
 * OAuth demo client
 */
public final class RESTClient {

    private static final String PORT_PROPERTY = "http.port";
    private static final int DEFAULT_PORT_VALUE = 8080;

    private static final String HTTP_PORT;
    static {
        Properties props = new Properties();
        try {
            props.load(RESTClient.class.getResourceAsStream("/client.properties"));
        } catch (Exception ex) {
            throw new RuntimeException("client.properties resource is not available");
        }
        HTTP_PORT = props.getProperty(PORT_PROPERTY);
    }

    int port;

    public RESTClient() {
        this(getPort());
    }

    public RESTClient(int port) {
        this.port = port;
    }

    public void registerClientApplication() throws Exception {
    	WebClient rs = WebClient.create("http://localhost:" + port + "/services/oauth/registerProvider");
    	WebClient.getConfig(rs).getHttpConduit().getClient().setReceiveTimeout(10000000L);
    	rs.form(new Form().param("appName", "Restaurant Reservations")
    			          .param("appURI", "http://localhost:" + port + "/services/reservations/reserve")
    			          .param("appRedirectURI", "http://localhost:" + port + "/services/reservations/reserve/complete")
    			          );
    }
    
    public void createUserAccount() throws Exception {
    	WebClient rs = WebClient.create("http://localhost:" + port + "/services/register/registerUser");
    	WebClient.getConfig(rs).getHttpConduit().getClient().setReceiveTimeout(10000000L);
    	rs.form(new Form().param("user", "barry@social.com").param("password", "1234"));
    	
    	printUserCalendar();
    }
    
    private void printUserCalendar() {
    	WebClient client = createClient("http://localhost:" + port + "/services/social/accounts/calendar", 
    			"barry@social.com", "1234");
    	Calendar calendar = client.get(Calendar.class);
    	System.out.println(calendar.toString());
    }
    
    private void updateAndGetUserCalendar(int hour, String event) {
    	WebClient client = createClient("http://localhost:" + port + "/services/social/accounts/calendar", 
    			"barry@social.com", "1234");
    	Form form = new Form().param("hour", Integer.toString(hour)).param("event", event);
    	client.form(form);
    	printUserCalendar();
    }
    
    public void reserveTable() throws Exception {
    	WebClient rs = createClient("http://localhost:" + port + "/services/reservations/reserve/table", 
    			"barry@restaurant.com", "5678");
    	Response r = rs.form(new Form().param("name", "Barry")
    			                       .param("phone", "12345678")
    			                       .param("hour", "7"));
    	
    	int status = r.getStatus();
    	Object locationHeader = r.getMetadata().getFirst("Location");
    	if (status != 303 || locationHeader == null) {
    		System.out.println("OAuth flow is broken");
    	}
    	WebClient authorizeClient = createClient(locationHeader.toString(), 
    			"barry@social.com", "1234");
    	OAuthAuthorizationData data = authorizeClient.get(OAuthAuthorizationData.class);    	
    	Object authenticityCookie = authorizeClient.getResponse().getMetadata().getFirst("Set-Cookie");
    	System.out.println(authenticityCookie);
    	    	
    	Form authorizationResult = getAuthorizationResult(data);
    	authorizeClient.reset();
    	authorizeClient.to(data.getReplyTo(), false);
    	if (authenticityCookie != null) {
    		authorizeClient.header("Cookie", (String)authenticityCookie);
    	}
    	Response r2 = authorizeClient.form(authorizationResult);
    	
    	int status2 = r2.getStatus();
    	Object locationHeader2 = r2.getMetadata().getFirst("Location");
    	if (status2 != 303 || locationHeader2 == null) {
    		System.out.println("OAuth flow is broken");
    	}
    	
    	WebClient finalClient = createClient(locationHeader2.toString(), 
    			"barry@restaurant.com", "5678");
    	finalClient.accept("application/xml");
    	ReservationConfirmation confirm = finalClient.get(ReservationConfirmation.class);
    	
    	if (confirm != null) {
    		updateAndGetUserCalendar(7, "Dinner at " + confirm.getAddress());
    	} else {
    		System.out.println("Reservation failed");
    	}
    }
    
    private WebClient createClient(String address, String userName, String password) {
    	JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
    	bean.setAddress(address);
    	bean.setUsername(userName);
    	bean.setPassword(password);
    	
    	bean.getOutInterceptors().add(new LoggingOutInterceptor());
    	bean.getInInterceptors().add(new LoggingInInterceptor());
    	
    	return bean.createWebClient();
    }
    
    private Form getAuthorizationResult(OAuthAuthorizationData data) {
        Form form = new Form();
        form.param("client_id", data.getClientId());
        form.param("state", data.getState());
        form.param("scope", data.getProposedScope());
        form.param("redirect_uri", data.getRedirectUri());
        // TODO: get the user confirmation, using a popup window or a blocking cmd input
        form.param("oauthDecision", "allow");
        form.param("session_authenticity_token", data.getAuthenticityToken());
        return form;
    }
    
    public static void main(String[] args) throws Exception {

        RESTClient client = new RESTClient();
        client.registerClientApplication();
        client.createUserAccount();
        client.reserveTable();
    }

    private static int getPort() {
        try {
            return Integer.valueOf(HTTP_PORT);
        } catch (NumberFormatException ex) {
            // ignore
        }
        return DEFAULT_PORT_VALUE;
    }
}
