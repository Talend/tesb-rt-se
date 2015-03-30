/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package service;

import java.util.Collection;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.apache.cxf.ws.policy.AssertionInfoMap;
import org.apache.wss4j.policy.SP12Constants;
import org.apache.cxf.ws.security.trust.STSTokenValidator;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.handler.RequestData;
import org.apache.wss4j.dom.validate.Credential;

/**
 */
public class UTValidator extends STSTokenValidator {
    
    public Credential validate(Credential credential, RequestData data) throws WSSecurityException {
        Credential validatedCredential = super.validate(credential, data);
        
        // Assert the IssuedToken policy
        SoapMessage message = (SoapMessage)data.getMsgContext();
        AssertionInfoMap aim = message.get(AssertionInfoMap.class);
        Collection<AssertionInfo> ais = aim.get(SP12Constants.ISSUED_TOKEN);
        for (AssertionInfo ai : ais) {
            ai.setAsserted(true);
        }
        
        return validatedCredential;
    }

}
