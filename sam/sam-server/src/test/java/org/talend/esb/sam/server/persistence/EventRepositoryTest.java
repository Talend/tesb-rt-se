package org.talend.esb.sam.server.persistence;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.talend.esb.sam.common.event.CustomInfo;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.common.event.persistence.EventRepository;
import org.talend.esb.sam.common.util.EventCreator;

@ContextConfiguration("/EventRepositoryTest-context.xml")
public class EventRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
    
    @Resource
    private EventRepository eventRepository;

    @Before
    public void setUp() throws Exception {
        executeSqlScript("create.sql", true);
    }

    @Test
    public void testWriteEvent() {
        EventCreator creator = new EventCreator();
        GregorianCalendar cal = new GregorianCalendar(2000, Calendar.JANUARY, 1, 01 , 01, 10);
        
        List<CustomInfo> ciList = new ArrayList<CustomInfo>();
        CustomInfo ci1 = new CustomInfo();
        ci1.setCustKey("mykey1");
        ci1.setCustValue("myValue1");
        ciList.add(ci1);
        CustomInfo ci2 = new CustomInfo();
        ci2.setCustKey("mykey2");
        ci2.setCustValue("myValue2");
        ciList.add(ci2);
        
        Event event = creator.createEvent("content", cal.getTime(),
                            EventTypeEnum.REQ_IN, "orig_id", "localhost", "10.0.0.1", "1", "2", "3", "operation",
                            "service", "http");
        event.getCustomInfoList().clear();
        event.getCustomInfoList().addAll(ciList);
        
        Assert.assertNull(event.getPersistedId());
        eventRepository.writeEvent(event);
        Assert.assertNotNull(event.getPersistedId());
        
        //read Event from database
        Event readEvent = eventRepository.readEvent(event.getPersistedId().longValue());
        Assert.assertTrue(EqualsBuilder.reflectionEquals(event, readEvent));
                
    }

    @After
    public void tearDown() {
        executeSqlScript("drop.sql", true);
    }

}
