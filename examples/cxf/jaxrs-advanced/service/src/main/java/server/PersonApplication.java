/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package server;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.apache.cxf.jaxrs.provider.JSONProvider;

import service.advanced.PersonExceptionMapper;
import service.advanced.PersonInfoStorage;
import service.advanced.PersonServiceImpl;
import service.advanced.SearchService;

/*
 * Class that can be used (instead of XML-based configuration) to inform the JAX-RS 
 * runtime about the resources and providers it is supposed to deploy.  See the 
 * ApplicationServer class for more information.  
 */
@ApplicationPath("/personservice")
public class PersonApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>();
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> classes = new HashSet<Object>();

        PersonInfoStorage storage = new PersonInfoStorage();

        PersonServiceImpl personService = new PersonServiceImpl();
        personService.setStorage(storage);
        classes.add(personService);

        SearchService searchService = new SearchService();
        searchService.setStorage(storage);
        classes.add(searchService);

        // custom providers

        classes.add(new PersonExceptionMapper());

        JSONProvider provider = new JSONProvider();
        provider.setIgnoreNamespaces(true);
        classes.add(provider);

        return classes;
    }
}
