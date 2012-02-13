/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth.thirdparty;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import oauth.common.Calendar;
import oauth.common.CalendarEntry;
import oauth.common.ReservationConfirmation;
import oauth.common.ReservationFailure;

import org.apache.cxf.jaxrs.client.ServerWebApplicationException;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.form.Form;
import org.apache.cxf.rs.security.oauth.client.OAuthClientUtils.Token;

@Path("reserve")
public class RestaurantReservationService {
	
    private static final Logger LOG = Logger.getLogger(RestaurantReservationService.class.getName());
    
    private static final String NO_VERIFIER = "noverifier";
    private static final String NO_REQUEST = "norequest";
    private static final String NO_REQUEST_FOR_TOKEN = "norequesttoken";
    private static final String NO_RESERVATION = "noreserve";
    private static final String NO_OAUTH_REQUEST_TOKEN = "nooauthrequest";
    private static final String NO_OAUTH_ACCESS_TOKEN = "nooauthaccess";
    private static final String CALENDAR_ACCESS_PROBLEM = "calendar_forbidden";
    private static final String CALENDAR_BUSY = "calendar_busy";
    
    private static final Map<String, String> ERROR_DESCRIPTIONS;
    static {
        ERROR_DESCRIPTIONS = new HashMap<String, String>();
        ERROR_DESCRIPTIONS.put(NO_VERIFIER, 
                               "The reservation can not be completed due to you denying the access to your Calendar");
        ERROR_DESCRIPTIONS.put(NO_REQUEST, 
                               "No pending requests have been found, please try again");
        ERROR_DESCRIPTIONS.put(NO_REQUEST_FOR_TOKEN, 
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
    
    public void setOAuthClientManager(OAuthClientManager manager) {
    	this.manager = manager;
    }
    
    public void setSocialService(WebClient socialService) {
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
	public Response completeReservation(@QueryParam("oauth_token") String token,
    		                          @QueryParam("oauth_verifier") String verifier) {
		
	    
	    String userName = sc.getUserPrincipal().getName();
		Map<String, ReservationRequest> userRequests = requests.get(userName);
		if (userRequests == null) {
		    return redirectToFailureHandler(NO_REQUEST);
		}
		ReservationRequest request = userRequests.remove(token);
		if (request == null) {
		    return redirectToFailureHandler(NO_REQUEST_FOR_TOKEN);
		}
		
		if (verifier == null) {
            return redirectToFailureHandler(NO_VERIFIER);
        }
        
		LOG.info("Requesting OAuth server to replace an authorized request token with an access token");
		Token accessToken = manager.getAccessToken(request.getRequestToken(), verifier);
		if (accessToken == null) {
		    return redirectToFailureHandler(NO_OAUTH_ACCESS_TOKEN);
		}
		
		LOG.info("Completing the reservation request for a user: " + request.getReserveName());
        
		Calendar c = null;
		try {
		    String authHeader = manager.createAuthorizationHeader(accessToken, "GET",
	                socialService.getCurrentURI().toString());
	        socialService.replaceHeader("Authorization", authHeader);
	        
	        c = socialService.get(Calendar.class);
		} catch (ServerWebApplicationException ex) {
		    return redirectToFailureHandler(CALENDAR_ACCESS_PROBLEM);
		}
		
    	CalendarEntry entry = c.getEntry(request.getHour());
		if (entry.getEventDescription() == null || entry.getEventDescription().trim().isEmpty()) { 
			String address = restaurantService.post(new Form().set("name", request.getReserveName()) 
					                     .set("phone", request.getContactPhone()) 
					                     .set("hour", request.getHour()),
					                      String.class);
			if (address == null) {
			    return redirectToFailureHandler(NO_RESERVATION);
			}
			
            // update the user's calendar
			String authHeader = manager.createAuthorizationHeader(accessToken, "POST",
                    socialService.getCurrentURI().toString());
            socialService.replaceHeader("Authorization", authHeader);
            
            boolean calendarUpdated = true;
            try {
			    socialService.form(new Form().set("hour", request.getHour())
			                             .set("description", "Table reserved at " + address));
            } catch (ServerWebApplicationException ex) {
                calendarUpdated = false;
            }
			
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
		URI callback = getBaseUriBuilder().path("complete").build();
		
		ReservationRequest request = new ReservationRequest();
        request.setReserveName(name);
        request.setContactPhone(phone);
        request.setHour(hour);
        
		
		Token requestToken = manager.getRequestToken(callback, request);
		if (requestToken == null) {
            return redirectToFailureHandler(NO_OAUTH_REQUEST_TOKEN);
        }
		request.setRequestToken(requestToken);
	
		String userName = sc.getUserPrincipal().getName();
        
		synchronized (requests) {
			Map<String, ReservationRequest> userRequests = requests.get(userName);
			if (userRequests == null) {
				userRequests = new HashMap<String, ReservationRequest>(); 
				requests.put(userName, userRequests);
			}
			userRequests.put(requestToken.getToken(), request);
		}
		
		LOG.info("Persisting the reservation details and redirecting"
		        + " the current user to OAuth Authorization endpoint");
		
    	// Create a request token request and redirect
		return Response.seeOther(manager.getAuthorizationServiceURI(requestToken.getToken())).build();
    }
    
	private UriBuilder getBaseUriBuilder() {
	    return ui.getBaseUriBuilder().path("reserve");
	}
}
