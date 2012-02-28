/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth2.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import oauth2.common.OAuthConstants;

import org.apache.cxf.rs.security.oauth.common.Client;
import org.apache.cxf.rs.security.oauth.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth.grants.code.AuthorizationCodeDataProvider;
import org.apache.cxf.rs.security.oauth.grants.code.AuthorizationCodeRegistration;
import org.apache.cxf.rs.security.oauth.grants.code.ServerAuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth.provider.OAuthServiceException;

public class OAuthManager implements AuthorizationCodeDataProvider {

    private static final OAuthPermission READ_CALENDAR_PERMISSION;
    static {
        READ_CALENDAR_PERMISSION = new OAuthPermission(
                OAuthConstants.READ_CALENDAR_SCOPE, 
                OAuthConstants.READ_CALENDAR_DESCRIPTION, 
                Collections.<String>emptyList());
        READ_CALENDAR_PERMISSION.setDefault(true);
    }
    
	private Client client;
	private ServerAuthorizationCodeGrant grant;
	private ServerAccessToken at;
	
	public void registerClient(Client c) {
	    this.client = c;
	}
	public Client getClient(String clientId) throws OAuthServiceException {
		return client == null || !client.getClientId().equals(clientId) ? null : client;
	}

	public ServerAuthorizationCodeGrant createCodeGrant(
			AuthorizationCodeRegistration reg) throws OAuthServiceException {
		String code = UUID.randomUUID().toString();
		grant = new ServerAuthorizationCodeGrant(client, 
				                                 code, 
				                                 reg.getLifetime(), 
				                                 reg.getIssuedAt());
		grant.setRedirectUri(reg.getRedirectUri());
		grant.setSubject(reg.getSubject());
		
		List<String> approvedScopes = reg.getApprovedScope();
		grant.setApprovedScopes(convertScopeToPermissions(approvedScopes));
		
		return grant;
	}

	public ServerAuthorizationCodeGrant removeCodeGrant(String code)
			throws OAuthServiceException {
		ServerAuthorizationCodeGrant theGrant = null;
		if (grant.getCode().equals(code)) {
			theGrant = grant;
			grant = null;
		}
		return theGrant;
	}
	
	public void persistAccessToken(ServerAccessToken token) throws OAuthServiceException {
	    at = token;
	}

	public ServerAccessToken getAccessToken(String tokenId) throws OAuthServiceException {
		return at == null || !at.getTokenKey().equals(tokenId) ? null : at;
	}
	
	public void removeAccessToken(ServerAccessToken token) throws OAuthServiceException {
	    at = null;
	}
	
	public ServerAccessToken refreshAccessToken(String clientId, String refreshToken)
			throws OAuthServiceException {
		throw new UnsupportedOperationException();
	}

	public List<OAuthPermission> convertScopeToPermissions(List<String> scopes) {
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

}
