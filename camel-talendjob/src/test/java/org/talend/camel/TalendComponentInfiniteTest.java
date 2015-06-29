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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import routines.system.api.TalendJob;

public class TalendComponentInfiniteTest extends CamelTestSupport {

    public static class JobInfinite implements TalendJob {

        private static boolean passed = false;

        public static boolean isPassed() {
            boolean result = passed;
            passed = false;
            return result;
        }

        public String[][] runJob(String[] args) {
            return null;
        }

        public int runJobInTOS(String[] args) {
            synchronized (JobInfinite.class) {
                JobInfinite.class.notify();
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                passed = true;
            }
            return 0;
        }
    }

    @Override
    protected int getShutdownTimeout() {
        return 1;
    }

    @Test
    public void testJobInfiniteDirect() throws Exception {
        template.asyncRequestBody("direct:infinite", null);
        assertFalse(JobInfinite.isPassed());
        synchronized (JobInfinite.class) {
            JobInfinite.class.wait();
        }
        context.stop();
        assertTrue(JobInfinite.isPassed());
    }

    @Test
    public void testJobInfiniteSeda() throws Exception {
        sendBody("seda:infinite", null);
        assertFalse(JobInfinite.isPassed());
        synchronized (JobInfinite.class) {
            JobInfinite.class.wait();
        }
        context.stop();
        assertTrue(JobInfinite.isPassed());
    }

    @Test
    public void testJobInfiniteDirectParallel() throws Exception {
        template.asyncRequestBody("direct:parallel", null);
        assertFalse(JobInfinite.isPassed());
        synchronized (JobInfinite.class) {
            JobInfinite.class.wait();
        }
        context.stop();
        assertTrue(JobInfinite.isPassed());
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("direct:infinite")
                    .to("talend://org.talend.camel.TalendComponentInfiniteTest$JobInfinite");

                from("seda:infinite")
                    .to("talend://org.talend.camel.TalendComponentInfiniteTest$JobInfinite");

                from("direct:parallel")
                    .split(constant("1,2,3").tokenize(",")).parallelProcessing()
                    .to("talend://org.talend.camel.TalendComponentInfiniteTest$JobInfinite");
            }
        };
    }
}
