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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

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

	public static final QName SERVICE_QNAME_1 = new QName(
			"http://example.com/services", "service1");

	public static final QName SERVICE_QNAME_2 =
		new QName("http://example.com/services", "service2");

	public static final String SERVICE_NAME_1 = "{http:%2F%2Fexample.com%2Fservices}service1";
	
	public static final String SERVICE_NAME_2 = "{http:%2F%2Fexample.com%2Fservices}service2";

	public static final String ENDPOINT_1 = "http://ep.com/service1";

	public static final String ENDPOINT_2 = "http://ep.com/service2";

	public static final String ENDPOINT_NODE_1 = "http:%2F%2Fep.com%2Fservice1";

	public static final String ENDPOINT_NODE_2 = "http:%2F%2Fep.com%2Fservice2";

	public static final String STATUS_NODE = "live";

	public static final String SERVICE_PATH_1 =
		ServiceLocatorImpl.LOCATOR_ROOT_PATH + "/" + SERVICE_NAME_1;
	
	public static final String SERVICE_PATH_2 =
		ServiceLocatorImpl.LOCATOR_ROOT_PATH + "/" + SERVICE_NAME_2;

	public static final String ENDPOINT_PATH_11 = SERVICE_PATH_1 + "/"
		+ ENDPOINT_NODE_1;

	public static final String ENDPOINT_PATH_12 = SERVICE_PATH_1 + "/"
		+ ENDPOINT_NODE_2;

	public static final String ENDPOINT_PATH_22 = SERVICE_PATH_2 + "/"
		+ ENDPOINT_NODE_2;

	public static final String ENDPOINT_STATUS_PATH_11 = ENDPOINT_PATH_11 + "/" + STATUS_NODE;

	public static final String ENDPOINT_STATUS_PATH_12 = ENDPOINT_PATH_12 + "/" + STATUS_NODE;
	
	public static final byte[] EMPTY_CONTENT = new byte[0];
	         
	public static final SLPropertiesImpl PROPERTIES = new SLPropertiesImpl();
	
	public static final byte[] SERIALIZED_PROPERTIES;
	
	
	

	static {
		PROPERTIES.addProperty(NAME_1, VALUE_1, VALUE_2);
		PROPERTIES.addProperty(NAME_2, VALUE_3);
		
		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
			
			objectStream.writeObject(PROPERTIES);
			objectStream.close();
			SERIALIZED_PROPERTIES = byteStream.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
