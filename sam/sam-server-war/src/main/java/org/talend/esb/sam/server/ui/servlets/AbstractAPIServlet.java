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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.talend.esb.sam.server.ui.UIProvider;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Common class for API Servlets
 * 
 * @author zubairov
 * 
 */
public abstract class AbstractAPIServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private Logger log = LoggerFactory.getLogger(AbstractAPIServlet.class);


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		WebApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(this.getServletContext());
		try {
			UIProvider provider = (UIProvider) ctx.getBean("uiProvider");
			resp.setContentType("application/json");
			processRequest(req, resp, provider);
		} catch (Exception e) {
			log.error("Exception processing request " + req.getRequestURI() + " with parameters " + req.getQueryString(), e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().println(toJSON(e));
		}
	}

	/**
	 * This method should be implemented by the extensions of
	 * {@link AbstractAPIServlet}
	 * 
	 * @param req
	 * @param resp
	 * @param provider
	 */
	abstract void processRequest(HttpServletRequest req,
			HttpServletResponse resp, UIProvider provider) throws Exception;

	/**
	 * Converts {@link Exception} to {@link JsonObject}
	 */
	private JsonObject toJSON(Exception e) {
		JsonObject result = new JsonObject();
		result.add("message", new JsonPrimitive(String.valueOf(e.getMessage())));
		return result;
	}

}
