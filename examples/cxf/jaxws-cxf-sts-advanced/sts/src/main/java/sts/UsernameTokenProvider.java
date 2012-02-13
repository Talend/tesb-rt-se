/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */

package sts;

import java.security.Principal;

import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.sts.STSPropertiesMBean;
import org.apache.cxf.sts.token.provider.TokenProvider;
import org.apache.cxf.sts.token.provider.TokenProviderParameters;
import org.apache.cxf.sts.token.provider.TokenProviderResponse;
import org.apache.cxf.ws.security.sts.provider.STSException;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSPasswordCallback;
import org.apache.ws.security.WSSConfig;
import org.apache.ws.security.message.token.UsernameToken;
import org.w3c.dom.Document;

/**
 * A TokenProvider implementation that creates a UsernameToken.
 */
public class UsernameTokenProvider implements TokenProvider {

    private static final String TOKEN_TYPE = WSConstants.WSS_USERNAME_TOKEN_VALUE_TYPE;
    
    public boolean canHandleToken(String tokenType) {
        if (TOKEN_TYPE.equals(tokenType)) {
            return true;
        }
        return false;
    }
    
    public boolean canHandleToken(String tokenType, String realm) {
        return canHandleToken(tokenType);
    }
    
    public TokenProviderResponse createToken(TokenProviderParameters tokenParameters) {
        try {
            Document doc = DOMUtils.createDocument();
            
            Principal principal = tokenParameters.getPrincipal();
            String user = principal.getName();
            
            // Get the password
            WSPasswordCallback[] cb = {new WSPasswordCallback(user, WSPasswordCallback.USERNAME_TOKEN)};
            STSPropertiesMBean stsProperties = tokenParameters.getStsProperties();
            stsProperties.getCallbackHandler().handle(cb);
            String password = cb[0].getPassword();
            
            if (password == null || "".equals(password)) {
                throw new STSException("No password available", STSException.REQUEST_FAILED);
            }
            
            UsernameToken ut = new UsernameToken(true, doc, WSConstants.PASSWORD_TEXT);
            ut.setName(user);
            ut.setPassword(password);
            
            WSSConfig config = WSSConfig.getNewInstance();
            ut.setID(config.getIdAllocator().createId("UsernameToken-", ut));
            
            TokenProviderResponse response = new TokenProviderResponse();
            response.setToken(ut.getElement());
            response.setTokenId(ut.getID());
            
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new STSException("Error creating UsernameToken", e, STSException.REQUEST_FAILED);
        }
    }

}
