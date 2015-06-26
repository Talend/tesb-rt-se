/*
 * #%L
 * Talend ESB :: Camel Talend Job Component
 * %%
 * Copyright (C) 2011 - 2014 Talend Inc.
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

package org.talend.camel;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class TalendComponentParallelTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Test
    public void testJobParallel() throws Exception {
        resultEndpoint.expectedMessageCount(3);
        sendBody("direct:test", "propagateHeader=false");
        resultEndpoint.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("direct:test")
                    .split(constant("1,2,3").tokenize(",")).parallelProcessing()
                    .recipientList(constant("talend://org.talend.camel.TestJob"))
                    .to("mock:result");
            }
        };
    }

}
