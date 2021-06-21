/*
 * #%L
 * Talend :: ESB :: STS :: WAR
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.talend.esb.sts.war.handler;

import java.io.InputStream;
import java.util.Properties;

import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.handler.RequestData;
import org.apache.wss4j.dom.message.token.UsernameToken;
import org.apache.wss4j.dom.validate.Credential;
import org.apache.wss4j.dom.validate.Validator;
import org.apache.wss4j.dom.WSConstants;

public class PropertyFileCallbackHandler implements Validator{

    private static org.apache.commons.logging.Log log =
        org.apache.commons.logging.LogFactory.getLog(PropertyFileCallbackHandler.class);

    @Override
    public Credential validate(Credential credential, RequestData data)
            throws WSSecurityException {
        if (credential == null || credential.getUsernametoken() == null) {
            throw new WSSecurityException(WSSecurityException.ErrorCode.FAILURE, "noCredential");
        }

        String user = null;
        String password = null;

        UsernameToken usernameToken = credential.getUsernametoken();

        user = usernameToken.getName();
        String pwType = usernameToken.getPasswordType();
        if (log.isDebugEnabled()) {
            log.debug("UsernameToken user " + usernameToken.getName());
            log.debug("UsernameToken password type " + pwType);
        }

        if (usernameToken.isHashed()) {
            log.warn("Authentication failed as hashed username token not supported");
            throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
        }

        password = usernameToken.getPassword();

        if (!WSConstants.PASSWORD_TEXT.equals(pwType)) {
            log.warn("Password type " + pwType + " not supported");
            throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
        }

        if (!(user != null && user.length() > 0 && password != null && password.length() > 0)) {
            log.warn("User or password empty");
            throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
        }

        try (InputStream stream = PropertyFileCallbackHandler.class
                .getClassLoader().getResourceAsStream("user.properties")) {
            Properties properties = new Properties();
            properties.load(stream);
            String propertyPwd = (String)properties.get(user);
            if(propertyPwd == null || !propertyPwd.equalsIgnoreCase(password)) {
                log.info("Authentication failed");
                throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
            }
        } catch (Exception ex) {
            log.info("Authentication failed", ex);
            throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
        }

        return credential;
    }

}
