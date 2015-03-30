package org.talend.esb.sam.service.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wss4j.common.ext.WSPasswordCallback;

public class WSPasswordCallbackHandler implements CallbackHandler {
    private final String username;
    private final String password;

    public WSPasswordCallbackHandler(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback) callback;
                if (username.equals(pc.getIdentifier())) {
                    pc.setPassword(password);
                    break;
                }
            }
        }
    }

}
