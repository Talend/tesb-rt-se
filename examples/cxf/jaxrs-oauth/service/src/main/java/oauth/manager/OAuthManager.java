/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import oauth.common.OAuthConstants;

import org.apache.cxf.rs.security.oauth.data.AccessToken;
import org.apache.cxf.rs.security.oauth.data.AccessTokenRegistration;
import org.apache.cxf.rs.security.oauth.data.AuthorizationInput;
import org.apache.cxf.rs.security.oauth.data.Client;
import org.apache.cxf.rs.security.oauth.data.OAuthPermission;
import org.apache.cxf.rs.security.oauth.data.RequestToken;
import org.apache.cxf.rs.security.oauth.data.RequestTokenRegistration;
import org.apache.cxf.rs.security.oauth.data.Token;
import org.apache.cxf.rs.security.oauth.provider.OAuthDataProvider;
import org.apache.cxf.rs.security.oauth.provider.OAuthServiceException;

public class OAuthManager implements OAuthDataProvider {

    private static final OAuthPermission READ_CALENDAR_PERMISSION;
    static {
        READ_CALENDAR_PERMISSION = new OAuthPermission(
                OAuthConstants.READ_CALENDAR_SCOPE, 
                OAuthConstants.READ_CALENDAR_DESCRIPTION, 
                Collections.<String>emptyList());
        READ_CALENDAR_PERMISSION.setDefault(true);
    }
    
	private Client client;
	private RequestToken rt;
	private AccessToken at;
	
	public void registerClient(Client c) {
	    this.client = c;
	}
	
	public AccessToken createAccessToken(AccessTokenRegistration reg) throws OAuthServiceException {
	    RequestToken rt = reg.getRequestToken();
		String tokenId = UUID.randomUUID().toString();
		String tokenSecret = UUID.randomUUID().toString();
		at = new AccessToken(rt.getClient(), tokenId, tokenSecret);
		at.setSubject(rt.getSubject());
		at.setScopes(rt.getScopes());
		rt = null;
		return at;
	}

	public RequestToken createRequestToken(RequestTokenRegistration reg)
			throws OAuthServiceException {
		String tokenId = UUID.randomUUID().toString();
		String tokenSecret = UUID.randomUUID().toString();
		rt = new RequestToken(reg.getClient(), tokenId, tokenSecret);
		
		rt.setScopes(getPermissionsInfo(reg.getScopes()));
		rt.setCallback(reg.getCallback());
		return rt;
	}

	public String finalizeAuthorization(AuthorizationInput input)
			throws OAuthServiceException {
        RequestToken rt = input.getToken();
        if (rt.getScopes().containsAll(input.getApprovedScopes())) {
            rt.setScopes(input.getApprovedScopes());
        }
		String verifier = UUID.randomUUID().toString();
		rt.setVerifier(verifier);
		return verifier;
	}

	public AccessToken getAccessToken(String tokenId) throws OAuthServiceException {
		return at == null || !at.getTokenKey().equals(tokenId) ? null : at;
	}

	public Client getClient(String clientId) throws OAuthServiceException {
		return client == null || !client.getConsumerKey().equals(clientId) ? null : client;
	}

	private List<OAuthPermission> getPermissionsInfo(List<String> scopes) {
		List<OAuthPermission> list = new ArrayList<OAuthPermission>();
		for (String scope : scopes) {
		    if (scope.equals(OAuthConstants.READ_CALENDAR_SCOPE)) {
		        list.add(READ_CALENDAR_PERMISSION); 
		    } else if (scope.startsWith(OAuthConstants.UPDATE_CALENDAR_SCOPE)) {
		        String hourValue = scope.substring(OAuthConstants.UPDATE_CALENDAR_SCOPE.length());
		        list.add(new OAuthPermission(scope, 
		                OAuthConstants.UPDATE_CALENDAR_DESCRIPTION + hourValue + " o'clock",
		                Collections.<String>emptyList()));
		    }
		}
		if (!scopes.contains(OAuthConstants.READ_CALENDAR_SCOPE)) {
		    list.add(READ_CALENDAR_PERMISSION);
        }
		return list;
	}

	public RequestToken getRequestToken(String tokenId)
			throws OAuthServiceException {
		return rt == null || !rt.getTokenKey().equals(tokenId) ? null : rt;
	}

	public void removeToken(Token token) throws OAuthServiceException {
	    if (token instanceof RequestToken) {
		    rt = null;
	    } else {
		    at = null;
	    }
	}

}
