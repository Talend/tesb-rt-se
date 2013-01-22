
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
import org.talend.esb.sam.server.ui.UIProviderImpl;

import com.google.gson.JsonObject;

public class SAMRestServiceImpl implements SAMRestService {

 //   private static final Logger LOG = Logger.getLogger(SAMRestServiceImpl.class.getPackage().getName());

	@Override
	public Response getEvent(String arg0) {
	    SAMProviderImpl provider = new SAMProviderImpl();
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
	public Response getFlow(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response getFlows(Integer offset, List<String> params) {
		// TODO Auto-generated method stub
		return null;
	}

}
