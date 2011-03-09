package org.talend.esb.sam.agent.serviceclient;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.esb.sam.agent.collector.EventCollectorImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/agent-context.xml", "/sample-filters-context.xml"})
public class MonitoringServiceWrapperFiltersTest {
	@Resource
	EventCollectorImpl eventCollector;

    @Test
    public void testWrapper() {
    	Assert.assertEquals("We should have some filters", 2, eventCollector.getEventFilter().size());
        Assert.assertEquals("We should have some event manipulators", 2, eventCollector.getEventManipulator().size());
    }
}
