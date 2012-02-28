/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import oauth2.thirdparty.OAuthClientManager;
import oauth2.thirdparty.RestaurantReservationService;
import oauth2.thirdparty.SecurityContextFilter;

import org.apache.cxf.jaxrs.client.WebClient;

/*
 * Class that can be used (instead of XML-based configuration) to inform the JAX-RS 
 * runtime about the resources and providers it is supposed to deploy.  See the 
 * ApplicationServer class for more information.  
 */
@ApplicationPath("/reservations")
public class RestaurantReserveApplication extends Application {
    @Override
    public Set<Object> getSingletons() {
        Set<Object> classes = new HashSet<Object>();
        RestaurantReservationService reserveService = 
        	new RestaurantReservationService();
        WebClient socialService = 
        	WebClient.create("http://localhost:8080/thirdPartyAccess/calendar");
        reserveService.setSocialService(socialService);
        
        OAuthClientManager manager = new OAuthClientManager();
        manager.setAuthorizationURI("http://localhost:8080/social/authorize");
        WebClient ats = 
        	WebClient.create("http://localhost:8080/oauth/token");
        ats.accept(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
        WebClient.getConfig(ats).getHttpConduit().getClient().setReceiveTimeout(1000000L);
        manager.setAccessTokenService(ats);
        
        reserveService.setOAuthClientManager(manager);
        
        SecurityContextFilter filter = new SecurityContextFilter();
        filter.setUsers(Collections.singletonMap("barry@restaurant.com", "5678"));
        
        WebClient restaurantService = 
        	WebClient.create("http://localhost:8080/restaurant/reception");
        restaurantService.accept(MediaType.TEXT_PLAIN_TYPE).type(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
        reserveService.setRestaurantService(restaurantService);
        
        classes.add(reserveService);
        classes.add(filter);
        
        return classes;
    }
}
