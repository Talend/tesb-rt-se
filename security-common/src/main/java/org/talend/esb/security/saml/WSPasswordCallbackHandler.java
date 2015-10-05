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

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.jasypt.properties.PropertyValueEncryptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSPasswordCallbackHandler implements CallbackHandler {
    
    private static final transient Logger LOG = LoggerFactory.getLogger(WSPasswordCallbackHandler.class);
    private static final String ALGORITHM = "PBEWITHSHA256AND128BITAES-CBC-BC";
    private static final String PASSWORD_ENV_NAME = "TESB_ENV_PASSWORD";
    private static final String PROVIDER_NAME = "BC";

    private final String user;
    private final String pass;

    public WSPasswordCallbackHandler(String username, String password) {
        if(PropertyValueEncryptionUtils.isEncryptedValue(password)) {
            StandardPBEStringEncryptor enc = new StandardPBEStringEncryptor();
            EnvironmentStringPBEConfig env = new EnvironmentStringPBEConfig();
            env.setProvider(new BouncyCastleProvider());
            env.setProviderName(PROVIDER_NAME);
            env.setAlgorithm(ALGORITHM);
            env.setPasswordEnvName(PASSWORD_ENV_NAME);
            enc.setConfig(env);
            pass = PropertyValueEncryptionUtils.decrypt(password, enc);
        } else {
            pass = password;
        }
        user = username;
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        if (user == null) {
            LOG.debug("No user was specified in the WSPasswordCallbackHandler");
            return;
        }
        
        for (Callback callback : callbacks) {
            if (callback instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback) callback;
                if (user.equals(pc.getIdentifier())) {
                    pc.setPassword(pass);
                    break;
                }
            }
        }
    }

}
