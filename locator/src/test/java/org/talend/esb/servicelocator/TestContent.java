/*
 * #%L
 * Service Locator Client for CXF
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
package org.talend.esb.servicelocator;


import static org.talend.esb.servicelocator.TestValues.ENDPOINT_1;
import static org.talend.esb.servicelocator.TestValues.LAST_TIME_STARTED;
import static org.talend.esb.servicelocator.TestValues.LAST_TIME_STOPPED;

import java.io.ByteArrayOutputStream;
import java.util.Collection;

import org.talend.esb.DomMother;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.internal.endpoint.BindingType;
import org.talend.esb.servicelocator.client.internal.endpoint.TransportType;
import org.w3c.dom.Element;

public class TestContent {

    public static final String WSA = "http://www.w3.org/2005/08/addressing";

    public static final String SL = "http://talend.org/schemas/esb/locator/content/20011/11";

    public static final byte[] CONTENT_ANY_1 = new byte[] {51, 6, 23, 45, 127, 34};

    public static final byte[] CONTENT_ANY_2 = new byte[] {15, 26, 32, 76, 111};

    public static final byte[] CONTENT_ENDPOINT_1 =
        createContent(ENDPOINT_1, LAST_TIME_STARTED, -1, BindingType.SOAP_11, TransportType.HTTP, null);

    public static byte[] createContent(String addressVal) {
        return createContent(
                addressVal, -1, -1, BindingType.SOAP_11, TransportType.HTTP, null);
    }

    public static byte[] createContent(String addressVal, Long lastStartTime, Long lastStopTime) {
        return createContent(
                addressVal, lastStartTime, lastStopTime, BindingType.SOAP_11, TransportType.HTTP, null);
    }

    public static byte[] createContent(SLProperties props) {
        return createContent(
                "", LAST_TIME_STARTED, LAST_TIME_STOPPED, BindingType.SOAP_11, TransportType.HTTP, props);
    }

    public static byte[] createContent(
            String addressVal,
            long lastStartTime,
            long lastStopTime,
            SLProperties props) {
        return createContent(
                addressVal, lastStartTime, lastStopTime, BindingType.SOAP_11, TransportType.HTTP, props);
    }

    public static byte[] createContent(String addressVal, long lastStartTime, long lastStopTime,
            BindingType binding, TransportType transport,  SLProperties props) {
        Element root = DomMother.newDocument(SL, "EndpointData");
        addEPR(root, addressVal, props);

        DomMother.addLeafElement(root, SL, "LastTimeStarted", Long.toString(lastStartTime));
        DomMother.addLeafElement(root, SL, "LastTimeStopped", Long.toString(lastStopTime));

        DomMother.addLeafElement(root, SL, "Binding", binding.value());
        DomMother.addLeafElement(root, SL, "Transport", transport.value());

        return serialize(root);
    }

    public static byte[] createContentInvalidEPR() {
        Element root = DomMother.newDocument(SL, "EndpointData");
        DomMother.addElement(root, "invalidNamespace", "EndpointReference");

        return serialize(root);
    }

    public static void addProperties(Element parent, SLProperties properties) {
        Element slProps  = DomMother.addElement(parent, SL, "ServiceLocatorProperties");
        for (String name : properties.getPropertyNames()) {
            Element entry  = DomMother.addElement(slProps, SL, "Entry");
            DomMother.addAttribute(entry, "key",  name);

            Collection<String> values = properties.getValues(name);
            for (String  value : values) {
                DomMother.addLeafElement(entry, SL, "Value", value);
            }
        }
    }

    private static void addEPR(Element parent, String addressVal, SLProperties props) {
        Element epr = DomMother.addElement(parent, WSA, "EndpointReference");
        DomMother.addLeafElement(epr, WSA, "Address", addressVal);
        Element metadata  = DomMother.addElement(epr, WSA, "Metadata");

        if (props != null) {
            addProperties(metadata, props);
        }
    }

    private static byte[] serialize(Element root) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(10000);
        DomMother.serialize(root, outStream);
        return outStream.toByteArray();
    }
}
