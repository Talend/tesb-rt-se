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
import static org.talend.esb.servicelocator.TestValues.NAME_1;
import static org.talend.esb.servicelocator.TestValues.VALUE_1;

import java.io.ByteArrayOutputStream;


import org.talend.esb.DomMother;
import org.w3c.dom.Element;

public class TestContent {

    public static final String WSA = "http://www.w3.org/2005/08/addressing";
    
    public static final String SL = "http://talend.org/esb/serviceLocator/4.2";
    
    public static final long LAST_TIME_STARTED = 9874534433L;

    public static final byte[] CONTENT_ENDPOINT_1 = createContent(ENDPOINT_1, LAST_TIME_STARTED, null);

    public static byte[] createContent(String addressVal) {
        return createContent(addressVal, null, null);
    }
    
    public static byte[] createContent(String addressVal, Long lastStartTime, Long lastStopTime) {
        Element root = DomMother.newDocument(SL, "EndpointData");
        Element epr = DomMother.addElement(root, WSA, "EndpointReference");
        DomMother.addLeafElement(epr, WSA, "Address", addressVal);
        Element metadata  = DomMother.addElement(epr, WSA, "Metadata");
        Element slProps  = DomMother.addElement(metadata, SL, "ServiceLocatorProperties");
        Element entry  = DomMother.addElement(slProps, SL, "Entry");
        DomMother.addAttribute(entry, "key",  NAME_1);
        DomMother.addLeafElement(entry, SL, "Value", VALUE_1);

        if (lastStartTime != null) {
            DomMother.addLeafElement(root, SL, "LastTimeStarted", lastStartTime.toString());
        }
        
        if (lastStopTime != null) {
            DomMother.addLeafElement(root, SL, "LastTimeStopped", lastStopTime.toString());
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream(10000);
        DomMother.serialize(root, outStream);
        return outStream.toByteArray();
    }

}
