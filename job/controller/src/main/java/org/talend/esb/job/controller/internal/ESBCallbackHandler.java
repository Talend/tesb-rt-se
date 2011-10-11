package org.talend.esb.job.controller.internal;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

class ESBCallbackHandler implements CallbackHandler {

    @Override
    public void handle(Callback[] callbacks) throws IOException,
            UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {

                NameCallback nc = (NameCallback) callbacks[i];
                nc.setName("karaf");

            } else if (callbacks[i] instanceof PasswordCallback) {

                PasswordCallback pc = (PasswordCallback) callbacks[i];
                pc.setPassword(new char[] {'k', 'a', 'r', 'a', 'f' });

            } else {
                throw new UnsupportedCallbackException(callbacks[i],
                        "Unrecognized Callback");
            }
        }

    }
}
