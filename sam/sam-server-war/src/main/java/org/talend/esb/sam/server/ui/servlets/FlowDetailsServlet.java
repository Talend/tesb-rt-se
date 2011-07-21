/*
 * #%L
 * Service Activity Monitoring :: Server
 * %%
 * Copyright (C) 2011 Talend Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.talend.esb.sam.server.ui.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.talend.esb.sam.server.ui.UIProvider;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * API Service that returns flow details
 *
 * @author zubairov
 *
 */
public class FlowDetailsServlet extends AbstractAPIServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = 4001052811324863157L;

	@Override
	void processRequest(HttpServletRequest req, HttpServletResponse resp,
			UIProvider provider) throws Exception {
		String requestURI = req.getRequestURI();
		String flowID = requestURI.substring(requestURI.lastIndexOf('/') + 1);
		JsonObject result = provider.getFlowDetails(flowID, getBaseUrl(req));
		if (result != null) {
			resp.setHeader("Cache-Control", "no-cache");
			resp.setHeader("Pragma", "no-cache");
			resp.getWriter().println(result);
		} else {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			JsonObject notFoundMsg = new JsonObject();
			notFoundMsg.add("message", new JsonPrimitive("Can't find flow with ID: " + flowID));
			resp.getWriter().print(notFoundMsg.toString());
		}
	}

}
