package org.talend.esb.job.console;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.talend.esb.job.controller.Controller;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Main home servlet
 */
public class HomeServlet extends HttpServlet {

    private BundleContext bundleContext;

    public void init(ServletConfig servletConfig) throws ServletException {
        ServletContext context = servletConfig.getServletContext();
        bundleContext = (BundleContext) context.getAttribute("osgi-bundlecontext");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doIt(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doIt(request, response);
    }

    public void doIt(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

        try {
            PrintWriter writer = response.getWriter();
            writer.println(Template.header());

            writer.println(jobList());

            writer.println("<div class=\"content\">");

            if (job != null) {

                writer.println("<h2>" + job + " job detail</h2>");
                writer.println("Job name: " + job + "<br>");
                String result = request.getParameter("result");
                String message = request.getParameter("message");
                writer.println("Status: ");
                if (result != null) {
                    writer.println(result + "<br><i>" + message + "</i><br>");
                } else {
                    writer.println("runnable<br>");
                }
                writer.println("<br><br>");
                writer.println("<form action=\"home.do\">");
                writer.println("<input type=\"hidden\" name=\"job\" value=\"" + job + "\"/>");
                writer.println("<input type=\"hidden\" name=\"action\" value=\"run\"/>");
                writer.println("Arguments (optional): <input type=\"text\" name=\"args\" size=\"100\"/>");
                writer.println("<input type=\"submit\" value=\"Run\"/>");
                writer.println("</form>");
            } else {
                writer.println("<h2>Welcome to Talend ESB Job Console</h2>");
                writer.println("<br>");
                writer.println("The available Talend Jobs are displayed on the left menu.<br>");
                writer.println("Click on a job to display job detail.<br>");
            }

            writer.println("</div>");

            writer.println(Template.footer());
            writer.flush();
            writer.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Use the job controller to get the job list and format it in HTML.
     *
     * @return the job list HTML formatted.
     */
    private String jobList() throws Exception {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<div class=\"wmenu\">\n");
        buffer.append("<h3>Jobs</h3>\n");

        Controller controller = null;
        ServiceReference ref = bundleContext.getServiceReference(Controller.class.getName());
        if (ref != null) {
            controller = (Controller) bundleContext.getService(ref);
        }
        if (controller != null) {
            List<String> jobs = controller.list();
            for (String job : jobs) {
                buffer.append("<a href=\"home.do?job=" + job + "\">" + job + "</a><br>\n");
            }
        } else {
            buffer.append("<h2><font style=\"color: red\">Job controller not found</font></h2><br>\n");
        }
        buffer.append("<br>");

        buffer.append("<div class=\"wmenufooter\">\n");
        buffer.append("<form action=\"uploadjob.do\" method=\"POST\" enctype=\"multipart/form-data\">\n");
        buffer.append("<input class=\"searchform\" type=\"file\" name=\"job\" size=\"15\"/>\n");
        buffer.append("<input class=\"searchform\" type=\"submit\" name=\"ok\" value=\"Upload Job\"/><br>\n");
        buffer.append("</form>\n");
        buffer.append("</div>\n");
        buffer.append("</div>\n");
        return buffer.toString();
    }

}
