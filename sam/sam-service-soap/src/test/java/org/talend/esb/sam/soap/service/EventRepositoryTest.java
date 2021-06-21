/*
 * #%L
 * Service Activity Monitoring :: Server
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
package org.talend.esb.sam.soap.service;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.annotation.Resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.server.persistence.EventRepositoryImpl;

@ContextConfiguration(locations = {"/test-persistence.xml"})
public class EventRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Resource
    private EventRepositoryImpl eventRepository;

    @Before
    public void setUp() throws Exception {
        //executeSqlScript("create.sql", true);
    }

    @Test
    public void testWriteEvent() {
        GregorianCalendar cal = new GregorianCalendar(2000, Calendar.JANUARY, 1, 01 , 01, 10);

        Event event = EventCreator.createEvent("content", cal.getTime(),
                            EventTypeEnum.REQ_IN, "orig_id", "localhost", "10.0.0.1", "1", "2", "3", "operation",
                            "service", "http");
        event.getCustomInfo().put("mykey1", "myValue1");
        event.getCustomInfo().put("mykey2", "myValue2");

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
