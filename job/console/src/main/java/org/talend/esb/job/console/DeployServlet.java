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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Servlet to deploy job or route.
 */
public class DeployServlet extends HttpServlet {

    private BundleContext bundleContext;
    private File tmpDir;
    private File deployDir;

    public void init(ServletConfig servletConfig) throws ServletException {
        ServletContext context = servletConfig.getServletContext();
        bundleContext = (BundleContext) context.getAttribute("osgi-bundlecontext");
        tmpDir = new File(System.getProperty("java.io.tmpdir"));
        deployDir = new File(System.getProperty("karaf.base") + "/deploy");
        if (!deployDir.exists()) {
            deployDir.mkdirs();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doIt(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doIt(request, response);
    }

    public void doIt(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();

        // set the size threshold, above which content will be stored on disk
        fileItemFactory.setSizeThreshold(1 * 1024 * 1024); // 1MB

        // set the temporary directory to store the uploaded files of size above threshold
        fileItemFactory.setRepository(tmpDir);

        ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
        try {
            // parse the request
            List items = uploadHandler.parseRequest(request);
            Iterator iterator = items.iterator();
            while (iterator.hasNext()) {
                FileItem item = (FileItem) iterator.next();
                if (!item.isFormField()) {
                    File file = new File(deployDir, item.getName());
                    item.write(file);
                    Bundle bundle = bundleContext.installBundle("file://" + file.getAbsolutePath());
                    bundle.start();
                }
            }
        } catch (Exception e) {
            response.sendRedirect("home.do?error=" + e.getMessage());
            return;
        }
        response.sendRedirect("home.do");
    }

}
