package org.talend.esb.sts.war.handler;

import java.io.InputStream;
import java.util.Properties;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.message.token.UsernameToken;
import org.apache.ws.security.validate.Credential;
import org.apache.ws.security.validate.Validator;

public class PropertyFileCallbackHandler implements Validator{

    private static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(PropertyFileCallbackHandler.class);
                
    @Override
    public Credential validate(Credential credential, RequestData data)
            throws WSSecurityException {
        if (credential == null || credential.getUsernametoken() == null) {
            throw new WSSecurityException(WSSecurityException.FAILURE, "noCredential");
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
            throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION);
        }
        
        password = usernameToken.getPassword();
        
        if (!WSConstants.PASSWORD_TEXT.equals(pwType)) {
            log.warn("Password type " + pwType + " not supported");
            throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION);           
        }
        
        if (!(user != null && user.length() > 0 && password != null && password.length() > 0)) {
            log.warn("User or password empty");
            throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION);
        }
        
        try {
            InputStream stream = PropertyFileCallbackHandler.class.getClassLoader().getResourceAsStream("user.properties");
            Properties properties = new Properties();
            properties.load(stream);
            String propertyPwd = (String)properties.get(user);
            if(propertyPwd == null || !propertyPwd.equalsIgnoreCase(password)) {
                log.info("Authentication failed");
                throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION);
            }
        } catch (Exception ex) {
            log.info("Authentication failed", ex);
            throw new WSSecurityException(WSSecurityException.FAILED_AUTHENTICATION);
        }
        
        return credential;
    }

}
