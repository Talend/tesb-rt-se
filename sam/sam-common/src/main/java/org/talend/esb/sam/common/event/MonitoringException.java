/*
 * #%L
 * Service Activity Monitoring :: Common
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
package org.talend.esb.sam.common.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The Class MonitoringException describes the monitoring exception.
 */
public class MonitoringException extends RuntimeException {

    private static final Logger LOG = Logger.getLogger(MonitoringException.class.getName());
    private static final long serialVersionUID = 3127641209174705808L;

    private final String code;
    private final String message;
    private final List<Event> events = new ArrayList<Event>();

    /**
     * Instantiates a new monitoring exception.
     *
     * @param code the code
     * @param message the message
     * @param t the Trowable type for exeption definition
     */
    public MonitoringException(String code, String message, Throwable t) {
        this(code, message, t, Collections.<Event>emptyList());
    }

    /**
     * Instantiates a new monitoring exception.
     *
     * @param code the monitoring exception code
     * @param message the message
     * @param t Trowable type for exeption definition
     * @param event the event
     */
    public MonitoringException(String code, String message, Throwable t,
            Event event) {
        this(code, message, t, Collections.singletonList(event));
    }

    /**
     * Instantiates a new monitoring exception.
     *
     * @param code the monitoring exception code
     * @param message the message
     * @param t Trowable type for exeption definition
     * @param events the events
     */
    public MonitoringException(String code, String message, Throwable t,
            List<Event> events) {
        super(t);
        this.code = code;
        this.message = message;
        this.events.addAll(events);
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#getMessage()
     */
    public String getMessage() {
        return message;
    }

    /**
     * Prints the error message as log message.
     *
     * @param level the log level
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

    /**
     * Adds the event.
     *
     * @param event the event
     */
    public void addEvent(Event event) {
        events.add(event);
    }

    /**
     * Adds the events.
     *
     * @param eventCollection the event collection
     */
    public void addEvents(Collection<Event> eventCollection) {
        events.addAll(eventCollection);
    }
}
