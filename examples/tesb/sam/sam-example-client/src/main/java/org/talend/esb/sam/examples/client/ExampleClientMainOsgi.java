/*
 * #%L
 * Service Activity Monitoring :: Example Client
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
package org.talend.esb.sam.examples.client;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Use this to test against customerService2 and sam-server each deployed on tomcat at 8080
 */
public class ExampleClientMainOsgi {
    public static void main(String args[]) throws Exception {
    	System.setProperty("serviceUrl", "http://localhost:9090/services/CustomerServicePort");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/client.xml");
        CustomerServiceTester tester = context.getBean(CustomerServiceTester.class);
        tester.testCustomerService();
        Thread.sleep(2000);
        context.close();
        System.exit(0);
    }
}
