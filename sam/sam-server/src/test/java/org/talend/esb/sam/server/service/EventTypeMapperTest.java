package org.talend.esb.sam.server.service;

import org.junit.Test;
import org.talend.esb.sam._2011._03.common.CustomInfoType;
import org.talend.esb.sam._2011._03.common.EventType;
import org.talend.esb.sam._2011._03.common.MessageInfoType;
import org.talend.esb.sam._2011._03.common.OriginatorType;
import org.talend.esb.sam.server.service.EventTypeMapper;

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
