package org.talend.esb.sam.agent.serviceclient;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.esb.sam.agent.collector.EventCollectorImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/agent-context.xml")
public class MonitoringServiceWrapperTest {
	@Resource
	EventCollectorImpl eventCollector;

    @Test
    public void testWrapper() {
        Assert.assertEquals("We should have no event filters", 0, eventCollector.getEventFilter().size());
        Assert.assertEquals("We should have no event manipulators", 0, eventCollector.getEventManipulator().size());
    }
}
