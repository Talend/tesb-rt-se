/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package com.talend.camel.examples.springsecurity.common;

import java.util.Map;

import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.MatrixParam;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@WebService(targetNamespace = "http://hello.com")
@Path("/")
public interface HelloWorld {

    @GET
    @Produces("text/plain")
    String sayHi(@MatrixParam("text") String text);

    @POST
    @Consumes("text/xml")
    @Produces("text/plain")
    String sayHiToUser(User user);

    @Path("users")
    @GET
    @Produces("text/xml")
    @XmlJavaTypeAdapter(IntegerUserMapAdapter.class)
    Map<Integer, User> getUsers();

}
