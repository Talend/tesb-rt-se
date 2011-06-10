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

import org.talend.esb.sam.server.ui.CriteriaAdapter;
import org.talend.esb.sam.server.ui.UIProvider;

import com.google.gson.JsonObject;

/**
 * The JSON API servlet serves simple requests
 *
 * @author zubairov
 *
 */
@SuppressWarnings("serial")
public class ListServlet extends AbstractAPIServlet {

	@Override
	void processRequest(HttpServletRequest req, HttpServletResponse resp,
			UIProvider provider) throws Exception {
		long offset = req.getParameter("offset") == null ? 1 : Long.parseLong(req.getParameter("offset"));
		long limit = req.getParameter("limit") == null ? 10 : Long.parseLong(req.getParameter("limit"));
		StringBuffer url = req.getRequestURL();
		String base = url.substring(0, url.length() - req.getRequestURI().length()) + req.getContextPath() + "/api/v1.0/";
		@SuppressWarnings("unchecked")
		CriteriaAdapter adapter = new CriteriaAdapter(offset, limit, req.getParameterMap());
		JsonObject result = provider.getEvents(offset, base, adapter);
		resp.getWriter().println(result);
	}

}
