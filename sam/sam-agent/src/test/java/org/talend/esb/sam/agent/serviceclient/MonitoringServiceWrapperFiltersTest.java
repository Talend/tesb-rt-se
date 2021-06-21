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
package org.talend.esb.sam.agent.serviceclient;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.esb.sam.agent.collector.EventCollector;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/META-INF/tesb/agent-context.xml", "/META-INF/tesb/sample-filters-context.xml"})
public class MonitoringServiceWrapperFiltersTest {
	@Resource
	EventCollector eventCollector;

    @Test
    public void testWrapper() {
    	Assert.assertEquals("We should have some filters", 2, eventCollector.getFilters().size());
        Assert.assertEquals("We should have some event manipulators", 2, eventCollector.getHandlers().size());
    }
}
