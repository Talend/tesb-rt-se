/*
 * #%L
 * Talend ESB Runtime Examples :: Locator Soap Service Example :: Client
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

import javax.xml.namespace.QName;

import javax.xml.ws.Service;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.schemas.esb.locator._2011._11.LookupEndpointResponse;
import org.talend.schemas.esb.locator._2011._11.LookupRequestType;
import org.talend.services.esb.locator.v1.LocatorService;

import demo.common.Greeter;

public class Client {

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/META-INF/client.xml");
        LocatorService client = (LocatorService) context.getBean("locatorService");

        LookupRequestType request = new LookupRequestType();
        request.setServiceName(new QName("http://talend.org/esb/examples/",
                "GreeterService"));

        LookupEndpointResponse response = client.lookupEndpoint(request);
        W3CEndpointReference endpointReference = response
                .getEndpointReference();
        System.out.println(endpointReference.toString());

        javax.xml.ws.Service jaxwsServiceObject = Service.create(new QName(
                "http://talend.org/esb/examples/", "GreeterService"));

        Greeter greeterProxy = jaxwsServiceObject.getPort(endpointReference,
                Greeter.class);
        String reply = greeterProxy.greetMe("HI");
        System.out.println("Server said: " + reply);

    }
}
