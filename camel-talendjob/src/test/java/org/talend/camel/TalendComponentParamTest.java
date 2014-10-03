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
import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

public class TalendComponentParamTest extends CamelTestSupport {

    static {
        BasicConfigurator.configure();
    }

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Test
    public void testJobWithoutContext() throws Exception {
        resultEndpoint.expectedMinimumMessageCount(1);
        resultEndpoint.expectedBodiesReceived((Object) null);
        sendBody("direct:withoutContext", null);
        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testJobWithContext() throws Exception {
        resultEndpoint.expectedMinimumMessageCount(1);
        resultEndpoint.expectedBodiesReceived("--context=Default");
        sendBody("direct:withContext", null);
        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testJobWithParamFromHeaders() throws Exception {
        context.setUseBreadcrumb(false);
        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.expectedBodiesReceived("--context_param header=value");
        sendBody("direct:paramFromHeader", null);
        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testJobParamFromContext() throws Exception {
        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.expectedBodiesReceived("--context_param property=context");
        context.getProperties().put("property", "context");
        sendBody("direct:paramFromContext", null);
        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testJobParamFromEndpoint() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived("--context_param property=endpoint");
        sendBody("direct:paramFromEndpoint", null);
        resultEndpoint.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {

                from("direct:withoutContext")
                    .to("talend://org.talend.camel.TestJob?propagateHeader=false")
                    .to("mock:result");

                from("direct:withContext")
                    .to("talend://org.talend.camel.TestJob?context=Default&propagateHeader=false")
                    .to("mock:result");

                from("direct:paramFromHeader")
                    .setHeader("header", constant("value"))
                    .to("talend://org.talend.camel.TestJob")
                    .to("mock:result");

                from("direct:paramFromContext")
                    .to("talend://org.talend.camel.TestJob?propagateHeader=false")
                    .to("mock:result");

                from("direct:paramFromEndpoint")
                    .to("talend://org.talend.camel.TestJob?propagateHeader=false&endpointProperties.property=endpoint")
                    .to("mock:result");
            }
        };
    }
}
