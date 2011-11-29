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
package org.talend.esb.sam.common.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MonitoringException extends RuntimeException {

    private static final Logger LOG = Logger.getLogger(MonitoringException.class.getName());
    private static final long serialVersionUID = 3127641209174705808L;

    private final String code;
    private final String message;
    private final List<Event> events = new ArrayList<Event>();

    public MonitoringException(String code, String message, Throwable t) {
        this(code, message, t, Collections.<Event>emptyList());
    }

    public MonitoringException(String code, String message, Throwable t,
            Event event) {
        this(code, message, t, Collections.singletonList(event));
    }

    public MonitoringException(String code, String message, Throwable t,
            List<Event> events) {
        super(t);
        this.code = code;
        this.message = message;
        this.events.addAll(events);
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Prints the error message as log message
     * 
     * @param e
     */
    public void logException(Level level) {
        if (!LOG.isLoggable(level)) {
            return;
        }
        final StringBuilder builder = new StringBuilder();
        builder.append("\n----------------------------------------------------");
        builder.append("\nMonitoringException");
        builder.append("\n----------------------------------------------------");
        builder.append("\nCode:    ").append(code);
        builder.append("\nMessage: ").append(message);
        builder.append("\n----------------------------------------------------");
        if (events != null) {
            for (Event event : events) {
                builder.append("\nEvent:");
                if (event.getMessageInfo() != null) {
                    builder.append("\nMessage id: ").append(event.getMessageInfo().getMessageId());
                    builder.append("\nFlow id:    ").append(event.getMessageInfo().getFlowId());
                    builder.append("\n----------------------------------------------------");
                } else {
                    builder.append("\nNo message id and no flow id");
                }
            }
        }
        builder.append("\n----------------------------------------------------\n");
        LOG.log(level, builder.toString(), this);
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public void addEvents(Collection<Event> eventCollection) {
        events.addAll(eventCollection);
    }
}
