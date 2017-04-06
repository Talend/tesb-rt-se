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
package org.talend.esb.security.openid;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;

import org.apache.cxf.helpers.IOUtils;

@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class OpenIDAccessTokenValidator implements ContainerRequestFilter {

	public OpenIDAccessTokenValidator() {
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
				org.apache.cxf.jaxrs.client.WebClient oidcWebClient = org.apache.cxf.jaxrs.client.WebClient
						.create(org.talend.esb.security.openid.OpenIDClientUtils
								.getValidationEndpointLocation(),
								java.util.Collections
										.singletonList(new org.apache.cxf.jaxrs.provider.json.JSONProvider<String>()))
						.type("application/x-www-form-urlencoded");
				javax.ws.rs.core.Response response = oidcWebClient
						.post("token="
								+ java.net.URLEncoder.encode(accessToken,
										"UTF-8")
								+ "&token_type_hint=access_token");

				try {
					Map<String, String> map = parseJson((InputStream) response
							.getEntity());

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

	private Map<String, String> parseJson(InputStream is) throws IOException {
		String str = IOUtils.readStringFromStream(is).trim();
		if (str.length() == 0) {
			return Collections.emptyMap();
		}
		if (!str.startsWith("{") || !str.endsWith("}")) {
			throw new IOException("JSON Sequence is broken");
		}
		Map<String, String> map = new LinkedHashMap<String, String>();

		str = str.substring(1, str.length() - 1).trim();
		String[] jsonPairs = str.split(",");
		for (int i = 0; i < jsonPairs.length; i++) {
			String pair = jsonPairs[i].trim();
			if (pair.length() == 0) {
				continue;
			}
			int index = pair.indexOf(":");
			String key = pair.substring(0, index).trim();
			if (key.startsWith("\"") && key.endsWith("\"")) {
				key = key.substring(1, key.length() - 1);
			}
			String value = pair.substring(index + 1).trim();
			if (value.startsWith("\"") && value.endsWith("\"")) {
				value = value.substring(1, value.length() - 1);
			}
			map.put(key, value);
		}

		return map;
	}

}
