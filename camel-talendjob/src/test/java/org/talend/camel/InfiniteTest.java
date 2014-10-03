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

import java.util.concurrent.TimeUnit;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

import routines.system.api.TalendJob;

public class InfiniteTest extends CamelTestSupport {

    static {
        BasicConfigurator.configure();
    }

    public static class JobInfinite implements TalendJob {
        public String[][] runJob(String[] args) {
            fail();
            return null;
        }
        public int runJobInTOS(String[] args) {
//            while (true) {
//            }
            try {
                Thread.sleep(10000);
                fail();
            } catch (InterruptedException e) {
                // expexcted
            }
            return 0;
        }
    }

    @Override
    protected int getShutdownTimeout() {
        return 1;
    }

    @Test
    public void testJobInfinite() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        sendBody("seda:infinite", null);
        context.stopRoute("infinite");
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("seda:infinite").routeId("infinite")
                    .to("talend://org.talend.camel.InfiniteTest$JobInfinite?propagateHeader=false")
                    .to("mock:result");
            }
        };
    }
}
