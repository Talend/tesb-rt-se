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
import oauth2.service.SecurityContextFilter;
import oauth2.service.SocialService;
import oauth2.service.UserAccounts;
import oauth2.service.UserRegistrationService;

import org.apache.cxf.rs.security.oauth2.services.AuthorizationCodeGrantService;

/*
 * Class that can be used (instead of XML-based configuration) to inform the JAX-RS 
 * runtime about the resources and providers it is supposed to deploy.  See the 
 * ApplicationServer class for more information.  
 */
@ApplicationPath("/social")
public class SocialApplication extends Application {
	
	private OAuthManager manager;
	private UserAccounts accounts;
	
	public void setAccounts(UserAccounts accounts) {
		this.accounts = accounts;
	}
	
    @Override
    public Set<Object> getSingletons() {
        Set<Object> classes = new HashSet<Object>();
        
        SocialService socialService = new SocialService();
        socialService.setAccounts(accounts);
        
        UserRegistrationService userRegService = new UserRegistrationService();
        userRegService.setAccounts(accounts);
        
        SecurityContextFilter scFilter = new SecurityContextFilter();
        scFilter.setAccounts(accounts);
        
        ThirdPartyAccessService thirdPartyAccessService = new ThirdPartyAccessService();
        thirdPartyAccessService.setAccounts(accounts);
        
        AuthorizationCodeGrantService authService = new AuthorizationCodeGrantService();
        authService.setDataProvider(manager);
                
        classes.add(socialService);
        classes.add(userRegService);
        classes.add(scFilter);
        classes.add(authService);
        classes.add(thirdPartyAccessService);
        
        return classes;
    }
    
    public void setOAuthManager(OAuthManager manager) {
        this.manager = manager;	
    }
}
