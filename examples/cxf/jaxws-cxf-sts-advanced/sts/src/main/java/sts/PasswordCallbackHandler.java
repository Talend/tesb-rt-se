/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package sts;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.ws.security.WSPasswordCallback;

/**
 * A CallbackHandler used by the STS to provide passwords.
 */
public class PasswordCallbackHandler implements CallbackHandler {

    public void handle(Callback[] callbacks) throws IOException,
            UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
                if ("mystskey".equals(pc.getIdentifier())) {
                    pc.setPassword("stskpass");
                    break;
                } else if (pc.getIdentifier().contains("CN=Carl Client")) {
                    pc.setPassword("client-password");
                    break;
                }
            }
        }
    }
}
