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
package org.talend.esb.auxiliary.storage.rest.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSPasswordCallbackHandler implements CallbackHandler {

    private static final transient Logger LOG = LoggerFactory.getLogger(WSPasswordCallbackHandler.class);

    private final String user;
    private final String pass;

    public WSPasswordCallbackHandler(String username, String password) {
        user = username;
        pass = password;
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        if (user == null) {
            LOG.debug("No user was specified in the WSPasswordCallbackHandler");
            return;
        }

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
