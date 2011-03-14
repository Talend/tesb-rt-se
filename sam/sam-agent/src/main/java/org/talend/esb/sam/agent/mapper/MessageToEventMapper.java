package org.talend.esb.sam.agent.mapper;

import org.apache.cxf.message.Message;
import org.talend.esb.sam.agent.interceptor.InterceptorType;
import org.talend.esb.sam.common.event.Event;

public interface MessageToEventMapper {

    public abstract Event mapToEvent(Message message, InterceptorType type, String content);

}
