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
package org.talend.esb.auxiliary.storage.rest.security;

import java.util.Map;

import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.rs.security.saml.SamlHeaderOutInterceptor;
import org.apache.cxf.ws.security.trust.STSClient;
import org.talend.esb.auxiliary.storage.rest.security.AbstractRestSecurityProvider;
import org.talend.esb.auxiliary.storage.rest.security.STSClientCreator;
import org.talend.esb.auxiliary.storage.rest.security.STSRESTOutInterceptor;

public abstract class AuxiliaryStorageRestClientSecurityProvider extends AbstractRestSecurityProvider {

    private String serverURL;

    private String authenticationUser;

    private String authenticationPassword;

    private Map<String, String> stsProps;

    private JAXRSClientFactoryBean cachedClientFactory = null;

    protected JAXRSClientFactoryBean getClientFactory() {
        if (null == cachedClientFactory) {
            JAXRSClientFactoryBean factoryBean = new JAXRSClientFactoryBean();
            factoryBean.setThreadSafe(true);
            factoryBean.setAddress(getServerURL());

            if (Authentication.BASIC == auxiliaryStorageAuthentication) {
                factoryBean.setUsername(authenticationUser);
                factoryBean.setPassword(authenticationPassword);
            }

            if (Authentication.SAML == auxiliaryStorageAuthentication) {
                STSClient stsClient = STSClientCreator.create(factoryBean.getBus(), stsProps);

                STSRESTOutInterceptor outInterceptor = new STSRESTOutInterceptor();
                outInterceptor.setStsClient(stsClient);

                factoryBean.getOutInterceptors().add(outInterceptor);
                factoryBean.getOutInterceptors().add(new SamlHeaderOutInterceptor());
            }

            cachedClientFactory = factoryBean;
        }
        return cachedClientFactory;
    }

    public String getServerURL() {
        return this.serverURL;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
        if (cachedClientFactory != null) {
            cachedClientFactory.setAddress(serverURL);
        }
    }

    public void setAuthenticationUser(String authenticationUser) {
        this.authenticationUser = authenticationUser;
    }

    public void setAuthenticationPassword(String authenticationPassword) {
        this.authenticationPassword = authenticationPassword;
    }

    public void setStsProps(Map<String, String> stsProps) {
        this.stsProps = stsProps;
    }

}
