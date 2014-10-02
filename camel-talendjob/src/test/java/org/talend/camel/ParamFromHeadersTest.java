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

import java.util.Arrays;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

import routines.system.api.TalendJob;

public class ParamFromHeadersTest extends CamelTestSupport {

    static {
        BasicConfigurator.configure();
    }

    public static class JobParamFromHeaders implements TalendJob {
        public String[][] runJob(String[] args) {
            fail();
            return null;
        }
        public int runJobInTOS(String[] args) {
            assertTrue(0 >= Arrays.binarySearch(args, "--context_param header=value"));
            return 0;
        }
    }

    @Test
    public void testJobWithParamFromHeaders() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        sendBody("direct:paramFromHeader", null);
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("direct:paramFromHeader")
                    .setHeader("header", constant("value"))
                    .to("talend://org.talend.camel.ParamFromHeadersTest$JobParamFromHeaders")
                    .to("mock:result");
            }
        };
    }
}
