/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package server;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import oauth2.manager.OAuthManager;
import oauth2.manager.ThirdPartyRegistrationService;

import org.apache.cxf.rs.security.oauth2.services.AccessTokenService;

/*
 * Class that can be used (instead of XML-based configuration) to inform the JAX-RS 
 * runtime about the resources and providers it is supposed to deploy.  See the 
 * ApplicationServer class for more information.  
 */
@ApplicationPath("/oauth")
public class OAuthManagerApplication extends Application {
	
	private OAuthManager manager;
	
    @Override
    public Set<Object> getSingletons() {
        Set<Object> classes = new HashSet<Object>();
        
        ThirdPartyRegistrationService thirdPartyService = new ThirdPartyRegistrationService();
        thirdPartyService.setDataProvider(manager);
        
        AccessTokenService ats = new AccessTokenService();
        ats.setDataProvider(manager);
        
        classes.add(thirdPartyService);
        classes.add(ats);
        return classes;
    }
    
    public void setOAuthManager(OAuthManager manager) {
        this.manager = manager;	
    }
}
