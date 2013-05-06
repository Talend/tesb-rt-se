/*
 * #%L
 * TIF :: Talend Component
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
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

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

public class TalendComponentTest extends CamelTestSupport {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        deleteDirectory("target/output");
    }

    @Test
    public void testRunJobWithDefaultContext() throws Exception {
        
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);       
        
        sendBody("direct:defaultContext", "foo");
        assertMockEndpointsSatisfied();
        assertFileExists("target/output/out.csv");
    }

    @Test
    public void testRunJobWithCustomContext() throws Exception {
        
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);       
        
        sendBody("direct:customContext", "foo");
        assertMockEndpointsSatisfied();
        assertFileExists("target/output/out.csv");
    }

    @Test
    public void testRunJobWithCustomContextParam() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);

        Map<String, String> properties = Collections.singletonMap("filename", "target/output/outCtxParam1.csv");
        mock.getCamelContext().setProperties(properties);
        sendBody("direct:defaultContext", "foo");
        assertMockEndpointsSatisfied();
        assertFileExists("target/output/outCtxParam1.csv");
    }

    @Test
    public void testRunJobWithCustomContextParamFromHeaders() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);

        Map<String, Object> headers = Collections.singletonMap("filename", (Object) "target/output/outCtxParam2.csv");
        sendBody("direct:defaultContext", "foo", headers);
        assertMockEndpointsSatisfied();
        assertFileExists("target/output/outCtxParam2.csv");
    }

    @Test
    public void testRunJobWithNoExistingCustomContextParam() throws Exception {
        
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);

        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("noExistingTalendParam", "bar");
        sendBody("direct:defaultContext", "foo", headers);
        assertMockEndpointsSatisfied();
        assertFileExists("target/output/out.csv");
    }
    
    public static void assertFileExists(String filename) {
            File file = new File(filename).getAbsoluteFile();
            assertTrue("File " + filename + " should exist", file.exists());
        } 

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("direct:defaultContext")
                    .to("talend://talendesb.jobcamel_0_1.jobCamel")
                    .to("mock:result");

                from("direct:customContext")
                    .to("talend://talendesb.jobcamel_0_1.jobCamel?context=Default")
                    .to("mock:result");
            }
        };
    }
}
