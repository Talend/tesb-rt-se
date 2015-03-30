/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth2.thirdparty;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import oauth2.common.Calendar;
import oauth2.common.CalendarEntry;
import oauth2.common.ReservationConfirmation;
import oauth2.common.ReservationFailure;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeGrant;

@Path("reserve")
public class RestaurantReservationService {
	
    private static final Logger LOG = Logger.getLogger(RestaurantReservationService.class.getName());
    
    private static final String NO_CODE_GRANT = "nocodegrant";
    private static final String NO_REQUEST_USER = "norequestuser";
    private static final String NO_REQUEST_STATE = "norequeststate";
    private static final String NO_REQUEST_AVAILABLE = "norequeststate";
    private static final String NO_RESERVATION = "noreserve";
    private static final String NO_OAUTH_REQUEST_TOKEN = "nooauthrequest";
    private static final String NO_OAUTH_ACCESS_TOKEN = "nooauthaccess";
    private static final String CALENDAR_ACCESS_PROBLEM = "calendar_forbidden";
    private static final String CALENDAR_BUSY = "calendar_busy";
    
    private static final Map<String, String> ERROR_DESCRIPTIONS;
    static {
        ERROR_DESCRIPTIONS = new HashMap<String, String>();
        ERROR_DESCRIPTIONS.put(NO_CODE_GRANT, 
                        "The reservation can not be completed due to you denying the access to your Calendar");
        ERROR_DESCRIPTIONS.put(NO_REQUEST_USER, 
                               "No pending requests have been found, please try again");
        ERROR_DESCRIPTIONS.put(NO_REQUEST_STATE, 
                               "The request key is not available, please try again");
        ERROR_DESCRIPTIONS.put(NO_REQUEST_AVAILABLE, 
                        "The information about the request can not be located, please try again");
        ERROR_DESCRIPTIONS.put(NO_RESERVATION, 
                               "All restaurants are currently booked, please try again");
        
        ERROR_DESCRIPTIONS.put(NO_OAUTH_REQUEST_TOKEN,
                "Problem initiating the OAuth flow in order to access your Calendar" 
                + ", please report to Social.com admin");
        
        ERROR_DESCRIPTIONS.put(NO_OAUTH_ACCESS_TOKEN,
                "Problem replacing your authorization key for OAuth access token" 
                + ", please report to Social.com admin");
        ERROR_DESCRIPTIONS.put(CALENDAR_ACCESS_PROBLEM, 
                               "Social.com refused to return a calendar, please try again");
        ERROR_DESCRIPTIONS.put(CALENDAR_BUSY, 
                         "You are busy at the requested hour, please check your calendar");
    }
    
    
	@Context
	private SecurityContext sc;
	@Context
	private UriInfo ui;
	
	private Map<String, Map<String, ReservationRequest>> requests = new
	    HashMap<String, Map<String, ReservationRequest>>();
	
	private WebClient socialService;
    private WebClient restaurantService;
    
    private OAuthClientManager manager;
    
    private AtomicInteger stateCounter = new AtomicInteger(); 
    
    public void setOAuthClientManager(OAuthClientManager manager) {
    	this.manager = manager;
    }
    
    public void setSocialService(WebClient socialService) {
    	// The timeout is set to simplify debugging on the server side;
		// otherwise the connection may time-out too early
		WebClient.getConfig(socialService).getHttpConduit().getClient().setReceiveTimeout(1000000);
		this.socialService = socialService;
	}
    
    public void setRestaurantService(WebClient restaurantService) {
		this.restaurantService = restaurantService;
	}

    @GET
    @Path("failure")
    @Produces({"text/html", "application/xml;q=0.9" })
    public ReservationFailure handleReservationFailure(@QueryParam("code") String errorCode) {
        LOG.info("Handling the reservation failure");
        
        String message = ERROR_DESCRIPTIONS.get(errorCode);
        return new ReservationFailure(message);
    }
    
	@GET
	@Path("complete")
	@Produces({"text/html", "application/xml;q=0.9" })
	public Response completeReservation(@QueryParam("code") String code,
    		                            @QueryParam("state") String state) {
		
	    String userName = sc.getUserPrincipal().getName();
		Map<String, ReservationRequest> userRequests = requests.get(userName);
		if (userRequests == null) {
		    return redirectToFailureHandler(NO_REQUEST_USER);
		}
		if (state == null) {
            return redirectToFailureHandler(NO_REQUEST_STATE);
        }
		ReservationRequest request = userRequests.remove(state);
		if (request == null) {
		    return redirectToFailureHandler(NO_REQUEST_AVAILABLE);
		}
		
		if (code == null) {
            return redirectToFailureHandler(NO_CODE_GRANT);
        }
		
		LOG.info("Completing the reservation request for a user: " + request.getReserveName());
        
		
		AuthorizationCodeGrant codeGrant = new AuthorizationCodeGrant(code, getCallbackURI());
		LOG.info("Requesting OAuth server to replace an authorized request token with an access token");
		ClientAccessToken accessToken = manager.getAccessToken(codeGrant);
		if (accessToken == null) {
		    return redirectToFailureHandler(NO_OAUTH_ACCESS_TOKEN);
		}
		
		
		Calendar c = null;
		try {
			String authHeader = manager.createAuthorizationHeader(accessToken);
	        socialService.replaceHeader("Authorization", authHeader);
	        
	        c = socialService.get(Calendar.class);
		} catch (RuntimeException ex) {
		    return redirectToFailureHandler(CALENDAR_ACCESS_PROBLEM);
		}
		
    	CalendarEntry entry = c.getEntry(request.getHour());
		if (entry.getEventDescription() == null || entry.getEventDescription().trim().isEmpty()) { 
			String address = restaurantService.post(new Form().param("name", request.getReserveName()) 
					                     .param("phone", request.getContactPhone()) 
					                     .param("hour", Integer.toString(request.getHour())),
					                      String.class);
			if (address == null) {
			    return redirectToFailureHandler(NO_RESERVATION);
			}
			
            // update the user's calendar
			String authHeader = manager.createAuthorizationHeader(accessToken);
            socialService.replaceHeader("Authorization", authHeader);
            
            Response response = socialService.form(new Form().param("hour", Integer.toString(request.getHour()))
			                             .param("description", "Table reserved at " + address));
            boolean calendarUpdated = response.getStatus() == 200 || response.getStatus() == 204;
			
			return Response.ok(new ReservationConfirmation(address, request.getHour(), calendarUpdated))
			               .build();
		} else {
		    return redirectToFailureHandler(CALENDAR_BUSY);
		}
    }
	
	private Response redirectToFailureHandler(String code) {
	    URI handlerUri = getBaseUriBuilder().path("failure").queryParam("code", code).build();
	    return Response.seeOther(handlerUri).build();
	}
	
	@POST
	@Path("table")
    public Response reserveTable(@FormParam("name") String name,
    		                     @FormParam("phone") String phone,
    		                     @FormParam("hour") int hour) {
		
	    LOG.info("Reservation request from a user " + name + " has been received");
	    
	    LOG.info("Requesting a temporarily token from the OAuth server");
		
		ReservationRequest request = new ReservationRequest();
        request.setReserveName(name);
        request.setContactPhone(phone);
        request.setHour(hour);
        
		
		String userName = sc.getUserPrincipal().getName();
        
		Integer state = stateCounter.addAndGet(1);
		synchronized (requests) {
			Map<String, ReservationRequest> userRequests = requests.get(userName);
			if (userRequests == null) {
				userRequests = new HashMap<String, ReservationRequest>(); 
				requests.put(userName, userRequests);
			}
			userRequests.put(state.toString(), request);
		}
		
		LOG.info("Persisting the reservation details and redirecting"
		        + " the current user to OAuth Authorization endpoint");
		
    	// Create an authorization code request and redirect
		URI authorizationServiceURI = 
			manager.getAuthorizationServiceURI(request, getCallbackURI(), state.toString());
		return Response.seeOther(authorizationServiceURI).build();
    }
    
	private URI getCallbackURI() {
		return getBaseUriBuilder().path("complete").build();
	}
	
	private UriBuilder getBaseUriBuilder() {
	    return ui.getBaseUriBuilder().path("reserve");
	}
}
