package org.sopera.monitoring.server.service;

import org.junit.Test;
import org.sopera.monitoring._2010._09.common.CustomInfoType;
import org.sopera.monitoring._2010._09.common.EventType;
import org.sopera.monitoring._2010._09.common.MessageInfoType;
import org.sopera.monitoring._2010._09.common.OriginatorType;

public class EventTypeMapperTest {

    /**
     * Test with empty eventType parts to check for Nullpointer exceptions
     */
    @Test
    public void testEmpty() {
        EventType eventType = new EventType();
        EventTypeMapper.map(eventType);
        eventType.setCustomInfo(new CustomInfoType());
        EventTypeMapper.map(eventType);
        eventType.setMessageInfo(new MessageInfoType());
        EventTypeMapper.map(eventType);
        eventType.setOriginator(new OriginatorType());
        EventTypeMapper.map(eventType);
    }
}
