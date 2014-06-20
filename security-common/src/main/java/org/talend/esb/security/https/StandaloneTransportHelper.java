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
package org.talend.esb.security.https;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.transport.http.HTTPConduit;


public class StandaloneTransportHelper {

    private StandaloneTransportHelper() {
    }

    public static void configureHttpsURLConnection(String address) throws NoSuchAlgorithmException, KeyManagementException {
        if (address.startsWith("https://")) {
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, new TrustManager[] { new FakeX509TrustManager() }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }
    }

    public static void configureConduit(HTTPConduit conduit, long connectionTimeout, long receiveTimeout) {
        if (connectionTimeout != conduit.getClient().getConnectionTimeout()) {
            conduit.getClient().setConnectionTimeout(connectionTimeout);
        }
        if (receiveTimeout != conduit.getClient().getReceiveTimeout()) {
            conduit.getClient().setReceiveTimeout(receiveTimeout);
        }
        if (conduit.getTarget().getAddress().getValue().startsWith("https://")) {
            TLSClientParameters tlsClientParams = conduit.getTlsClientParameters();
            if (tlsClientParams == null) {
                tlsClientParams = new TLSClientParameters();
                conduit.setTlsClientParameters(tlsClientParams);
            }

            tlsClientParams.setTrustManagers(new TrustManager[] { new FakeX509TrustManager() });
            tlsClientParams.setDisableCNCheck(true);
        }
    }
}
