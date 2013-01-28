/*
 * #%L
 * TESB :: Examples :: Locator Rest Client
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
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
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Client {

	private static final Logger LOG = Logger.getLogger(Client.class
			.getPackage().getName());


        public void runCodeClient() throws Exception {
            System.out.println("*** Running the client initialized from the code ***");

            JAXRSClientFactoryBean factoryBean = new JAXRSClientFactoryBean();
            factoryBean.setAddress("locator://some_usefull_information");
            factoryBean.setServiceClass(OrderService.class);
            factoryBean.setServiceName(new QName("http://service.demo/", "OrderServiceImpl"));
            
            org.talend.esb.servicelocator.cxf.LocatorFeature feature =
                new org.talend.esb.servicelocator.cxf.LocatorFeature();
            feature.setSelectionStrategy("evenDistributionSelectionStrategy");  
            factoryBean.setFeatures(Collections.singletonList(feature)); 


            OrderService client = factoryBean.create(OrderService.class);
            useOrderService(client); 
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
            for (int i = 0; i < 5; i++) {
		Order ord = client.getOrder(orderId);

		System.out.println("invoaction number:"+i);
		System.out.println("Order description is::"+ord.getDescription());
		if (LOG.isLoggable(Level.INFO)) {
			LOG.log(Level.INFO, ord.getDescription());
		}
		
		Thread.sleep(2000);
	    }
        } 

	public static void main(String[] args) throws Exception {
		Client client = new Client();
                client.runSpringClient();
                client.runCodeClient();
		
		
		System.exit(0);

	}

}
