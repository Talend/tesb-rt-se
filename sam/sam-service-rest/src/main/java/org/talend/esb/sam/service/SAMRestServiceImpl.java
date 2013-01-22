
package org.talend.esb.sam.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.talend.esb.sam.common.event.Event;

public class SAMRestServiceImpl implements SAMRestService {

 //   private static final Logger LOG = Logger.getLogger(SAMRestServiceImpl.class.getPackage().getName());

	@Override
	public Response getEvent(String arg0) {
		// TODO Auto-generated method stub
		return Response.ok(new Event()).build();
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
		return Response.ok(new Flow()).build();
	}

	@Override
	public Response getFlows(Integer offset, List<String> params) {
		FlowCollection flowCollection = new FlowCollection();
		HashMap<String, URI> flows = new HashMap<String, URI>();
		try {
            flows.put("key", new URI("http://value"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
		flowCollection.setFlows(flows);		
		return Response.ok(flowCollection).build();
	}

}
