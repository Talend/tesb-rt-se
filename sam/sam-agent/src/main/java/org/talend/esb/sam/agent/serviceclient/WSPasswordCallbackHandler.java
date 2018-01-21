package org.talend.esb.sam.agent.serviceclient;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSPasswordCallbackHandler implements CallbackHandler {
    
    private static final transient Logger LOG = LoggerFactory.getLogger(WSPasswordCallbackHandler.class);

    private final String username;
    private final String password;

    public WSPasswordCallbackHandler(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        if (username == null) {
            LOG.debug("No user was specified in the WSPasswordCallbackHandler");
            return;
        }

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
