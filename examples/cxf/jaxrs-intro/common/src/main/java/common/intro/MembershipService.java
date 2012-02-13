/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package common.intro;

import java.util.Collection;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;

/**
 * This interface describes a JAX-RS root resource. All the JAXRS annotations (except those overridden) will
 * be inherited by classes implementing it.
 */
@Path("/members")
public interface MembershipService {

    /**
     * Sub-resource locator (note the absence of HTTP Verb annotations such as GET). It locates a Person
     * instance with a provided id and delegates to it to process the request. Note that a Person sub-resource
     * may delegate to another sub-resource.
     */
    @Path("/{id}")
    public Person getMemberSubresource(@PathParam("id") int id);

    /**
     * Adds a member (person) to the membership list. It is expected to return an HTTP 201 status and Location
     * header pointing to a newly created child resource. Note that JAX-RS Response can have a status,
     * headers, and response entity returned.
     */
    @POST
    @Consumes("application/xml")
    public Response addMember(Person person);

    /**
     * Removes a member (person) from the membership list.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteMember(@PathParam("id") int id);

    /**
     * Returns an explicit collection of all members in XML format in response to HTTP GET requests
     */
    @GET
    @Produces("application/xml")
    public Collection<Person> getAllMembers();

}
