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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.talend.esb.servicelocator.client.SLPropertiesImpl;
import org.talend.esb.servicelocator.client.TransportType;

public class TestValues {

    public static final String NAME_1 = "name1";

    public static final String NAME_2 = "name2";

    public static final String NAME_3 = "name3";

    public static final String NAME_4 = "name4";

    public static final String VALUE_1 = "value1";

    public static final String VALUE_2 = "value2";

    public static final String VALUE_3 = "value3";

    public static final String VALUE_4 = "value4";

    public static final String NAME_1_NOT_TRIMMED = "\tname1";

    public static final String VALUE_1_NOT_TRIMMED = " " + VALUE_1;

    public static final String VALUE_2_NOT_TRIMMED = " " + VALUE_2 + "\t";

    public static final String VALUE_3_NOT_TRIMMED = VALUE_3 + "\t";

    public static final QName SERVICE_QNAME_1 = new QName("http://example.com/services", "service1");

    public static final QName SERVICE_QNAME_2 = new QName("http://example.com/services", "service2");

    public static final QName SERVICE_QNAME_3 = new QName("http://example.com/services", "service3");

    public static final QName SERVICE_QNAME_4 = new QName("http://example.com/services", "service4");

    public static final QName SERVICE_QNAME_5 = new QName("http://example.com/services", "service5");

    public static final String REL_ENDPOINT_1 = "endpoint1";

    public static final String PREFIX_1 = "http://ep.com/";

    public static final Map<String, String> PREFIXES_1 = new HashMap<String, String>();

    public static final String PREFIX_HTTP = "http://ep.com/";

    public static final String PREFIX_HTTPS = "https://ep.com:9001/";

    public static final String ENDPOINT_1 = "http://ep.com/endpoint1";

    public static final String ENDPOINT_2 = "http://ep.com/endpoint2";

    public static final String ENDPOINT_3 = "http://ep.com/endpoint3";

    public static final String ENDPOINT_4 = "http://ep.com/endpoint4";

    public static final String ENDPOINT_5 = "http://ep.com/endpoint5";

    public static final byte[] EMPTY_CONTENT = new byte[0];

    public static final long LAST_TIME_STARTED = 1302458583L;

    public static final long LAST_TIME_STOPPED = 1302468588L;

    public static final SLPropertiesImpl PROPERTIES = new SLPropertiesImpl();

    public static final SLPropertiesImpl PROPERTIES_1 = PROPERTIES;

    public static final SLPropertiesImpl PROPERTIES_2 = new SLPropertiesImpl();

    public static final SLPropertiesImpl PROPERTIES_3 = new SLPropertiesImpl();

    public static final SLPropertiesImpl PROPERTIES_EMPTY = new SLPropertiesImpl();

    public static final String USER_NAME = "user1";

    public static final String PASSWORD = "pwd";

    public static final byte[] USER_NAME_PASSWORD_BYTES;

    static {
        PROPERTIES.addProperty(NAME_1, VALUE_1, VALUE_2);
        PROPERTIES.addProperty(NAME_2, VALUE_3);

        PROPERTIES_2.addProperty(NAME_2, VALUE_2, VALUE_3);
        PROPERTIES_2.addProperty(NAME_3, VALUE_1);
        PROPERTIES_2.addProperty(NAME_4, VALUE_4);

        PROPERTIES_3.addProperty(NAME_4, VALUE_3, VALUE_2, VALUE_1);
        PROPERTIES_3.addProperty(NAME_2, VALUE_4);

        PREFIXES_1.put(TransportType.HTTP.toString(), PREFIX_HTTP);
        PREFIXES_1.put(TransportType.HTTPS.toString(), PREFIX_HTTPS);

        try {
            USER_NAME_PASSWORD_BYTES = (USER_NAME + ":" + PASSWORD).getBytes("UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
