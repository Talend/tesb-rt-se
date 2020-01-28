/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.talend.esb.security.oidc;

import org.apache.cxf.common.util.Base64Utility;
import org.apache.commons.collections.map.LRUMap;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;

@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class OidcAccessTokenValidator implements ContainerRequestFilter {

	private OidcConfiguration oidcConfiguration = OidcClientUtils.getOidcConfiguration();
	private Map<String,Map<String, String>> tokenCacheMap;
	
	public OidcAccessTokenValidator() {
	}
	
	public OidcAccessTokenValidator(OidcConfiguration oidcConfiguration) {
		this.oidcConfiguration = oidcConfiguration;
	}
	
	@Override
	public void filter(
			javax.ws.rs.container.ContainerRequestContext requestContext)
			throws java.io.IOException {


		if(oidcConfiguration.getOidcCacheEnable() && tokenCacheMap == null){
			tokenCacheMap =  (Map<String,Map<String, String>>) Collections.synchronizedMap(new LRUMap(oidcConfiguration.getOidcCacheSize()));
		}

		boolean authFailed = true;
		String username = "";
		String authzHeader = requestContext.getHeaders().getFirst(
				"Authorization");
		if (authzHeader != null && authzHeader.startsWith("Bearer ")) {
			String accessToken = authzHeader.substring("Bearer ".length());
			if (accessToken != null && !accessToken.isEmpty()) {
				String validationEndpoint = oidcConfiguration.getValidationEndpoint();
				String clientSecret = oidcConfiguration.getClientSecret();
				String publicClientId = oidcConfiguration.getPublicClientId();

				if(validationEndpoint==null){
					throw new RuntimeException("Location of Oidc validation endpoint is not set");
				}

				Map<String,String> tokenDetails = null;

				if(oidcConfiguration.getOidcCacheEnable()) {
					tokenDetails = tokenCacheMap.get(accessToken);
					//Check if the stored token is valid. If not, we have to request the IDP (the token may be refreshed on the server side)
                    if(tokenDetails !=null) {
                        String exp = (tokenDetails.get("exp") == null ? "" : tokenDetails.get("exp")) + "000";
                        if (System.currentTimeMillis() > Long.valueOf(exp) ) {
                            tokenDetails = null;
                        }
                    }
				}

				if(tokenDetails == null) {
					//No tokenDetails --> Request validationEndpoint for retrieving status and expiration date (exp) of the accessToken
					org.apache.cxf.jaxrs.client.WebClient oidcWebClient = org.apache.cxf.jaxrs.client.WebClient
							.create(validationEndpoint,
									java.util.Collections
											.singletonList(new org.apache.cxf.jaxrs.provider.json.JSONProvider<String>()))
							.type("application/x-www-form-urlencoded");
					if(clientSecret !=null){
						String AuthorizationBase64 = Base64Utility.encode((publicClientId + ":" + clientSecret).getBytes());
						oidcWebClient.header("Authorization","Basic " + AuthorizationBase64);
					}

					String tokenInfoPost = "token="
							+ java.net.URLEncoder.encode(accessToken,
							"UTF-8")
							+ "&token_type_hint=access_token";
					javax.ws.rs.core.Response response = null;

					response = oidcWebClient
							.post(tokenInfoPost);

					try {

						tokenDetails = org.talend.esb.security.oidc.OidcClientUtils
								.parseJson((InputStream) response.getEntity());

					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					if(oidcConfiguration.getOidcCacheEnable()) {
						tokenCacheMap.put(accessToken, tokenDetails);
					}

				}

				long currentTimeMs = System.currentTimeMillis();
				String exp = (tokenDetails.get("exp")==null?"":tokenDetails.get("exp")) + "000";
				String active = tokenDetails.get("active");
				username = tokenDetails.get("username");

                if(!oidcConfiguration.getOidcCacheEnable()){
                    if("true".equalsIgnoreCase(active)){
                        authFailed = false;
                    }
                }

                if(oidcConfiguration.getOidcCacheEnable()) {
                    if (Long.valueOf(exp) > currentTimeMs) {
                        authFailed = false;
                    }
                }

                if(oidcConfiguration.getOidcCacheEnable() && authFailed){
					tokenCacheMap.remove(accessToken);
				}
				/*String active = map.get("active");
				if (active != null && active.equalsIgnoreCase("true")) {
					authFailed = false;
				}
				 */



			}
		}

		if (!authFailed && oidcConfiguration.getOidcOverloadUserHeader()){
			requestContext.getHeaders().putSingle("user",username);
		}

		if (authFailed) {
			javax.ws.rs.core.Response.ResponseBuilder builder = javax.ws.rs.core.Response
					.status(javax.ws.rs.core.Response.Status.UNAUTHORIZED);
			builder.header(javax.ws.rs.core.HttpHeaders.WWW_AUTHENTICATE,
					"Bearer");
			requestContext.abortWith(builder.build());
		}

	}

}
