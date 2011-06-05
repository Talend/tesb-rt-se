/*
 * #%L
 * Service Locator Client for CXF
 * %%
 * Copyright (C) 2011 Talend Inc.
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

import java.io.ByteArrayOutputStream;
import java.util.Collection;


import org.talend.esb.DomMother;
import org.talend.esb.servicelocator.client.SLProperties;
import org.w3c.dom.Element;

public class TestContent {

    public static final String WSA = "http://www.w3.org/2005/08/addressing";
    
    public static final String SL = "http://talend.org/esb/serviceLocator/4.2";

    public static final long LAST_TIME_STARTED = 129874534433L;

    public static final long LAST_TIME_STOPPED = 129885675343L;

    public static final byte[] CONTENT_ENDPOINT_1 = createContent(ENDPOINT_1, LAST_TIME_STARTED, -1, null);

    public static byte[] createContent(String addressVal) {
        return createContent(addressVal, -1, -1, null);
    }

    public static byte[] createContent(String addressVal, Long lastStartTime, Long lastStopTime) {
        return createContent(addressVal, lastStartTime, lastStopTime, null);
    }
    
    public static byte[] createContent(SLProperties props) {
        return createContent("", LAST_TIME_STARTED, LAST_TIME_STOPPED, props);
    }

    public static byte[] createContent(String addressVal, long lastStartTime, long lastStopTime, SLProperties props) {
        Element root = DomMother.newDocument(SL, "EndpointData");
        Element epr = DomMother.addElement(root, WSA, "EndpointReference");
        DomMother.addLeafElement(epr, WSA, "Address", addressVal);
        Element metadata  = DomMother.addElement(epr, WSA, "Metadata");
        
        if (props != null) {
            addProperties(metadata, props);
        }

        DomMother.addLeafElement(root, SL, "LastTimeStarted", Long.toString(lastStartTime));
        DomMother.addLeafElement(root, SL, "LastTimeStopped", Long.toString(lastStopTime));

        ByteArrayOutputStream outStream = new ByteArrayOutputStream(10000);
        DomMother.serialize(root, outStream);
        return outStream.toByteArray();
    }
    
    public static void addProperties(Element parent, SLProperties properties) {
        Element slProps  = DomMother.addElement(parent, SL, "ServiceLocatorProperties");
        for(String name : properties.getPropertyNames()) {
            Element entry  = DomMother.addElement(slProps, SL, "Entry");
            DomMother.addAttribute(entry, "key",  name);

            Collection<String> values = properties.getValues(name);
            for(String  value : values) {
                DomMother.addLeafElement(entry, SL, "Value", value);                
            }
        }
    }

}
