/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package server;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import oauth.manager.OAuthManager;
import oauth.manager.ThirdPartyAccessService;
import oauth.service.SecurityContextFilter;
import oauth.service.SocialService;
import oauth.service.UserAccounts;
import oauth.service.UserRegistrationService;

import org.apache.cxf.rs.security.oauth.services.AuthorizationRequestService;

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
        scFilter.setUserRegistrationPath("registerUser");
        scFilter.setAccounts(accounts);
        
        ThirdPartyAccessService thirdPartyAccessService = new ThirdPartyAccessService();
        thirdPartyAccessService.setAccounts(accounts);
        
        AuthorizationRequestService authService = new AuthorizationRequestService();
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
