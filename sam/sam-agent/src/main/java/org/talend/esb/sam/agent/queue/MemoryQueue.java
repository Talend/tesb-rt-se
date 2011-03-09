package org.talend.esb.sam.agent.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.talend.esb.sam.common.event.Event;

public class MemoryQueue<E extends Event> implements Queue<E>{

	private java.util.Queue<E> queue = new ConcurrentLinkedQueue<E>();
	
	public MemoryQueue(){
	}
	
	public void add(E object) {
		queue.add(object);
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public E remove() {
		return queue.remove();
	}

}
