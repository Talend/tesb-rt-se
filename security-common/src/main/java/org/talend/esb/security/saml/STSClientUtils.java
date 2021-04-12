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
package org.talend.esb.security.saml;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.trust.STSClient;

public class STSClientUtils {

    private static final String STS_WSDL_LOCATION = "sts.wsdl.location";
    private static final String STS_X509_WSDL_LOCATION = "sts.x509.wsdl.location";
    private static final String STS_NAMESPACE = "sts.namespace";
    private static final String STS_SERVICE_NAME = "sts.service.name";
    private static final String STS_ENDPOINT_NAME = "sts.endpoint.name";
    private static final String STS_X509_ENDPOINT_NAME = "sts.x509.endpoint.name";

    private static final String STS_TOKEN_TYPE = "sts.tokentype";
    private static final String STS_KEY_TYPE = "sts.keytype";
    private static final String STS_ALLOW_RENEWING = "sts.allow.renewing";

    private static Map<String, Object> stsProperties;

    public STSClientUtils(Map<String, Object> stsProperties) {
        STSClientUtils.stsProperties = stsProperties;
    }

    public STSClientUtils(Map<String, Object> stsProperties, Map<String, Object> stsPropertiesOverride) {
        Map<String, Object> props = stsProperties == null ? null : new HashMap<String, Object>(stsProperties);
        if (stsPropertiesOverride != null) {
            if (props == null) {
                props = new HashMap<String, Object>(stsPropertiesOverride);
            } else {
                props.putAll(stsPropertiesOverride);
            }
        }
        STSClientUtils.stsProperties = props;
    }

    // for registry
    public static STSClient createSTSClient(Bus bus, Map<String, Object> stsProps) {
        STSClient stsClient = createClient(bus, stsProps);

        stsClient.setWsdlLocation((String) stsProps.get(STS_WSDL_LOCATION));
        stsClient.setEndpointQName(new QName(
                (String) stsProps.get(STS_NAMESPACE), (String) stsProps.get(STS_ENDPOINT_NAME)));

        return stsClient;
    }

    public static STSClient createSTSClient(Bus bus, String username, String password) {
        final Map<String, Object> stsProps = new HashMap<String, Object>(stsProperties);
        stsProps.put(SecurityConstants.USERNAME, username);
        stsProps.put(SecurityConstants.PASSWORD, password);

        return createSTSClient(bus, stsProps);
    }

    // for bpm connector
    public static STSClient createSTSX509Client(Bus bus, Map<String, Object> stsProps) {
        final STSClient stsClient = createClient(bus, stsProps);

        stsClient.setWsdlLocation((String) stsProps.get(STS_X509_WSDL_LOCATION));
        stsClient.setEndpointQName(new QName(
                (String) stsProps.get(STS_NAMESPACE), (String) stsProps.get(STS_X509_ENDPOINT_NAME)));

        return stsClient;
    }

    public static STSClient createSTSX509Client(Bus bus, String alias) {
        Map<String, Object> stsProps = new HashMap<String, Object>(stsProperties);
        stsProps.put(SecurityConstants.STS_TOKEN_USERNAME, alias);

        return createSTSX509Client(bus, stsProps);
    }

    public static void applyAuthorization(final STSClient stsClient, String role) {
        if (null != role && role.length() > 0) {
            stsClient.setClaims(ClaimsBuilder.createClaimValue(role));
        } else {
            stsClient.setClaims(ClaimsBuilder.createClaimType());
        }
    }

    private static STSClient createClient(Bus bus, Map<String, Object> stsProps) {
        final STSClient stsClient = new STSClient(bus);
        stsClient.setServiceQName(new QName(
                (String) stsProps.get(STS_NAMESPACE), (String) stsProps.get(STS_SERVICE_NAME)));

        final Map<String, Object> props = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : stsProps.entrySet()) {
            if (SecurityConstants.ALL_PROPERTIES.contains(entry.getKey())) {
                props.put(entry.getKey(), processFileURI(entry.getValue()));
            }
        }
        stsClient.setProperties(props);

        stsClient.setEnableLifetime(true);

        if (stsProps.get(STS_TOKEN_TYPE) != null) {
            stsClient.setTokenType((String) stsProps.get(STS_TOKEN_TYPE));
        }

        if (stsProps.get(STS_KEY_TYPE) != null) {
            stsClient.setKeyType((String) stsProps.get(STS_KEY_TYPE));
        }

        stsClient.setAllowRenewingAfterExpiry(true);
        if (stsProps.get(STS_ALLOW_RENEWING) != null) {
            stsClient.setAllowRenewing(Boolean.valueOf((String) stsProps.get(STS_ALLOW_RENEWING)));
        }

        return stsClient;
    }

    private static Object processFileURI(Object fileURI) {
        if (fileURI instanceof String) {
            String fileURIName = (String) fileURI;
            if (fileURIName.startsWith("file:")) {
                try {
                    return new URL(fileURIName);
                } catch (MalformedURLException e) {
                    // keep as is
                }
            }
        }
        return fileURI;
    }

}
