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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.rs.security.saml.SamlHeaderOutInterceptor;
import org.apache.cxf.ws.security.trust.STSClient;
import org.talend.esb.auxiliary.storage.rest.security.AbstractRestSecurityProvider;
import org.talend.esb.auxiliary.storage.rest.security.STSClientCreator;
import org.talend.esb.security.saml.STSRESTOutInterceptor;

public abstract class AuxiliaryStorageRestClientSecurityProvider extends AbstractRestSecurityProvider {

    private String serverURL;

    private String authenticationUser;

    private String authenticationPassword;

    private Map<String, String> stsProps;

    private JAXRSClientFactoryBean cachedClientFactory = null;

    public AuxiliaryStorageRestClientSecurityProvider(){
    	super();
    }

    public AuxiliaryStorageRestClientSecurityProvider(Properties props){
    	super();
    	if(props!=null && !props.isEmpty()){
            String url = props.getProperty("auxiliary.storage.service.url");
            if (null == url || url.trim().isEmpty()) {
                throw new RuntimeException("Auxiliary Storage client URL property ['auxiliary.storage.service.url'] is not configured");
            }

        	setServerURL(url);

        	setAuxiliaryStorageAuthentication(props.getProperty("auxiliary.storage.service.authentication", Authentication.NO.name()));
            setAuthenticationUser(props.getProperty("auxiliary.storage.service.authentication.user"));
            setAuthenticationPassword(props.getProperty("auxiliary.storage.service.authentication.password"));

            Map<String, String> stsProps = new HashMap<String, String>();
            for (String propName : props.stringPropertyNames()) {
                if (propName.startsWith("ws-security.") || propName.startsWith("security.") || propName.startsWith("sts.")) {
                    stsProps.put(propName, props.getProperty(propName));
                }
            }
            setStsProps(stsProps);
    	}else{
    		throw new RuntimeException("Provided Auxiliary Storage client properties are empty");
    	}
    }

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
