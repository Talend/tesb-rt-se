package org.talend.esb.sam.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.server.ui.CriteriaAdapter;

public class SAMRestServiceImpl implements SAMRestService {

    SAMProvider provider;

    @Context
    protected UriInfo uriInfo;

    public void setProvider(SAMProvider provider) {
        this.provider = provider;
    }

    @Override
    public Response getEvent(String arg0) {
        return Response.ok(provider.getEventDetails(arg0)).build();
    }

    @Override
    public Response getEvents(Integer offset, Integer limit, List<String> params) {
        CriteriaAdapter adapter = new CriteriaAdapter(offset, limit, convertParams(params));
        EventCollection eventCollection = new EventCollection();
        List<Event> events = provider.getEvents(adapter);
        Map<String, URI> eventLinks = new HashMap<String, URI>();
        for (Event event : events) {
            try {
            	eventLinks.put("", new URI(uriInfo.getBaseUri().toString().concat("/event/").concat(event.getPersistedId().toString())));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        eventCollection.setEvents(eventLinks);
        return Response.ok(eventCollection).build();
    }

    @Override
    public Response getFlow(String flowID) {
        FlowDetails flowDetails = new FlowDetails();
        flowDetails.setId(flowID);
        List<FlowEvent> flowEvents = provider.getFlowDetails(flowID);
        for (FlowEvent flow : flowEvents) {
            try {
                flow.setUri(new URI(uriInfo.getBaseUri().toString().concat("/event/").concat(flow.getId())));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        flowDetails.setEvents(flowEvents);
        return Response.ok(flowDetails).build();
    }

    @Override
    public Response getFlows(Integer offset, Integer limit, List<String> params) {
        CriteriaAdapter adapter = new CriteriaAdapter(offset, limit, convertParams(params));
        FlowCollection flowCollection = new FlowCollection();
        List<Flow> flows = provider.getFlows(adapter);
        for (Flow flow : flows) {
            try {
                flow.setUri(new URI(uriInfo.getBaseUri().toString().concat("/flow/").concat(flow.getId())));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        flowCollection.setFlows(flows);
        return Response.ok(flowCollection).build();
    }

    private Map<String, String[]> convertParams(List<String> params) {
        Map<String, String[]> paramsMap = new HashMap<String, String[]>();
        for (String param : params) {
            String[] p = param.split(",");
            if (p.length == 2) {
                paramsMap.put(p[0], new String[] { p[1] });
            }
        }
        return paramsMap;
    }

}
