/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package common.advanced;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * This interface describes a JAX-RS root resource. All the JAXRS annotations (except those overridden) will
 * be inherited by classes implementing it.
 */
@Path("/main")
public interface PersonService {

    /**
     * Returns an explicit collection of persons in either XML or JSON formats in response to HTTP GET
     * requests. Note that in order to demonstrate that the JAX-RS runtime manages the initialization of both
     * method parameters, Integer types are used. Primitive types such as 'int' could have been used instead.
     * 
     * @param start Starting index (not ID) of the person to return, 0 by default (as indicated by the
     *            DefaultValue annotation)
     * @param size Number of persons to return, -1 to signify all persons (as indicated by the DefaultValue
     *            annotation)
     */
    @GET
    @Produces({
        "application/xml", "application/json"
    })
    Response getPersons(@DefaultValue("0") @QueryParam("start") Integer start,
                        @DefaultValue("-1") @QueryParam("size") Integer size);

    /**
     * Sub-resource locator (note the absence of HTTP Verb annotations such as GET). It locates a Person
     * instance with a provided id and delegates to it to process the request. Note that a Person sub-resource
     * may delegate to another sub-resource. This @Path uses a regular expression to match (permit) only
     * numeric IDs from the client in order to have this locator called.
     */
    @Path("/{id:\\d+}")
    Person getPersonSubresource(@PathParam("id") Long id);

    /**
     * Adds a child to the existing Person. It is expected to return an HTTP 201 status and Location header
     * pointing to a newly created child resource. Note that JAX-RS Response can have a status, headers, and
     * response entity returned.
     */
    @POST
    @Path("{id}/children")
    @Consumes({
        "application/xml", "application/json"
    })
    Response addChild(@PathParam("id") Long id, Person child);

}
