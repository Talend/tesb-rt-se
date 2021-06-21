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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.common.service.MonitoringService;
import org.talend.esb.sam.server.persistence.EventRowMapper;

/**
 * Tests the sevice implementation together with the database
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-server.xml", "/server-config.xml"})
public class MonitoringServiceImplTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Resource
    MonitoringService monitoringSerivce;

//    @Before
//    public void setUp() throws Exception {
//        executeSqlScript("create.sql", true);
//    }


    @Test
    public void testWritingSeveralEvents() {
        List<Event> events = new ArrayList<Event>();
        events.add(generateEvent());
        events.add(generateEvent());
        events.add(generateEvent());

        monitoringSerivce.putEvents(events);

        for (Event event : events) {
            EventRowMapper rowMapper = new EventRowMapper();
            Event loaded = jdbcTemplate.queryForObject("select * from EVENTS where ID=?", rowMapper, event.getPersistedId());
            Assert.assertNotNull(loaded);
            Assert.assertEquals(event.getPersistedId(), loaded.getPersistedId());
        }

    }

    @After
    public void tearDown() {
        executeSqlScript("drop.sql", true);
    }

    private Event generateEvent(){
        Date cal = Calendar.getInstance().getTime();
        String generated = Integer.valueOf(cal.toString().hashCode()).toString();
        return EventCreator.createEvent("<request><values><value>a</value></values></request>", cal, EventTypeEnum.REQ_IN, "JUnit", "localhost", "127.0.0.1", generated, generated, generated, "testOperation", "testPort", "JAVA");
    }

    //FIXME test filter

    //FIXME test handler
}
