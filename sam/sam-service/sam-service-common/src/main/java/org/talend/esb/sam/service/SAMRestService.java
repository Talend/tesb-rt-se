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
    @Produces({ "application/json" })
    EventCollection getEvents(Integer offset, Map<String, String> params);

    @GET
    @Path("event/{id}")
    @Produces({ "application/json" })
    Event getEvent(@PathParam("id") String id);

    @GET
    @Path("flows")
    @Produces({ "application/json" })
    FlowCollection getFlows(Integer offset, Map<String, String> params);
    
    @GET
    @Path("flow/{id}")
    @Produces({ "application/json" })
    Flow getFlow(@PathParam("id") String id);
}
