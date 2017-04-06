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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.cxf.helpers.IOUtils;

public class OpenIDClientUtils {

	private static final String OPENID_TOKEN_ENDPOINT_LOCATION = "org.talend.esb.openid.token.endpoint";
	private static final String OPENID_VALIDATION_ENDPOINT_LOCATION = "org.talend.esb.openid.validation.endpoint";
	private static final String OPENID_PUBLIC_CLIENT_ID = "org.talend.esb.openid.public.client.id";
	private static final String OPENID_SCOPE = "org.talend.esb.openid.scope";

	private static final String DEFAULT_OPENID_SCOPE = "openid";
	private static final String DEFAULT_PUBLIC_CLIENT_ID = "iam_pub_client";

	private static Map<String, String> openidProperties = new HashMap<String, String>();

	public OpenIDClientUtils(Map<String, String> openidProperties) {
		OpenIDClientUtils.openidProperties = openidProperties;
	}

	public static String getTokenEndpointLocation() {
		if (System.getProperty(OPENID_TOKEN_ENDPOINT_LOCATION) != null) {
			return (String) System.getProperty(OPENID_TOKEN_ENDPOINT_LOCATION);
		} else {
			return openidProperties.get(OPENID_TOKEN_ENDPOINT_LOCATION);
		}
	}

	public static String getValidationEndpointLocation() {
		if (System.getProperty(OPENID_VALIDATION_ENDPOINT_LOCATION) != null) {
			return (String) System
					.getProperty(OPENID_VALIDATION_ENDPOINT_LOCATION);
		} else {
			return openidProperties.get(OPENID_VALIDATION_ENDPOINT_LOCATION);
		}
	}

	public static String getPublicClientID() {
		if (System.getProperty(OPENID_PUBLIC_CLIENT_ID) != null) {
			return (String) System.getProperty(OPENID_PUBLIC_CLIENT_ID);
		} else {
			if (null == openidProperties.get(OPENID_PUBLIC_CLIENT_ID)) {
				return DEFAULT_PUBLIC_CLIENT_ID;
			}
			return openidProperties.get(OPENID_PUBLIC_CLIENT_ID);
		}
	}

	public static String getScope() {
		if (System.getProperty(OPENID_SCOPE) != null) {
			return (String) System.getProperty(OPENID_SCOPE);
		} else {
			if (null == openidProperties.get(OPENID_SCOPE)) {
				return DEFAULT_OPENID_SCOPE;
			}
			return openidProperties.get(OPENID_SCOPE);
		}
	}

	public static Map<String, String> getOpenidSettings() {
		Map<String, String> settings = new HashMap<String, String>();
		settings.put(OPENID_TOKEN_ENDPOINT_LOCATION, getTokenEndpointLocation());
		settings.put(OPENID_VALIDATION_ENDPOINT_LOCATION,
				getValidationEndpointLocation());
		settings.put(OPENID_PUBLIC_CLIENT_ID, getPublicClientID());
		settings.put(OPENID_SCOPE, getScope());
		return settings;
	}
	
	public static Map<String, String> parseJson(InputStream is) throws IOException {
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
