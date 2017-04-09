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

import java.io.InputStream;
import java.util.Map;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;

@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class OidcAccessTokenValidator implements ContainerRequestFilter {

	public OidcAccessTokenValidator() {
	}

	@Override
	public void filter(
			javax.ws.rs.container.ContainerRequestContext requestContext)
			throws java.io.IOException {
		boolean authFailed = true;
		String authzHeader = requestContext.getHeaders().getFirst(
				"Authorization");
		if (authzHeader != null && authzHeader.startsWith("Bearer ")) {
			String accessToken = authzHeader.substring("Bearer ".length());
			if (accessToken != null && !accessToken.isEmpty()) {
				String validationEndpoint =org.talend.esb.security.oidc.OidcClientUtils
						.getValidationEndpointLocation();
				
				if(validationEndpoint==null){
					throw new RuntimeException("Location of Oidc validation endpoint is not set");
				}
				org.apache.cxf.jaxrs.client.WebClient oidcWebClient = org.apache.cxf.jaxrs.client.WebClient
						.create(validationEndpoint,
								java.util.Collections
										.singletonList(new org.apache.cxf.jaxrs.provider.json.JSONProvider<String>()))
						.type("application/x-www-form-urlencoded");
				javax.ws.rs.core.Response response = oidcWebClient
						.post("token="
								+ java.net.URLEncoder.encode(accessToken,
										"UTF-8")
								+ "&token_type_hint=access_token");

				try {
					Map<String, String> map = org.talend.esb.security.oidc.OidcClientUtils
							.parseJson((InputStream) response.getEntity());

					String active = map.get("active");
					if (active != null && active.equalsIgnoreCase("true")) {
						authFailed = false;
					}
				} catch (Exception e) {
				}

			}
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
