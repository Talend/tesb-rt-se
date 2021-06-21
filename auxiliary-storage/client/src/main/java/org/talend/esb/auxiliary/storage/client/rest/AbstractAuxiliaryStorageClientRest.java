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
package org.talend.esb.auxiliary.storage.client.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.cxf.jaxrs.client.WebClient;
import org.talend.esb.auxiliary.storage.common.AuxiliaryObjectFactory;
import org.talend.esb.auxiliary.storage.rest.security.AuxiliaryStorageRestClientSecurityProvider;


public abstract class AbstractAuxiliaryStorageClientRest<E> extends AuxiliaryStorageRestClientSecurityProvider {

    private static final String NO_REST_SERVER = "None of the auxiliary storage REST server(s) is available. ";

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

    protected void switchServerURL(String usedUrl, Exception exception) {
        try {
            switchServerURL(usedUrl);
        } catch (RuntimeException e) {
            if (NO_REST_SERVER.equals(e.getMessage())) {
                throw new RuntimeException(NO_REST_SERVER, exception);
            }
            throw e;
        }
    }

    private void useAnotherURL() {
      currentServerURLIndex++;

      if (currentServerURLIndex >= serverURLs.length) {
          currentServerURLIndex = 0;
          super.setServerURL(serverURLs[currentServerURLIndex]);
          cachedClient = null;

          throw new RuntimeException(NO_REST_SERVER);
      }

      super.setServerURL(serverURLs[currentServerURLIndex]);
      cachedClient = null;

    }
}
