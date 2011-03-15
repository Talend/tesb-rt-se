/*
 * #%L
 * Service Activity Monitoring :: Agent
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
