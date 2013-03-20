/*
 * #%L
 * Talend :: ESB :: Job :: Controller
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.cxf.helpers.DOMUtils;
import org.apache.cxf.ws.security.trust.claims.ClaimsCallback;

/**
 * This CallbackHandler implementation creates a Claims Element for a "role" ClaimValue for
 * a specified role name that is passed as parameter, and stores it on the ClaimsCallback
 * object.
 */
public class ClaimValueCallbackHandler implements CallbackHandler {

    private String claimValue;

    public void handle(Callback[] callbacks)
        throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof ClaimsCallback) {
                ClaimsCallback callback = (ClaimsCallback) callbacks[i];
                callback.setClaims(createClaims());
                
            } else {
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
            }
        }
    }

    /**
     * Create a Claims Element for a "role"
     */
    private Element createClaims() {
        Document doc = DOMUtils.createDocument();
        Element claimsElement =
            doc.createElementNS("http://docs.oasis-open.org/ws-sx/ws-trust/200512", "Claims");
        claimsElement.setAttributeNS(null, "Dialect", "http://schemas.xmlsoap.org/ws/2005/05/identity");

        Element claimValueElement =
            doc.createElementNS("http://schemas.xmlsoap.org/ws/2005/05/identity", "ClaimValue");
        claimValueElement.setAttributeNS(null, "Uri", "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role");
        Element value = doc.createElementNS("http://schemas.xmlsoap.org/ws/2005/05/identity", "Value");
        value.setTextContent(claimValue);
        claimValueElement.appendChild(value);

        claimsElement.appendChild(claimValueElement);

        return claimsElement;
    }

    public String getClaimValue() {
        return claimValue;
    }

    public void setClaimValue(String claimValue) {
        this.claimValue = claimValue;
    }

}
