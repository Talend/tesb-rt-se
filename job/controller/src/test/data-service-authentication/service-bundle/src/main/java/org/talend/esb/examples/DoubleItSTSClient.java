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

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;

public class DoubleItSTSClient {

    static final String NAMESPACE = "http://examples.esb.talend.org/";

    static final QName SERVICE_QNAME = new QName(NAMESPACE, "DoubleItService");

    public static void main(String[] args) throws Exception {

        SpringBusFactory bf = new SpringBusFactory();

        Bus bus = bf.createBus("META-INF/spring/client-sts-beans.xml");
        SpringBusFactory.setDefaultBus(bus);
        SpringBusFactory.setThreadDefaultBus(bus);

        URL wsdl = DoubleItSTSClient.class.getResource("DoubleIt.wsdl");
        Service service = Service.create(wsdl, SERVICE_QNAME);

        QName portQName = new QName(NAMESPACE, "DoubleItAsymmetricSAML2Port");

        DoubleItPortType symmetricSaml2Port = service.getPort(portQName, DoubleItPortType.class);

        int x =  symmetricSaml2Port.execute(10);

        System.out.println(x);
    }
}
