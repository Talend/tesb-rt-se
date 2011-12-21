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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.talend.esb.sam.server.ui.UIProvider;

/**
 * Common class for API Servlets.
 *
 * @author zubairov
 */
public abstract class AbstractAPIServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(AbstractAPIServlet.class.getName());

    private final boolean noCache;

    private UIProvider uiProvider;

    /**
     * Instantiates a new abstract api servlet.
     */
    protected AbstractAPIServlet() {
        this.noCache = true;
    }

    /**
     * Instantiates a new abstract api servlet.
     *
     * @param cachingAllowed the caching allowed
     */
    protected AbstractAPIServlet(boolean cachingAllowed) {
        this.noCache = !cachingAllowed;
    }

    /**
     * Sets the ui provider.
     *
     * @param uiProvider the new ui provider
     */
    public void setUiProvider(UIProvider uiProvider) {
        this.uiProvider = uiProvider;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
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
            logger.log(Level.SEVERE, "Exception processing request " + req.getRequestURI()
                    + " with parameters " + req.getQueryString(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeResponse(resp, toJSON(e), callback);
        }
    }

    /**
     * This method should be implemented by the extensions of.
     *
     * @param req the servlet request
     * @param provider the provider
     * @return the json object
     * @throws Exception the exception
     * {@link AbstractAPIServlet}
     */
    abstract JsonObject process(HttpServletRequest req, UIProvider provider) throws Exception;

    /**
     * Write response.
     *
     * @param resp the servlet response
     * @param output the output object
     * @param callback the callback
     * @throws IOException Signals that an I/O exception has occurred.
     */
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
     * Converts {@link Exception} to {@link JsonObject}.
     *
     * @param e the e
     * @return the json object
     */
    private JsonObject toJSON(Exception e) {
        JsonObject result = new JsonObject();
        result.add(/*"message"*/"error", new JsonPrimitive(String.valueOf(e.getMessage())));
        return result;
    }


    /**
     * Gets the base url.
     *
     * @param req the servlet request
     * @return the base url
     */
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
