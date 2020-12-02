package org.talend.esb.security.oidc;

import java.util.HashMap;
import java.util.Map;

public class OidcConfiguration {

	public static final String OIDC_TOKEN_ENDPOINT_LOCATION = "token.endpoint";
	public static final String OIDC_VALIDATION_ENDPOINT_LOCATION = "validation.endpoint";
	public static final String OIDC_PUBLIC_CLIENT_ID = "public.client.id";
	public static final String OIDC_CLIENT_SECRET = "client.secret";
	public static final String OIDC_SCOPE = "scope";
	public static final String OIDC_CACHE_ENABLE = "cache.use" ;
	public static final String OIDC_CACHE_SIZE = "cache.size";
	public static final String OIDC_OVERLOAD_USER_HEADER = "token.overload.user.header";

	private static final String DEFAULT_OIDC_SCOPE = "openid";
	private static final String DEFAULT_PUBLIC_CLIENT_ID = "aFSloIZSXHRQtA";
	private static final int DEFAULT_OIDC_CACHE_SIZE = 100;

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

	public String getClientSecret() {

		if (System.getProperty(OIDC_CLIENT_SECRET) != null) {
			return (String) System.getProperty(OIDC_CLIENT_SECRET);
		}

		return oidcProperties.get(OIDC_CLIENT_SECRET);
	}

	public void setClientSecret(String clientSecret) {
		oidcProperties.put(OIDC_CLIENT_SECRET, clientSecret);
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

public boolean getOidcCacheEnable(){
		if ("true".equalsIgnoreCase(System.getProperty(OIDC_CACHE_ENABLE))) {
			return true;
		}

		if("true".equalsIgnoreCase(oidcProperties.get(OIDC_CACHE_ENABLE))){
			return true;
		}
			return false;
	}

	public int getOidcCacheSize(){
		try{
			if(System.getProperty(OIDC_CACHE_SIZE) != null){
				return Integer.valueOf(System.getProperty(OIDC_CACHE_SIZE));
			}

			if(oidcProperties.get(OIDC_CACHE_SIZE) != null){
				return Integer.valueOf(oidcProperties.get(OIDC_CACHE_SIZE));
			}
			return DEFAULT_OIDC_CACHE_SIZE;

		} catch(Exception e){
			return DEFAULT_OIDC_CACHE_SIZE;
		}
	}

	public boolean getOidcOverloadUserHeader(){
		if ("true".equalsIgnoreCase(System.getProperty(OIDC_OVERLOAD_USER_HEADER))) {
			return true;
		}

		if("true".equalsIgnoreCase(oidcProperties.get(OIDC_OVERLOAD_USER_HEADER))){
			return true;
		}
		return false;
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
