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

import org.talend.esb.sam.server.ui.CriteriaAdapter;
import org.talend.esb.sam.server.ui.UIProvider;

import com.google.gson.JsonObject;

/**
 * The JSON API servlet serves simple requests
 *
 * @author zubairov
 *
 */
public class ListServlet extends AbstractAPIServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = -6145158514605088533L;

	@Override
	JsonObject process(HttpServletRequest req, UIProvider provider) throws Exception {
		long offset = getRequestLongParam(req, "offset", 1);
		long limit = getRequestLongParam(req, "limit", 10);
		@SuppressWarnings("unchecked")
		CriteriaAdapter adapter = new CriteriaAdapter(offset, limit, req.getParameterMap());
		return provider.getEvents(offset, getBaseUrl(req), adapter);
	}

	private long getRequestLongParam(HttpServletRequest req, String paramName, long defaultValue)
			throws Exception {
		String paramValue = req.getParameter(paramName);
		if (null == paramValue) {
			return defaultValue;
		}
		return Long.parseLong(paramValue);
	}
}
