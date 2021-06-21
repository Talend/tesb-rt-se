/*
 * #%L
 * TESB :: Examples :: Locator Rest Client
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

package demo.client;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import demo.common.*;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.esb.servicelocator.cxf.LocatorFeature;

public class Client {

        public void runCodeProxyClient() throws Exception {
            System.out.println("*** Running Proxy client initialized from the code ***");

            JAXRSClientFactoryBean factoryBean = prepareFactoryBean();
            factoryBean.setServiceClass(OrderService.class);
            OrderService client = factoryBean.create(OrderService.class);
            useOrderService(client);
        }

        public void runCodeWebClient() throws Exception {
            System.out.println("*** Running WebClient initialized from the code ***");

            JAXRSClientFactoryBean factoryBean = prepareFactoryBean();
            WebClient wc = factoryBean.createWebClient();
            wc.accept("application/xml");

            String orderId = "1";
            for (int i = 1; i <= 3; i++) {
                wc.path("orderservice").path(orderId);
                Order ord = wc.get(Order.class);
                describeOrder(i, ord);
		wc.back(true);

        	Thread.sleep(2000);
            }
        }

        private JAXRSClientFactoryBean prepareFactoryBean() throws Exception {
            JAXRSClientFactoryBean factoryBean = new JAXRSClientFactoryBean();
            factoryBean.setAddress("locator://some_usefull_information");
            factoryBean.setServiceName(new QName("http://common.demo/", "OrderService"));

            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "META-INF/tesb/locator/beans.xml" });
            LocatorFeature feature = (LocatorFeature) context.getBean("locatorFeature");

            factoryBean.setFeatures(Collections.singletonList(feature));
            return factoryBean;
        }

        public void runSpringClient() throws Exception {
            System.out.println("*** Running Spring initialized client ***");


            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "META-INF/client.xml" });
            OrderService client = (OrderService) context.getBean("restClient");
            useOrderService(client);
        }

        private void useOrderService(OrderService client) throws Exception {
            String orderId = "1";
            for (int i = 1; i <= 3; i++) {
		Order ord = client.getOrder(orderId);

		describeOrder(i, ord);

		Thread.sleep(2000);
	    }
        }

        private void describeOrder(int invocationNumber, Order ord) {
            System.out.println("invocation number:" + invocationNumber);
	    System.out.println("Order description is::" + ord.getDescription());
	    if (LOG.isLoggable(Level.INFO)) {
		LOG.log(Level.INFO, ord.getDescription());
	    }
        }

	private static final Logger LOG = Logger.getLogger(Client.class
			.getPackage().getName());

	public static void main(String[] args) throws Exception {
		Client client = new Client();
                client.runSpringClient();
                client.runCodeProxyClient();
		client.runCodeWebClient();

		System.exit(0);

	}

}
