package org.talend.esb.locator.service.internal;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

public class WSPasswordCallbackHandler implements CallbackHandler {

    private final String user;
    private final String pass;

    public WSPasswordCallbackHandler(String username, String password) {
        user = username;
        pass = password;
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException,
            UnsupportedCallbackException {
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
