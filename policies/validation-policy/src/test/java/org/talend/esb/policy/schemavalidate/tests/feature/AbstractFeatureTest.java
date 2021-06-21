/*
 * #%L
 * Service Activity Monitoring :: Agent
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
package org.talend.esb.policy.schemavalidate.tests.feature;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.esb.policy.schemavalidate.feature.SchemaValidationFeature;
import org.talend.esb.policy.schemavalidate.tests.common.AbstractTest;
import org.talend.esb.policy.schemavalidate.testservice.client.CustomerServiceTester;

import com.example.customerservice.CustomerService;


public abstract class AbstractFeatureTest extends AbstractTest{

	public void basicFeatureTest(final String[] springContextPath, String customerName) throws Exception{
    	clientCtxt = new ClassPathXmlApplicationContext(springContextPath);

    	CustomerServiceTester client = (CustomerServiceTester)clientCtxt.getBean("tester");
    	client.testCustomerService(customerName);
        clientCtxt.close();
	}

	public void customFeatureTest(SchemaValidationFeature feature, String customerName) throws Exception{
    	clientCtxt = new ClassPathXmlApplicationContext("classpath:spring/client/feature/client-defaultApplicationContext.xml");
    	ClientProxyFactoryBean clientFactory = (ClientProxyFactoryBean)clientCtxt.getBean(ClientProxyFactoryBean.class);

    	List<SchemaValidationFeature> features = new ArrayList<SchemaValidationFeature>();
    	features.add(feature);
    	clientFactory.setFeatures(features);

      	CustomerServiceTester tester = new CustomerServiceTester();
    	tester.setCustomerService(clientFactory.create(CustomerService.class));
    	tester.testCustomerService(customerName);
        clientCtxt.close();
	}

}
