/*
 * #%L
 * Talend :: ESB :: Job :: Controller
 * %%
 * Copyright (C) 2011 Talend Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.talend.esb.job.controller.internal;

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
