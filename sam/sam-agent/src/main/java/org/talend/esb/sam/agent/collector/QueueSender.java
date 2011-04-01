package org.talend.esb.sam.agent.collector;

import java.util.Queue;
import java.util.logging.Logger;

import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.spi.EventHandler;

public class QueueSender implements EventHandler {
    private static Logger logger = Logger.getLogger(EventCollectorImpl.class
                    .getName());

    private Queue<Event> queue;

    public void setQueue(Queue<Event> queue) {
        this.queue = queue;
    }

    /**
     * Stores an event in the queue and returns. So the synchronous execution of
     * this service is as short as possible.
     */
    @Override
    public void handleEvent(Event event) {
            String id = (event.getMessageInfo() != null) ? event.getMessageInfo().getMessageId() : null;
            logger.fine("Store event [message_id=" + id + "] in cache.");
            queue.add(event);
    }
}
