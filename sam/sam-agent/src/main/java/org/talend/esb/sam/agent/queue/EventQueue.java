package org.talend.esb.sam.agent.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

import javax.inject.Named;
import javax.inject.Singleton;

import org.talend.esb.sam.common.event.Event;

@Named("queue")
@Singleton
public class EventQueue extends ConcurrentLinkedQueue<Event> {

    /**
     * 
     */
    private static final long serialVersionUID = -6461582835945008902L;

}
