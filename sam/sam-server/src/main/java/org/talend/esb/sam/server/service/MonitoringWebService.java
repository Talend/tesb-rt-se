/*
 * #%L
 * Service Activity Monitoring :: Server
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
package org.talend.esb.sam.server.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.talend.esb.sam._2011._03.common.EventType;
import org.talend.esb.sam._2011._03.common.FaultType;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.MonitoringException;
import org.talend.esb.sam.monitoringservice.v1.MonitoringService;
import org.talend.esb.sam.monitoringservice.v1.PutEventsFault;

public class MonitoringWebService implements MonitoringService {

    private static Logger logger = Logger.getLogger(MonitoringWebService.class.getName());

    private org.talend.esb.sam.common.service.MonitoringService monitoringService;

    public String putEvents(List<EventType> eventTypes) throws PutEventsFault {
        logger.info("Received event(" + eventTypes.size() + ") from Agent.");
        List<Event> events = new ArrayList<Event>();

        try {
            for (EventType eventType : eventTypes) {
                Event event = EventTypeMapper.map(eventType);
                events.add(event);
            }
        } catch (RuntimeException e) {
            throwFault("004", "Could not map web service data to event." + e.getMessage(), e);
        }

        try {
            monitoringService.putEvents(events);
        } catch (MonitoringException e) {
            e.logException(Level.SEVERE);
            throwFault(e.getCode(), e.getMessage(), e);
        } catch (Throwable t) {
            throwFault("000", "Unknown error " + t.getMessage(), t);
        }

        return "success";
    }

    private static void throwFault(String code, String message, Throwable t) throws PutEventsFault {
        logger.severe("Throw Fault " + code + " " + message);

        FaultType faultType = new FaultType();
        faultType.setFaultCode(code);
        faultType.setFaultMessage(message);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        t.printStackTrace(printWriter);
        String exception = stringWriter.toString();

        faultType.setStackTrace(exception);

        logger.log(Level.SEVERE, "Exception", t);

        throw new PutEventsFault(message, faultType, t);
    }

    public void setMonitoringService(org.talend.esb.sam.common.service.MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

}
