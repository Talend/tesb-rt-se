/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

public class ServiceKeystorePasswordCallback implements CallbackHandler {
    
    private Map passwords = 
        new HashMap();
    
    public ServiceKeystorePasswordCallback() {
        passwords.put("myservicekey", "skpass");
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            WSPasswordCallback pc = (WSPasswordCallback)callbacks[i];

            String pass = (String) passwords.get(pc.getIdentifier());
            if (pass != null) {
                pc.setPassword(pass);
                return;
            }
        }
    }   
}

