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
package org.talend.esb.policy.schemavalidate.tests.policy;

import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.ws.policy.PolicyConstants;
import org.apache.neethi.Policy;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.esb.policy.schemavalidate.SchemaValidationPolicy;
import org.talend.esb.policy.schemavalidate.tests.common.AbstractTest;
import org.talend.esb.policy.schemavalidate.testservice.client.CustomerServiceTester;

import com.example.customerservice.CustomerService;


public abstract class AbstractPolicyTest extends AbstractTest{

	public void basicPolicyTest(final String[] springContextPath, String customerName) throws Exception{
		clientCtxt = new ClassPathXmlApplicationContext(springContextPath);

    	CustomerServiceTester tester = (CustomerServiceTester)clientCtxt.getBean("tester");

    	tester.testCustomerService(customerName);
        clientCtxt.close();
	}

	public void customPolicyTest(SchemaValidationPolicy assertion, String customerName) throws Exception{
    	clientCtxt = new ClassPathXmlApplicationContext("classpath:spring/client/policy/client-defaultApplicationContext.xml");
    	ClientProxyFactoryBean clientFactory = (ClientProxyFactoryBean)clientCtxt.getBean(ClientProxyFactoryBean.class);

    	Policy policy = new Policy();
    	policy.addAssertion(assertion);

    	clientFactory.getProperties().put(PolicyConstants.POLICY_OVERRIDE, policy);

    	CustomerServiceTester tester = new CustomerServiceTester();

    	tester.setCustomerService(clientFactory.create(CustomerService.class));
    	tester.testCustomerService(customerName);
        clientCtxt.close();
	}
}
