/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package talend.camel.examples.jaxrsjmshttp.common;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.Oneway;

@Path("/")
@Produces({"text/xml", "application/xml" })
@Consumes({"text/xml", "application/xml" })
public interface BookStore {

    @GET
    @Path("{bookId}")
    Book getBook(@PathParam("bookId") Long id);
    
    @POST
    Response addBook(Book book);
    
    @PUT
    @Oneway
    void oneWayRequest(Book book) throws Exception;
    
    @GET
    BookList listBooks();

}
