/*
 * ============================================================================
 *
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 5/7 rue Salomon De Rothschild, 92150 Suresnes, France
 *
 * ============================================================================
 */
package org.talend.esb.auxiliary.storage.rest.security;

import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.Bus;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.trust.STSClient;

public class STSClientCreator {

    public static final String STS_WSDL_LOCATION = "sts.wsdl.location";
    //public static final String STS_NAMESPACE = "sts.namespace";
    public static final String STS_SERVICE_NAME = "sts.service.name";
    public static final String STS_ENDPOINT_NAME = "sts.endpoint.name";

    public static final String STS_TOKEN_TYPE = "sts.tokentype";
    public static final String STS_KEY_TYPE = "sts.keytype";
    public static final String STS_ALLOW_RENEWING = "sts.allow.renewing";

    public static STSClient create(Bus bus, Map<String, String> stsProps) {

        final STSClient stsClient = new STSClient(bus);
        stsClient.setWsdlLocation(stsProps.get(STS_WSDL_LOCATION));
        stsClient.setServiceName(stsProps.get(STS_SERVICE_NAME));
        stsClient.setEndpointName(stsProps.get(STS_ENDPOINT_NAME));

        Map<String, Object> props = new HashMap<String, Object>();
        for (Map.Entry<String, String> entry : stsProps.entrySet()) {
            if (SecurityConstants.ALL_PROPERTIES.contains(entry.getKey())) {
                String value = entry.getValue();
                value = value.startsWith("file:") ? value.replaceAll("\\\\", "/") : value;
                props.put(entry.getKey(), value);
            }
        }

        String user = stsProps.get(SecurityConstants.USERNAME);
        if (user == null) {
            user = stsProps.get("ws-" + SecurityConstants.USERNAME);
        }
        String password = stsProps.get(SecurityConstants.PASSWORD);
        if (password == null) {
            password = stsProps.get("ws-" + SecurityConstants.PASSWORD);
        }
        props.put(SecurityConstants.CALLBACK_HANDLER, new WSPasswordCallbackHandler(
                user, password));

        stsClient.setProperties(props);

        stsClient.setEnableLifetime(true);

        stsClient.setTokenType(stsProps.get(STS_TOKEN_TYPE));

        stsClient.setKeyType(stsProps.get(STS_KEY_TYPE));

        stsClient.setAllowRenewingAfterExpiry(true);
        stsClient.setAllowRenewing(Boolean.valueOf(stsProps.get(STS_ALLOW_RENEWING)));

        return stsClient;
    }

}
