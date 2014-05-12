/*
 * ============================================================================
 *
 * Copyright (C) 2011 - 2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 *
 * ============================================================================
 */
package org.talend.esb.callcontext.store.rest.security;

public abstract class AbstractRestSecurityProvider {

	protected enum Authentication {

        NO, BASIC, SAML;

        public static Authentication fromString(String value) {
            if (null == value) {
                return NO;
            }
            for (Authentication security : Authentication.values()) {
                if (security.name().equals(value)) {
                    return security;
                }
            }
            throw new IllegalArgumentException("Unsupported authentication type: " + value);
        }
    }

    protected Authentication callContextStoreAuthentication = Authentication.NO;

    public void setCallContextStoreAuthentication(String callContextStoreAuthentication) {
        this.callContextStoreAuthentication = Authentication.fromString(callContextStoreAuthentication);
    }

}
