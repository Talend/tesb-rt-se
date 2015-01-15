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
package org.talend.esb.auxiliary.storage.client.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.cxf.jaxrs.client.WebClient;
import org.talend.esb.auxiliary.storage.common.AuxiliaryObjectFactory;
import org.talend.esb.auxiliary.storage.rest.security.AuxiliaryStorageRestClientSecurityProvider;


public abstract class AbstractAuxiliaryStorageClientRest<E> extends AuxiliaryStorageRestClientSecurityProvider {

    private String[] serverURLs;

    private int currentServerURLIndex;

    AuxiliaryObjectFactory<E> factory;

    private WebClient cachedClient = null;

    private ReentrantLock lock = new ReentrantLock();
    
    public AbstractAuxiliaryStorageClientRest() {
    	super();
    }
    
    public AbstractAuxiliaryStorageClientRest(Properties props) {
        super(props);
    }

    protected WebClient getWebClient() {
        if (null == cachedClient) {
            cachedClient = getClientFactory().createWebClient();
        }
        return cachedClient;
    }

    protected String urlEncode(String param) throws UnsupportedEncodingException {
        return URLEncoder.encode(param, "UTF-8");
    }

    public void switchServerURL(String usedUrl) {

        if (lock.tryLock()) {
            try {
                if (usedUrl.equals(getServerURL())) {
                    useAnotherURL();
                }
            } finally {
              lock.unlock();
            }
        }
    }

    public void setServerURL(String serverURL) {
        serverURLs = serverURL.split(",");
        currentServerURLIndex = 0;
        super.setServerURL(serverURLs[currentServerURLIndex]);
    }

    private void useAnotherURL() {
      currentServerURLIndex++;

      if (currentServerURLIndex >= serverURLs.length) {
          currentServerURLIndex = 0;
          super.setServerURL(serverURLs[currentServerURLIndex]);
          cachedClient = null;

          throw new RuntimeException("None of the auxiliary storage REST server(s) is available");
      }

      super.setServerURL(serverURLs[currentServerURLIndex]);
      cachedClient = null;

    }
}
