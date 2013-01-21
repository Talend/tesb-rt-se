package org.talend.esb.sam.service;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.talend.esb.sam.common.event.Event;

@Path("/")
public interface SAMRestService {

    @GET
    @Path("events")
    @Produces({ "application/xml" })
    EventCollection getEvents(Integer offset, Map<String, String> params);

    @GET
    @Path("event/{id}")
    @Produces({ "application/xml" })
    Event getEvent(@PathParam("id") String id);

    @GET
    @Path("flows")
    @Produces({ "application/xml" })
    FlowCollection getFlows(Integer offset, Map<String, String> params);
    
    @GET
    @Path("flow/{id}")
    @Produces({ "application/xml" })
    Flow getFlow(@PathParam("id") String id);
}
