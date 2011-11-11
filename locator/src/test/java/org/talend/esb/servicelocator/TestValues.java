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

import javax.xml.namespace.QName;

import org.talend.esb.servicelocator.client.SLPropertiesImpl;
import org.talend.esb.servicelocator.client.internal.ServiceLocatorImpl;

public class TestValues {

    public static final String NAME_1 = "name1";

    public static final String NAME_2 = "name2";

    public static final String NAME_3 = "name3";

    public static final String NAME_4 = "name4";

    public static final String VALUE_1 = "value1";

    public static final String VALUE_2 = "value2";

    public static final String VALUE_3 = "value3";

    public static final String VALUE_4 = "value4";

    public static final QName SERVICE_QNAME_1 = new QName("http://example.com/services", "service1");

    public static final QName SERVICE_QNAME_2 = new QName("http://example.com/services", "service2");

    public static final QName SERVICE_QNAME_3 = new QName("http://example.com/services", "service3");

    public static final QName SERVICE_QNAME_4 = new QName("http://example.com/services", "service4");

    public static final String SERVICE_NAME_1 = "{http:%2F%2Fexample.com%2Fservices}service1";

    public static final String SERVICE_NAME_2 = "{http:%2F%2Fexample.com%2Fservices}service2";

    public static final String ENDPOINT_1 = "http://ep.com/endpoint1";

    public static final String REL_ENDPOINT_1 = "endpoint1";

    public static final String PREFIX_1 = "http://ep.com/";

    public static final String ENDPOINT_2 = "http://ep.com/endpoint2";

    public static final String ENDPOINT_3 = "http://ep.com/endpoint3";

    public static final String ENDPOINT_4 = "http://ep.com/endpoint4";

    public static final String ENDPOINT_NODE_1 = "http:%2F%2Fep.com%2Fendpoint1";

    public static final String ENDPOINT_NODE_2 = "http:%2F%2Fep.com%2Fendpoint2";

    public static final String STATUS_NODE = "live";

    public static final String SERVICE_PATH_1 = ServiceLocatorImpl.LOCATOR_ROOT_PATH + "/" + SERVICE_NAME_1;

    public static final String SERVICE_PATH_2 = ServiceLocatorImpl.LOCATOR_ROOT_PATH + "/" + SERVICE_NAME_2;

    public static final String ENDPOINT_PATH_11 = SERVICE_PATH_1 + "/" + ENDPOINT_NODE_1;

    public static final String ENDPOINT_PATH_12 = SERVICE_PATH_1 + "/" + ENDPOINT_NODE_2;

    public static final String ENDPOINT_PATH_22 = SERVICE_PATH_2 + "/" + ENDPOINT_NODE_2;

    public static final String ENDPOINT_STATUS_PATH_11 = ENDPOINT_PATH_11 + "/" + STATUS_NODE;

    public static final String ENDPOINT_STATUS_PATH_12 = ENDPOINT_PATH_12 + "/" + STATUS_NODE;

    public static final byte[] EMPTY_CONTENT = new byte[0];
    
    public static final long LAST_TIME_STARTED = 1302458583L;

    public static final long LAST_TIME_STOPPED = 1302468588L;

    public static final SLPropertiesImpl PROPERTIES = new SLPropertiesImpl();

    public static final SLPropertiesImpl PROPERTIES_1 = PROPERTIES;

    public static final SLPropertiesImpl PROPERTIES_2 = new SLPropertiesImpl();

    public static final SLPropertiesImpl PROPERTIES_3 = new SLPropertiesImpl();

    public static final SLPropertiesImpl PROPERTIES_EMPTY = new SLPropertiesImpl();

    static {
        PROPERTIES.addProperty(NAME_1, VALUE_1, VALUE_2);
        PROPERTIES.addProperty(NAME_2, VALUE_3);

        PROPERTIES_2.addProperty(NAME_2, VALUE_2, VALUE_3);
        PROPERTIES_2.addProperty(NAME_3, VALUE_1);
        PROPERTIES_2.addProperty(NAME_4, VALUE_4);

        PROPERTIES_3.addProperty(NAME_4, VALUE_3, VALUE_2, VALUE_1);
        PROPERTIES_3.addProperty(NAME_2, VALUE_4);
    }
}
