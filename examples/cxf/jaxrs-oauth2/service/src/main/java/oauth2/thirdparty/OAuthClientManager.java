/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth2.thirdparty;

import java.net.URI;

import oauth2.common.OAuthConstants;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.rs.security.oauth.client.OAuthClientUtils;
import org.apache.cxf.rs.security.oauth.client.OAuthClientUtils.Consumer;
import org.apache.cxf.rs.security.oauth.common.ClientAccessToken;
import org.apache.cxf.rs.security.oauth.grants.code.AuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth.provider.OAuthServiceException;

public class OAuthClientManager {

	private static final String DEFAULT_CLIENT_ID = "123456789";
	private static final String DEFAULT_CLIENT_SECRET = "987654321";
	
	private WebClient accessTokenService;
    private String authorizationServiceURI;
    private Consumer consumer = new Consumer(DEFAULT_CLIENT_ID, DEFAULT_CLIENT_SECRET);
    
	public OAuthClientManager() {
		
	}
	
	public URI getAuthorizationServiceURI(ReservationRequest request,
			                              URI redirectUri,
			                              String reservationRequestKey) {
		String scope = OAuthConstants.UPDATE_CALENDAR_SCOPE + request.getHour();
	    return OAuthClientUtils.getAuthorizationURI(authorizationServiceURI, 
	    		                                    consumer.getKey(),
	    		                                    redirectUri.toString(),
	    		                                    reservationRequestKey,
	    		                                    scope);
	}
	public ClientAccessToken getAccessToken(AuthorizationCodeGrant codeGrant) {
	    try {
	        return OAuthClientUtils.getAccessToken(accessTokenService, consumer, codeGrant);
	    } catch (OAuthServiceException ex) {
	        return null;
	    }
	}
	
	public String createAuthorizationHeader(ClientAccessToken token) {
		return OAuthClientUtils.createAuthorizationHeader(consumer, token);
	}
	
	public void setAccessTokenService(WebClient ats) {
		// the timeout is set to simply debugging on the server side
		WebClient.getConfig(ats).getHttpConduit().getClient().setReceiveTimeout(1000000);
		this.accessTokenService = ats;
	}
    
    public void setAuthorizationURI(String uri) {
		this.authorizationServiceURI = uri;
	}
	
}
