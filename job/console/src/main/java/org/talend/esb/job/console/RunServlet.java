/*
 * #%L
 * Talend :: ESB :: Job :: Web Console
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
package org.talend.esb.job.console;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;

/**
 * Run servlet
 */
public class RunServlet extends HttpServlet {

    private BundleContext bundleContext;

    public void init(ServletConfig servletConfig) {
        ServletContext servletContext = servletConfig.getServletContext();
        bundleContext = (BundleContext) servletContext.getAttribute("osgi-bundlecontext");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doIt(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doIt(request, response);
    }

    public void doIt(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String args = request.getParameter("args");
        String error = null;
/*
        if (name != null && name.trim().length() > 0) {
            // looking for the controler
            ServiceReference ref = bundleContext.getServiceReference(Controller.class.getName());
            if (ref != null) {
                Controller controller = (Controller) bundleContext.getService(ref);
                if (controller != null) {
                    try {
                        if (args != null) {
                            String[] argArray = args.split(" ");
                            controller.run(name, argArray);
                        } else {
                            controller.run(name);
                        }
                    } catch (Exception e) {
                        error = e.getMessage();
                    }
                }
                bundleContext.ungetService(ref);
            }
        }
*/
        if (error != null) {
            response.sendRedirect("home.do?error=" + error);
        } else {
            response.sendRedirect("home.do");
        }
    }

}
