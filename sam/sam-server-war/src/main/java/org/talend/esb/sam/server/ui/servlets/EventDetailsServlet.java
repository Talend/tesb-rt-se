package org.talend.esb.sam.server.ui.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.talend.esb.sam.server.ui.UIProvider;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class EventDetailsServlet extends AbstractAPIServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = -799338434124236891L;

	@Override
	void processRequest(HttpServletRequest req, HttpServletResponse resp,
			UIProvider provider) throws Exception {
		String requestURI = req.getRequestURI();
		String eventID = requestURI.substring(requestURI.lastIndexOf('/') + 1);
		JsonObject result = provider.getEventDetails(eventID);
		if (result != null) {
			resp.getWriter().println(result);
		} else {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			JsonObject notFoundMsg = new JsonObject();
			notFoundMsg.add("message", new JsonPrimitive("Can't find event with ID: " + eventID));
			resp.getWriter().print(notFoundMsg.toString());
		}
	}



}
