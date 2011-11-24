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

	private final boolean noCache;

	private UIProvider uiProvider;

	protected AbstractAPIServlet() {
		this.noCache = true;
	}

	protected AbstractAPIServlet(boolean cachingAllowed) {
		this.noCache = !cachingAllowed;
	}

	public void setUiProvider(UIProvider uiProvider) {
		this.uiProvider = uiProvider;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		WebApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(this.getServletContext());
		UIProvider provider = null;
		if (null != ctx){
			provider = (UIProvider) ctx.getBean("uiProvider");
		}else{
			provider = uiProvider;
		}

		String callback = req.getParameter("callback");
		try {
			JsonObject result = process(req, provider);

			if (noCache) {
				resp.setHeader("Cache-Control", "no-cache, must-revalidate");
				resp.setHeader("Expires", "Thu, 09 May 1974 03:35:00 GMT");
				resp.setHeader("Pragma", "no-cache");
			}

			writeResponse(resp, result, callback);
		} catch (NotFoundException e) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			writeResponse(resp, toJSON(e), callback);
		} catch (Exception e) {
			log.error("Exception processing request " + req.getRequestURI()
					+ " with parameters " + req.getQueryString(), e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			writeResponse(resp, toJSON(e), callback);
		}
	}

	/**
	 * This method should be implemented by the extensions of
	 * {@link AbstractAPIServlet}
	 *
	 * @param req
	 * @param provider
	 */
	abstract JsonObject process(HttpServletRequest req, UIProvider provider) throws Exception;

	private void writeResponse(HttpServletResponse resp, JsonObject output, String callback)
			throws IOException {
		if (null == callback || callback.trim().isEmpty()) {
			resp.setContentType("application/json");
			resp.getWriter().println(output);
		} else {
			resp.setContentType("text/javascript");
			resp.getWriter().println(callback + "(" + output + ");");
		}
	}

	/**
	 * Converts {@link Exception} to {@link JsonObject}
	 */
	private JsonObject toJSON(Exception e) {
		JsonObject result = new JsonObject();
		result.add(/*"message"*/"error", new JsonPrimitive(String.valueOf(e.getMessage())));
		return result;
	}


	protected String getBaseUrl(HttpServletRequest req) {
		StringBuffer url = req.getRequestURL();
		// protocol://host:port
		String base = url.substring(0, url.length() - req.getRequestURI().length());
		// + application context
		base += req.getContextPath();
		// + rest base context
		base += "/api/v1.0/";
		return base;
	}
}
