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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;

import javax.ws.rs.core.Response;

public class OidcClientUtils {

	private static OidcConfiguration oidcConfiguration = new OidcConfiguration();

	public OidcClientUtils(Map<String, String> oidcProperties) {
		oidcConfiguration = new OidcConfiguration(oidcProperties);
	}

	public OidcClientUtils(OidcConfiguration oidcConfiguration) {
		OidcClientUtils.oidcConfiguration = oidcConfiguration;
	}

	public static String getValidationEndpoint() {
		return oidcConfiguration.getValidationEndpoint();
	}

	public static String getTokenEndpoint() {
		return oidcConfiguration.getTokenEndpoint();
	}

	public static String getScope() {
		return oidcConfiguration.getScope();
	}

	public static String getPublicClientID() {
		return oidcConfiguration.getPublicClientId();
	}
	
	public static OidcConfiguration getOidcConfiguration() {
		return oidcConfiguration;
	}

	public static Map<String, String> parseJson(InputStream is)
			throws IOException {
		String str = IOUtils.readStringFromStream(is).trim();
		if (str.length() == 0) {
			return Collections.emptyMap();
		}
		if (!str.startsWith("{") || !str.endsWith("}")) {
			throw new IOException("JSON Sequence is broken: " + str);
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
	
	/**
	 *
	 * Obtain OIDC access token from token endpoint. Intended to be used by OIDC clients.
	 *
	 * @param oidcUsername
	 * @param oidcPassword
	 * @return Authentication bearer HTTP header value
	 * @throws java.lang.Exception
	 * */
	public static String oidcClientBearer(String oidcUsername, String oidcPassword) throws java.lang.Exception {
		return oidcClientBearer(oidcUsername, oidcPassword, oidcConfiguration);
	}
	
	/**
	 * 
	 * Obtain OIDC access token from token endpoint. Intended to be used by OIDC
	 * clients.
	 * 
	 * @param oidcUsername
	 * @param oidcPassword
	 * @param oidcProperties
	 * @return Authentication bearer HTTP header value
	 * @throws java.lang.Exception
	 */
	public static String oidcClientBearer(String oidcUsername,
			String oidcPassword, Map<String, String> oidcProperties)
			throws java.lang.Exception {
		return oidcClientBearer(oidcUsername, oidcPassword, new OidcConfiguration(oidcProperties));
	}

	/**
	 * 
	 * Obtain OIDC access token from token endpoint. Intended to be used by OIDC
	 * clients.
	 * 
	 * @param oidcUsername
	 * @param oidcPassword
	 * @param oidcConfiguration 
	 * @return Authentication bearer HTTP header value
	 * @throws java.lang.Exception
	 */
	public static String oidcClientBearer(String oidcUsername,
			String oidcPassword, OidcConfiguration oidcConfiguration)
			throws java.lang.Exception {
		
		if (oidcConfiguration == null) {
			throw new IllegalArgumentException(
					"OIDC configuration is not set");
		}

		if (oidcUsername == null || oidcUsername.isEmpty()) {
			throw new IllegalArgumentException(
					"OIDC username is a required parameter");
		}

		if (oidcPassword == null) {
			throw new IllegalArgumentException(
					"OIDC password is a required parameter");
		}

		String tokenEndpoint = oidcConfiguration.getTokenEndpoint();
		if (tokenEndpoint == null || tokenEndpoint.isEmpty()) {
			throw new Exception("Token endpoint setting is null or empty");
		}
		String clientId = oidcConfiguration.getPublicClientId();
		if (clientId == null || clientId.isEmpty()) {
			throw new Exception("OIDC client ID setting is null or empty");
		}
		String scope = oidcConfiguration.getScope();

		WebClient webClient = WebClient
				.create(tokenEndpoint,
						java.util.Collections
								.singletonList(new org.apache.cxf.jaxrs.provider.json.JSONProvider()))
				.type("application/x-www-form-urlencoded");
		Response response = webClient.post("grant_type=password&scope=" + scope
				+ "&username=" + oidcUsername + "&password=" + oidcPassword
				+ "&client_id=" + clientId);

		java.util.Map<String, String> responseMap;
		try {
			responseMap = parseJson((java.io.InputStream) response.getEntity());
		} catch (Exception ex) {
			throw new Exception(
					"Can not parse response from  OIDC Access Token service: ",
					ex);
		}

		if (response.getStatus() != 200) {
			if (responseMap.get("error") != null) {
				throw new Exception("OIDC Access Token request failed: "
						+ responseMap.get("error"));
			} else {
				throw new Exception("OIDC token endpoint replied with HTTTP "
						+ response.getStatus() + " on token request");
			}
		}

		if (!"Bearer".equals(responseMap.get("token_type"))) {
			throw new Exception(
					"Token returned from OIDC Access Token service is not of Bearer type");
		}

		return "Bearer " + responseMap.get("access_token");
	}
}
