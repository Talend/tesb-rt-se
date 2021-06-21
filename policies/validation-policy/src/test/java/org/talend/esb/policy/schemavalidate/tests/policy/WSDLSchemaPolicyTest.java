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
package org.talend.esb.policy.schemavalidate.tests.policy;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Test;
import org.talend.esb.policy.schemavalidate.SchemaValidationPolicy;
import org.talend.esb.policy.schemavalidate.SchemaValidationPolicy.ValidationType;


public class WSDLSchemaPolicyTest extends AbstractPolicyTest {

    @Test
    public void testWSDLConsumerRequestValid() throws Exception {
    	basicPolicyTest(new String[]{"classpath:spring/client/policy/client-wsdlSchemaContext.xml"},
    			VALID_CUSTOMER_NAME);
    }

    @Test(expected=SOAPFaultException.class)
    public void testWSDLConsumerRequestNotValid() throws Exception {
    	basicPolicyTest(new String[]{"classpath:spring/client/policy/client-wsdlSchemaContext.xml"},
    			NOT_VALID_CUSTOMER_NAME);
    }

    @Test
    public void testWSLDConsumerResponseValid() throws Exception {
    	SchemaValidationPolicy policy = new SchemaValidationPolicy();
    	policy.setAppliesToType(SchemaValidationPolicy.AppliesToType.valueOf("consumer"));
    	policy.setMessageType(SchemaValidationPolicy.MessageType.valueOf("response"));
    	policy.setValidationType(ValidationType.valueOf("WSDLSchema"));
    	customPolicyTest(policy, VALID_CUSTOMER_NAME);
    }

    @Test(expected=SOAPFaultException.class)
    public void testWSLDConsumerResponseNotValid() throws Exception {
    	SchemaValidationPolicy policy = new SchemaValidationPolicy();
    	policy.setAppliesToType(SchemaValidationPolicy.AppliesToType.valueOf("consumer"));
    	policy.setMessageType(SchemaValidationPolicy.MessageType.valueOf("response"));
    	policy.setValidationType(ValidationType.valueOf("WSDLSchema"));
    	customPolicyTest(policy, NOT_VALID_CUSTOMER_NAME);
    }
}
