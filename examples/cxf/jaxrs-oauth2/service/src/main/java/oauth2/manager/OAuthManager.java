/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth2.manager;

import java.util.ArrayList;
import java.util.List;

import oauth2.common.OAuthConstants;

import org.apache.cxf.rs.security.oauth2.common.AccessTokenRegistration;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeDataProvider;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeRegistration;
import org.apache.cxf.rs.security.oauth2.grants.code.ServerAuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.tokens.bearer.BearerAccessToken;
import org.apache.cxf.rs.security.oauth2.tokens.refresh.RefreshToken;

public class OAuthManager implements AuthorizationCodeDataProvider {

    private static final OAuthPermission READ_CALENDAR_PERMISSION;
    static {
        READ_CALENDAR_PERMISSION = new OAuthPermission(
                OAuthConstants.READ_CALENDAR_SCOPE, 
                OAuthConstants.READ_CALENDAR_DESCRIPTION);
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

	// grant management
	public ServerAuthorizationCodeGrant createCodeGrant(
			AuthorizationCodeRegistration reg) throws OAuthServiceException {
		grant = new ServerAuthorizationCodeGrant(client, 3600L);
		grant.setRedirectUri(reg.getRedirectUri());
		grant.setSubject(reg.getSubject());
		List<String> scope = reg.getApprovedScope().isEmpty() ? reg.getRequestedScope() 
                : reg.getApprovedScope();
		grant.setApprovedScopes(scope);
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
	
	// token management
	public ServerAccessToken createAccessToken(AccessTokenRegistration reg)
		throws OAuthServiceException {
		ServerAccessToken token = new BearerAccessToken(reg.getClient(), 3600L);
		
		List<String> scope = reg.getApprovedScope().isEmpty() ? reg.getRequestedScope() 
				                                              : reg.getApprovedScope();
		token.setScopes(convertScopeToPermissions(reg.getClient(), scope));
		token.setSubject(reg.getSubject());
		token.setGrantType(reg.getGrantType());
		
		at = token;
		
		return token;
	}
	
	public ServerAccessToken getAccessToken(String tokenId) throws OAuthServiceException {
		return at == null || !at.getTokenKey().equals(tokenId) ? null : at;
	}
	
	public void removeAccessToken(ServerAccessToken token) throws OAuthServiceException {
	    at = null;
	}
	
	public ServerAccessToken refreshAccessToken(Client clientId, String refreshToken, List<String> scopes)
			throws OAuthServiceException {
		throw new UnsupportedOperationException();
	}

	public ServerAccessToken getPreauthorizedToken(Client client, List<String> scopes,
	    UserSubject subject, String grantType) throws OAuthServiceException {
		return null;
	}

	// permissions
	public List<OAuthPermission> convertScopeToPermissions(Client client, List<String> scopes) {
		List<OAuthPermission> list = new ArrayList<OAuthPermission>();
		for (String scope : scopes) {
		    if (scope.equals(OAuthConstants.READ_CALENDAR_SCOPE)) {
		        list.add(READ_CALENDAR_PERMISSION); 
		    } else if (scope.startsWith(OAuthConstants.UPDATE_CALENDAR_SCOPE)) {
		    	String description = OAuthConstants.UPDATE_CALENDAR_DESCRIPTION;
		    	
		        String hourValue = scope.substring(OAuthConstants.UPDATE_CALENDAR_SCOPE.length());
		        if (hourValue.equals("24")) {
		        	description += " any time of the day";
		        }  else {
		        	description += hourValue + " o'clock";
		        }
		        list.add(new OAuthPermission(scope, description));
		    }
		}
		if (!scopes.contains(OAuthConstants.READ_CALENDAR_SCOPE)) {
		    list.add(READ_CALENDAR_PERMISSION);
        }
		return list;
	}
	@Override
	public void revokeToken(Client client, String token, String tokenTypeHint)
			throws OAuthServiceException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public List<ServerAccessToken> getAccessTokens(Client arg0, UserSubject arg1) throws OAuthServiceException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<RefreshToken> getRefreshTokens(Client arg0, UserSubject arg1) throws OAuthServiceException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<ServerAuthorizationCodeGrant> getCodeGrants(Client arg0, UserSubject arg1)
			throws OAuthServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
