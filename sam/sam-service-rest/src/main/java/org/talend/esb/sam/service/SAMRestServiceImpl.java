package org.talend.esb.sam.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

public class SAMRestServiceImpl implements SAMRestService {

    SAMProvider provider;

    public void setProvider(SAMProvider provider) {
        this.provider = provider;
    }

    @Override
    public Response getEvent(String arg0) {
        return Response.ok(provider.getEventDetails(arg0)).build();
    }

    @Override
    public Response getEvents(Integer offset, List<String> params) {
        EventCollection eventCollection = new EventCollection();
        HashMap<String, URI> events = new HashMap<String, URI>();
        try {
            events.put("key", new URI("http://value"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        eventCollection.setEvents(events);
        return Response.ok(eventCollection).build();
    }

    @Override
    public Response getFlow(String flowID) {
        // TODO Auto-generated method stub
        return Response.ok(provider.getFlowDetails(flowID)).build();
    }

    @Override
    public Response getFlows(Integer offset, List<String> params) {
        return Response.ok(provider.getFlows(offset, convertParams(params))).build();
    }
    
    private Map<String, String> convertParams(List<String> params) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        for (String param : params) {
            String[] p = param.split(",");
            if(p.length == 2) {
                paramsMap.put(p[0], p[1]);
            }
        }
        return paramsMap;
    }

}
