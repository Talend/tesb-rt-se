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
package org.talend.esb.sam.service.soap;

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

/**
 * The Class MonitoringWebService is implementing the monitoring service.
 */
public class MonitoringWebService implements MonitoringService {

    private static final Logger LOG = Logger.getLogger(MonitoringWebService.class.getName());

    private org.talend.esb.sam.common.service.MonitoringService monitoringService;

    /**
     * Sets the monitoring service.
     *
     * @param monitoringService the new monitoring service
     */
    public void setMonitoringService(org.talend.esb.sam.common.service.MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    /* (non-Javadoc)
     * @see org.talend.esb.sam.monitoringservice.v1.MonitoringService#putEvents(java.util.List)
     */
    public String putEvents(List<EventType> eventTypes) throws PutEventsFault {
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Received event(" + eventTypes.size() + ") from Agent.");
        }
        List<Event> events = new ArrayList<Event>(eventTypes.size());

        try {
            for (EventType eventType : eventTypes) {
                events.add(EventTypeMapper.map(eventType));
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

    /**
     * Throw fault.
     *
     * @param code the fault code
     * @param message the message
     * @param t the throwable type
     * @throws PutEventsFault
     */
    private static void throwFault(String code, String message, Throwable t) throws PutEventsFault {
        if (LOG.isLoggable(Level.SEVERE)) {
            LOG.log(Level.SEVERE, "Throw Fault " + code + " " + removeCRLF(message), t);
        }

        FaultType faultType = new FaultType();
        faultType.setFaultCode(code);
        faultType.setFaultMessage(message);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        t.printStackTrace(printWriter);

        faultType.setStackTrace(stringWriter.toString());

        throw new PutEventsFault(message, faultType, t);
    }

    private static String removeCRLF(String s) {
        return s.replaceAll("\r|\n", "");
    }
}
