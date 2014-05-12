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
package org.talend.esb.callcontext.store.client.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.cxf.jaxrs.client.WebClient;
import org.talend.esb.callcontext.store.common.CallContextFactory;
import org.talend.esb.callcontext.store.rest.security.CallContextStoreRestClientSecurityProvider;


public abstract class AbstractCallContextStoreClientRest<E> extends CallContextStoreRestClientSecurityProvider {

    CallContextFactory<E> factory;
    
    private WebClient cachedClient = null;

    protected WebClient getWebClient() {
        if (null == cachedClient) {
            cachedClient = getClientFactory().createWebClient();
        }
        return cachedClient;
    }

    protected String urlEncode(String param) throws UnsupportedEncodingException {
        return URLEncoder.encode(param, "UTF-8");
    } 
}
