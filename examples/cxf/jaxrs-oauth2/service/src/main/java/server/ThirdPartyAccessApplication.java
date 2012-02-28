/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package server;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import oauth2.manager.OAuthManager;
import oauth2.manager.ThirdPartyAccessService;
import oauth2.service.UserAccounts;

import org.apache.cxf.rs.security.oauth2.filters.OAuthRequestFilter;

/*
 * Class that can be used (instead of XML-based configuration) to inform the JAX-RS 
 * runtime about the resources and providers it is supposed to deploy.  See the 
 * ApplicationServer class for more information.  
 */
@ApplicationPath("/thirdPartyAccess")
public class ThirdPartyAccessApplication extends Application {
    
    private OAuthManager manager;
    private UserAccounts accounts;

    public void setAccounts(UserAccounts accounts) {
        this.accounts = accounts;
    }

    public void setOAuthManager(OAuthManager manager) {
        this.manager = manager;	
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> classes = new HashSet<Object>();
        
        ThirdPartyAccessService thirdPartyAccessService = new ThirdPartyAccessService();
        thirdPartyAccessService.setAccounts(accounts);
        
        classes.add(thirdPartyAccessService);
        
        OAuthRequestFilter filter = new OAuthRequestFilter();
        filter.setDataProvider(manager);
        classes.add(filter);
        
        return classes;
    }
}
