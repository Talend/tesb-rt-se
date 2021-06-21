/*
 * #%L
 * Service Activity Monitoring :: Agent
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
package org.talend.esb.policy.schemavalidate.tests.feature;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Test;
import org.talend.esb.policy.schemavalidate.feature.SchemaValidationFeature;


public class CustomSchemaFeatureTest extends AbstractFeatureTest{

	private static String[] CLIENT_APPLICATION_CONTEXT_PATH =
			new String[] {"classpath:spring/client/feature/client-customSchemaContext.xml"};

    @Test
    public void testLocalCustomSchemaConsumerRequestValid() throws Exception {
    	basicFeatureTest(CLIENT_APPLICATION_CONTEXT_PATH, VALID_CUSTOMER_NAME);
    }

    @Test(expected=SOAPFaultException.class)
    public void testLocalCustomSchemaConsumerRequestNotValid() throws Exception {
    	basicFeatureTest(CLIENT_APPLICATION_CONTEXT_PATH, NOT_VALID_CUSTOMER_NAME);
    }

    @Test
    public void testLocalCustomSchemaConsumerResponseValid() throws Exception {
    	SchemaValidationFeature f = new SchemaValidationFeature();
    	f.setAppliesTo("consumer");
    	f.setMessage("response");
    	f.setType("CustomSchema");
    	f.setPath(VALID_SCHEMA_RELATIVE_PATH);
    	customFeatureTest(f, VALID_CUSTOMER_NAME);
    }

    @Test(expected=SOAPFaultException.class)
    public void testLocalCustomSchemaConsumerResponseNotValid() throws Exception {
    	SchemaValidationFeature f = new SchemaValidationFeature();
    	f.setAppliesTo("consumer");
    	f.setMessage("response");
    	f.setType("CustomSchema");
    	f.setPath(VALID_SCHEMA_RELATIVE_PATH);
    	customFeatureTest(f, NOT_VALID_CUSTOMER_NAME);
    }

    @Test(expected=SOAPFaultException.class)
    public void testLocalCustomSchemaNotExists() throws Exception {
	  	SchemaValidationFeature f = new SchemaValidationFeature();
	  	f.setAppliesTo("consumer");
	  	f.setMessage("request");
	  	f.setType("CustomSchema");
	  	f.setPath(NOT_EXISTING_SCHEMA_RELATIVE_PATH);
	  	customFeatureTest(f, VALID_CUSTOMER_NAME);
  }

    @Test
    public void testLocalCustomSchemaAbsolutePathConsumerResponseValid() throws Exception {
    	SchemaValidationFeature f = new SchemaValidationFeature();
    	f.setAppliesTo("consumer");
    	f.setMessage("response");
    	f.setType("CustomSchema");
    	f.setPath(VALID_SCHEMA_ABSOLUTE_PATH);
    	customFeatureTest(f, VALID_CUSTOMER_NAME);
    }

    @Test(expected=SOAPFaultException.class)
    public void testLocalCustomSchemaAbsolutePathNotExist() throws Exception {
    	SchemaValidationFeature f = new SchemaValidationFeature();
    	f.setAppliesTo("consumer");
    	f.setMessage("response");
    	f.setType("CustomSchema");
    	f.setPath(NOT_EXISTING_SCHEMA_ABSOLUTE_PATH);
    	customFeatureTest(f, VALID_CUSTOMER_NAME);
    }

    @Test(expected=SOAPFaultException.class)
    public void testLocalNotValidCustomSchema() throws Exception {
    	SchemaValidationFeature f = new SchemaValidationFeature();
    	f.setAppliesTo("consumer");
    	f.setMessage("response");
    	f.setType("CustomSchema");
    	f.setPath(NOT_VALID_SCHEMA_RELATIVE_PATH);
    	customFeatureTest(f, VALID_CUSTOMER_NAME);
    }
}
