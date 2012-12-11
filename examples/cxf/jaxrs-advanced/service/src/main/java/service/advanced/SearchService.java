/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package service.advanced;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.cxf.jaxrs.ext.search.SearchContext;

import common.advanced.Person;
import common.advanced.PersonCollection;
import common.advanced.PersonInfo;

/**
 * JAX-RS SearchService root resource
 */
@Path("search")
public class SearchService {

    private PersonInfoStorage storage;

    public SearchService() {
    }

    public void setStorage(PersonInfoStorage storage) {
        this.storage = storage;
    }

    @GET
    @Produces({"application/xml", "application/json"})
    @Path("person")
    public PersonCollection findPersonsWithTypedQuery(@Context SearchContext context) {
    	
    	List<Person> personList = storage.getTypedQueryPerson(context);
        
    	// Execute JPA2 query and return the result 
        return new PersonCollection(personList);
    }
    
    @GET
    @Produces({"application/xml", "application/json"})
    @Path("personinfo/{expression}")
    public List<PersonInfo> findPersonsWithTuple(@Context SearchContext context,
    		                                     @PathParam("expression") String expression) {
    	
    	return storage.getTypedQueryTuple(context, expression);
    }

}
