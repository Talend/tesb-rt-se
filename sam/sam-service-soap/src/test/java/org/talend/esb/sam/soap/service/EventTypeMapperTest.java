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

import org.junit.Test;
import org.talend.esb.sam._2011._03.common.CustomInfoType;
import org.talend.esb.sam._2011._03.common.EventType;
import org.talend.esb.sam._2011._03.common.MessageInfoType;
import org.talend.esb.sam._2011._03.common.OriginatorType;
import org.talend.esb.sam.service.soap.EventTypeMapper;

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
