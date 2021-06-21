/*
 * #%L
 * CXF :: Example :: SimpleService :: TESB container
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

import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class SimpleClient {

    public SimpleClient() {
    }

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = null;

        if (args[0].equals("war")) {
            context = new ClassPathXmlApplicationContext(
                    new String[] { "META-INF/spring/client-beans-war.xml" });
        } else {
            context = new ClassPathXmlApplicationContext(
                    new String[] { "META-INF/spring/client-beans-osgi.xml" });
        }

        SimpleService client = (SimpleService) context.getBean("simpleClient");

        for (int i = 0; i < 100; i++) {
            String response = client.sayHi("Alex");
            System.out.println(response);
        }

        for (int i = 1; i < 6; i++) {
            int result = client.doubleIt(i);
            System.out.println(result);
        }

        // "Incorrect name" exception would be thrown
        try {
            String response = client.sayHi("Joe");
        }catch(RuntimeException e) {
            e.printStackTrace();
        }

    }

}
