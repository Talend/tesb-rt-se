/*
 * #%L
 * Talend ESB Runtime Examples :: Locator Soap Service Example :: Webapp
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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
package demo.service;

import javax.servlet.*;
import javax.xml.namespace.QName;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.schemas.esb.locator._2011._11.BindingType;
import org.talend.schemas.esb.locator._2011._11.TransportType;
import org.talend.services.esb.locator.v1.InterruptedExceptionFault;
import org.talend.services.esb.locator.v1.LocatorService;
import org.talend.services.esb.locator.v1.ServiceLocatorFault;

public class ContextListener implements ServletContextListener {

    private ServletContext context = null;

    /*
     * This method is invoked when the Web Application has been removed and is
     * no longer able to accept requests
     */

    public void contextDestroyed(ServletContextEvent event) {
        // Output a simple message to the server's console
        System.out.println("The Simple Web App. Has Been Removed");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "/client.xml");
        LocatorService client = (LocatorService) context
                .getBean("locatorService");

        String serviceHost = this.context.getInitParameter("serviceHost");

        try {
            client.unregisterEndpoint(new QName("http://talend.org/esb/examples/", "GreeterService"), serviceHost);
        } catch (InterruptedExceptionFault e) {
            e.printStackTrace();
        } catch (ServiceLocatorFault e) {
            e.printStackTrace();
        }
        this.context = null;
    }

    // This method is invoked when the Web Application
    // is ready to service requests

    public void contextInitialized(ServletContextEvent event) {
        this.context = event.getServletContext();

        // Output a simple message to the server's console
        System.out.println("The Simple Web App. Is Ready");

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "/client.xml");
        LocatorService client = (LocatorService) context
                .getBean("locatorService");

        String serviceHost = this.context.getInitParameter("serviceHost");

        try {
            client.registerEndpoint(new QName(
                    "http://talend.org/esb/examples/", "GreeterService"),
                    serviceHost, BindingType.SOAP_11, TransportType.HTTP, null);
        } catch (InterruptedExceptionFault e) {
            e.printStackTrace();
        } catch (ServiceLocatorFault e) {
            e.printStackTrace();
        }
    }
}
