/*
 * #%L
 * Service Activity Monitoring :: Common
 * %%
 * Copyright (C) 2011 Talend Inc.
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
package org.talend.esb.sam.common.filter.impl;

import org.junit.Assert;
import org.junit.Test;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.common.event.MessageInfo;

public class JxPathFilterTest {

    @Test
    public void testExpression() {
        Event event = new Event();
        event.setContent("test");
        event.setEventType(EventTypeEnum.FAULT_IN);
        event.getCustomInfo().put("key1", "value1");
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setFlowId("urn:flowid");
        messageInfo.setMessageId("urn:messageId");
        messageInfo.setOperationName("{namespace}opname");
        messageInfo.setPortType("{namespace}portname");
        messageInfo.setTransportType("http");
        event.setMessageInfo(messageInfo);
        
        JxPathFilter jxPathFilter = new JxPathFilter();
        jxPathFilter.setExpression("content='test'");
        Assert.assertTrue("Event should be filtered ", jxPathFilter.filter(event));
        
        checkFiltered("content='test'", event);
        checkNotFiltered("content='test2'", event);
        checkFiltered("eventType='FAULT_IN'", event);
        checkFiltered("content='test' and eventType='FAULT_IN'", event);
        checkFiltered("content='test' and eventType='FAULT_IN'", event);
        checkFiltered("content='test' and eventType='FAULT_IN' and customInfo/key1='value1'", event);
        
        checkFiltered("messageInfo/flowId='urn:flowid' and messageInfo/operationName='{namespace}opname'", event);
    }
    
    public void checkFiltered(String expression, Event event) {
        boolean filtered = new JxPathFilter(expression).filter(event);
        Assert.assertTrue("Event should be filtered for expression " + expression, filtered);
    }
    
    public void checkNotFiltered(String expression, Event event) {
        boolean filtered = new JxPathFilter(expression).filter(event);
        Assert.assertFalse("Event should not be filtered for expression " + expression, filtered);
    }
}
