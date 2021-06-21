/*
 * #%L
 * Talend :: ESB :: Job :: Controller
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
package org.talend.esb.examples;

import javax.xml.ws.BindingProvider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DoubleItClient {

    public static final String NAME = "karaf_name";

    public static final String PASS = "karaf_password";

    public static void main(String[] args) throws Exception {


        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "META-INF/spring/client-beans.xml");
        DoubleIt client = (DoubleIt) context.getBean("DoubleItClient");

        ((BindingProvider) client).getRequestContext().put(
                BindingProvider.USERNAME_PROPERTY, NAME);
        ((BindingProvider) client).getRequestContext().put(
                BindingProvider.PASSWORD_PROPERTY, PASS);

        int result = client.execute(10);

        System.out.println("Result is " + result);
    }

}
