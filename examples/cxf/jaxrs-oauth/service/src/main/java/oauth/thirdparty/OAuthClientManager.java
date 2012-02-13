/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth.thirdparty;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import oauth.common.OAuthConstants;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.rs.security.oauth.client.OAuthClientUtils;
import org.apache.cxf.rs.security.oauth.client.OAuthClientUtils.Consumer;
import org.apache.cxf.rs.security.oauth.client.OAuthClientUtils.Token;
import org.apache.cxf.rs.security.oauth.provider.OAuthServiceException;

public class OAuthClientManager {

	private static final String DEFAULT_CLIENT_ID = "123456789";
	private static final String DEFAULT_CLIENT_SECRET = "987654321";
	
	private WebClient accessTokenService;
    private WebClient requestTokenService;
    private String authorizationServiceURI;
    private Consumer consumer = new Consumer(DEFAULT_CLIENT_ID, DEFAULT_CLIENT_SECRET);
    
	public OAuthClientManager() {
		
	}
	
	public URI getAuthorizationServiceURI(String token) {
	    return OAuthClientUtils.getAuthorizationURI(authorizationServiceURI, token);
	}
	
	public Token getRequestToken(URI callback, ReservationRequest request) {
	    try {
	        // This is an optional step. Without requesting this extra scope
	        // the client will not be able to update the user's calendar.
	        // Note that the Social.com has documented the format of this scope
	        // as updateCalendar-x where x is a specific hour.
	        // Social.com has also documented that a "readCalendar" scoped will be
	        // allocated by default
	        Map<String, String> extraParams = new HashMap<String, String>();
	        extraParams.put(org.apache.cxf.rs.security.oauth.utils.OAuthConstants.X_OAUTH_SCOPE, 
	                        OAuthConstants.UPDATE_CALENDAR_SCOPE + request.getHour());
	       
	        return OAuthClientUtils.getRequestToken(requestTokenService, consumer, callback, extraParams);
	    } catch (OAuthServiceException ex) {
            return null;
        }    
	}
	
	public Token getAccessToken(Token requestToken, String verifier) {
	    try {
	        return OAuthClientUtils.getAccessToken(accessTokenService, consumer, requestToken, verifier);
	    } catch (OAuthServiceException ex) {
	        return null;
	    }
	}
	
	public String createAuthorizationHeader(Token token, String method, String requestURI) {
		return OAuthClientUtils.createAuthorizationHeader(consumer, token, method, requestURI);
	}
	
	public void setAccessTokenService(WebClient ats) {
		this.accessTokenService = ats;
	}
    
    public void setRequestTokenService(WebClient rts) {
		this.requestTokenService = rts;
	}
	
	public void setAuthorizationURI(String uri) {
		this.authorizationServiceURI = uri;
	}
	
}
