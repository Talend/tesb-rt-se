/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package server;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import oauth2.thirdparty.RestaurantService;

/*
 * Class that can be used (instead of XML-based configuration) to inform the JAX-RS 
 * runtime about the resources and providers it is supposed to deploy.  See the 
 * ApplicationServer class for more information.  
 */
@ApplicationPath("/restaurant")
public class RestaurantApplication extends Application {
    @Override
    public Set<Object> getSingletons() {
        Set<Object> classes = new HashSet<Object>();
        
        classes.add(new RestaurantService());
        
        return classes;
    }
}
