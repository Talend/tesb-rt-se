package org.talend.esb.security.oidc;

import java.util.HashMap;
import java.util.Map;

public class OidcConfiguration {

	public static final String OIDC_TOKEN_ENDPOINT_LOCATION = "token.endpoint";
	public static final String OIDC_VALIDATION_ENDPOINT_LOCATION = "validation.endpoint";
	public static final String OIDC_PUBLIC_CLIENT_ID = "public.client.id";
	public static final String OIDC_SCOPE = "scope";

	private static final String DEFAULT_OIDC_SCOPE = "openid";
	private static final String DEFAULT_PUBLIC_CLIENT_ID = "aFSloIZSXHRQtA";

	public Map<String, String> oidcProperties = new HashMap<String, String>();

	public OidcConfiguration() {
	}

	public OidcConfiguration(Map<String, String> oidcProperties) {
		this.oidcProperties = oidcProperties;
	}

	public String getPublicClientId() {
		
		if (System.getProperty(OIDC_PUBLIC_CLIENT_ID) != null) {
			return (String) System.getProperty(OIDC_PUBLIC_CLIENT_ID);
		} 
		
		if (null == oidcProperties.get(OIDC_PUBLIC_CLIENT_ID)) {
			return DEFAULT_PUBLIC_CLIENT_ID;
		}
		return oidcProperties.get(OIDC_PUBLIC_CLIENT_ID);
	}

	public void setPublicClientId(String publicClientId) {
		oidcProperties.put(OIDC_PUBLIC_CLIENT_ID, publicClientId);
	}

	public String getScope() {
		
		if (System.getProperty(OIDC_SCOPE) != null) {
			return (String) System.getProperty(OIDC_SCOPE);
		} 
		
		if (null == oidcProperties.get(OIDC_SCOPE)) {
			return DEFAULT_OIDC_SCOPE;
		}
		return oidcProperties.get(OIDC_SCOPE);
	}

	public void setScope(String scope) {
		oidcProperties.put(OIDC_SCOPE, scope);
	}

	public String getValidationEndpoint() {
		
		if (System.getProperty(OIDC_VALIDATION_ENDPOINT_LOCATION) != null) {
			return (String) System.getProperty(OIDC_VALIDATION_ENDPOINT_LOCATION);
		} 
		
		return oidcProperties.get(OIDC_VALIDATION_ENDPOINT_LOCATION);
	}

	public void setValidationEndpoint(String validationEndpoint) {
		oidcProperties.put(OIDC_VALIDATION_ENDPOINT_LOCATION,
				validationEndpoint);
	}

	public String getTokenEndpoint() {
		
		if (System.getProperty(OIDC_TOKEN_ENDPOINT_LOCATION) != null) {
			return (String) System.getProperty(OIDC_TOKEN_ENDPOINT_LOCATION);
		} 
		
		return oidcProperties.get(OIDC_TOKEN_ENDPOINT_LOCATION);
	}

	public void setTokenEndpoint(String tokenEndpoint) {
		oidcProperties.put(OIDC_TOKEN_ENDPOINT_LOCATION, tokenEndpoint);
	}
	
	public Map<String, String> getOidcProperties() {
		return oidcProperties;
	}
	
	
}
