/*
 * ============================================================================
 *
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 5/7 rue Salomon De Rothschild, 92150 Suresnes, France
 *
 * ============================================================================
 */
package common.talend;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.wss4j.common.ext.WSPasswordCallback;

public class ClientCallbackHandler implements CallbackHandler {

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
                if ("aebert".equals(pc.getIdentifier())) {
                    pc.setPassword("aebertpassword");
                    break;
                } else if ("jdoe".equals(pc.getIdentifier())) {
                    pc.setPassword("jdoepassword");
                    break;
                } else if ("bbrindle".equals(pc.getIdentifier())) {
                    pc.setPassword("bbrindlepassword");
                    break;
                } else if ("rlambert".equals(pc.getIdentifier())) {
                    pc.setPassword("rlambertpassword");
                    break;
                }
            }
        }
    }
}
