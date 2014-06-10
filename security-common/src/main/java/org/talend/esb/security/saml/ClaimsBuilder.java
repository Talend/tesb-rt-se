/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.talend.esb.security.saml;

import org.apache.cxf.helpers.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ClaimsBuilder {

    // org.apache.cxf.sts.STSConstants.IDT_NS_05_05
    private static final String IDENTITY_NS_05_05 = "http://schemas.xmlsoap.org/ws/2005/05/identity";

    // org.apache.cxf.sts.STSConstants.WST_NS_05_12
    private static final String WSTRUST_NS_05_12 = "http://docs.oasis-open.org/ws-sx/ws-trust/200512";

    private static final String CLAIM_ROLE_NAME = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/role";

    private ClaimsBuilder() {
    }

    public static Element createClaimValue(String claimValue) {
        Element elClaims = createClaimsElement();

        Element elClaimValue = elClaims.getOwnerDocument().createElementNS(IDENTITY_NS_05_05, "ClaimValue");
        elClaimValue.setAttributeNS(null, "Uri", CLAIM_ROLE_NAME);

        Element elValue = elClaims.getOwnerDocument().createElementNS(IDENTITY_NS_05_05, "Value");
        elValue.setTextContent(claimValue);

        elClaimValue.appendChild(elValue);

        elClaims.appendChild(elClaimValue);

        return elClaims;
    }

    public static Element createClaimType() {
        Element elClaims = createClaimsElement();

        Element elClaimType = elClaims.getOwnerDocument().createElementNS(IDENTITY_NS_05_05, "ClaimType");
        elClaimType.setAttributeNS(null, "Uri", CLAIM_ROLE_NAME);

        elClaims.appendChild(elClaimType);

        return elClaims;
    }

    private static Element createClaimsElement() {
        Document doc = DOMUtils.createDocument();

        Element elClaims = doc.createElementNS(WSTRUST_NS_05_12, "Claims");
        elClaims.setAttributeNS(null, "Dialect", IDENTITY_NS_05_05);

        return elClaims;
    }

}
