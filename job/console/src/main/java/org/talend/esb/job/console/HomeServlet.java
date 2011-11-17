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
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * Main home servlet
 */
public class HomeServlet extends HttpServlet {

//    private BundleContext bundleContext;

//    public void init(ServletConfig servletConfig) throws ServletException {
//        ServletContext context = servletConfig.getServletContext();
//        bundleContext = (BundleContext) context.getAttribute("osgi-bundlecontext");
//    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doIt(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doIt(request, response);
    }

    public void doIt(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String embedded = request.getParameter("embedded");
        /*
        String job = request.getParameter("job");
        String action = request.getParameter("action");
        String args = request.getParameter("args");

        if (job != null && action != null && action.equals("run")) {
            String result = "fail";
            String message = "";
            ServiceReference ref = bundleContext.getServiceReference(Controller.class.getName());
            if (ref != null) {
                Controller controller = (Controller) bundleContext.getService(ref);
                if (controller != null) {
                    try {
                        if (args != null && args.length() > 0) {
                            String[] argsArray = args.split(" ");
                            controller.run(job, argsArray);
                        } else {
                            controller.run(job);
                        }
                        result = "ok";
                    } catch (Exception e) {
                        message = e.getMessage();
                    }
                }
            }
            response.sendRedirect("home.do?job=" + job + "&result=" + result + "&message=" + message);
            return;
        }
*/
        try {
            PrintWriter writer = response.getWriter();

            if (embedded == null)
                writer.println(Template.header());

            writer.println("<div id=\"toolsbar_bc\">");
            writer.println("<div id=\"dc_refresh\" class=\"bc_btn\"><div class=\"bb\"><div><a href=\"home.do\"><img src=\"img/icons/database_refresh.gif\" alt=\"Refresh\" /><span>Refresh</span></a></div></div></div>");
            writer.println("<div id=\"dc_deploy\" class=\"bc_btn\"><div><div><img src=\"img/icons/package_go.gif\" alt=\"Deploy\" /><span>Deploy</span></div></div></div>");
            writer.println("<div id=\"dc_run\" class=\"disabled bc_btn\"><div><div><img src=\"img/icons/resultset_next_d.gif\" alt=\"Run\" /><span>Run</span></div></div></div>");
            writer.println("<div id=\"dc_undeploy\" class=\"disabled bc_btn\"><div><div><img src=\"img/icons/package_d.gif\" alt=\"Undeploy\" /><span>Undeploy</span></div></div></div>");
            writer.println("</div>");

            writer.println("<div id=\"body_bc\">");
            writer.println("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" id=\"bc_grid\">");
            writer.println("\t\t  <thead>\n" +
                    "\t\t  \t<tr>\n" +
                    "\t\t  \t  <th align=\"left\">Label</th>\n" +
                    "\t\t\t  <th>Type</th>\n" +
                    "\t\t\t  <th>Name</th>\n" +
                    "\t\t\t  <th>Version</th>\n" +
                    "\t\t\t  <th>Status</th>\n" +
                    "\t\t\t</tr>\n" +
                    "\t\t  </thead>");
            writer.println("<tbody>");

            // list job
/*
            Controller controller = null;
            ServiceReference ref = bundleContext.getServiceReference(Controller.class.getName());
            if (ref != null) {
                controller = (Controller) bundleContext.getService(ref);
            }

            if (controller != null) {
                Collection<String> routes = controller.listRoutes();
                for (String routeName : routes) {
                    writer.println("<tr>");
                    writer.println("<td class=\"td0\"><span>" + routeName + "</span></td>");
                    writer.println("<td><img src=\"img/icons/route.gif\" alt=\"Route\" /><span>Route</span></td>");
                    writer.println("<td><span>" + routeName + "</span></td>");
                    writer.println("<td><span></span></td>");
                    writer.println("<td><div><img src=\"img/icons/package_go.gif\" altr=\"Deployed\" /><span>Deployed</span></div></td>");
                    writer.println("</tr>");
                }
            }

            if (ref != null) {
                bundleContext.ungetService(ref);
            }
*/
            writer.println("</tbody>");
            writer.println("</table>");
            writer.println("</div>");

            writer.println("\t<div id=\"bc_box\">\n" +
                    "\t\t<h2>Deploy new bundle</h2>\n" +
                    "\t\t<table cellpadding=\"0\" cellspacing=\"5\">\n" +
                    "\t\t <tr>\n" +
                    "\t\t \t<td><span>Bundle</span></td>\n" +
                    "<form action=\"deploy.do\" method=\"POST\" enctype=\"multipart/form-data\">" +
                    "\t\t\t<td><input type=\"file\" name=\"file\" size=\"25\" /></td>\n" +
                    "\t\t </tr>\n" +
                    "\t\t <tr><td colspan=\"3\">&nbsp;</td></tr>\n" +
                    "\t\t <tr>\n" +
                    "\t\t \t<td></td>\n" +
                    "\t\t\t<td>\n" +
                    "\t\t\t\t<input type=\"submit\" value=\"Ok\" id=\"bc_box_ok\" class=\"button\"/>&nbsp;&nbsp;\n" +
                    "\t\t\t\t<input type=\"button\" value=\"Cancel\" id=\"bc_box_cancel\" class=\"button\" />\n" +
                    "</form>" +
                    "\t\t\t</td>\n" +
                    "\t\t\t<td></td>\t\t\t\n" +
                    "\t\t </tr>\n" +
                    "\t\t</table>\n" +
                    "\t</div>");

            if (request.getParameter("error") != null) {
                writer.println("<script type=\"text/javascript\">");
                writer.println("window.alert('Job/Route Action Error: " + request.getParameter("error") + "')");
                writer.println("</script>");
            }

            if (embedded == null)
                writer.println(Template.footer());

            writer.flush();
            writer.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }


}
