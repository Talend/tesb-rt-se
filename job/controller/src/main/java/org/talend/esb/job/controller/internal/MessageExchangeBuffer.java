/*
 * #%L
 * Talend :: ESB :: Job :: Controller
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
package org.talend.esb.job.controller.internal;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.talend.esb.job.controller.internal.RuntimeESBProviderCallback.MessageExchange;

/**
 * An buffer between providers of SOAP requests and consumers processing the request and providing a
 * response. A <code>MessageExchangeBuffer</code> is unbounded for requests from providers and blocks
 * consumers until a new request is available.  A <code>MessageExchangeBuffer</code> can be stopped to
 * accept requests. Still all buffered requests are forwarded to consumers until the buffer is empty.
 */
public class MessageExchangeBuffer {

    public static final Logger LOG =
        Logger.getLogger(MessageExchangeBuffer.class.getName());

    private static final WorkloadListener DUMMY_LISTENER = new WorkloadListener() {
        public void initialValues(MessageExchangeBuffer buffer, int consumersIdle, int waitingRequests) { }

        public void valuesChanged(MessageExchangeBuffer buffer, int consumersIdle, int waitingRequests) { }
    };

    /**
     * Marker which indicates if retrieved from queue that no more requests are to be expected. Should always
     * be re-added to queue if retrieved to let other consumers know about.
     */
    private static final MessageExchange POISON = new MessageExchange(null);

    private volatile Status status = Status.RUNNING;

    private final AtomicInteger idleConsumers = new AtomicInteger(0);

    private final BlockingQueue<MessageExchange> requests = new LinkedBlockingQueue<MessageExchange>();

    private WorkloadListener listener = DUMMY_LISTENER;


    /**
     * Removes and returns a request from the buffer. The response related to the request is put into the
     * <code>MessageExchange</code> as soon as available. If necessary waits until one request is available.
     *
     * @return the request wrapped in a <code>MessageExchange</code>. The response related to the request must
     *         be added to the <code>MessageExchange</code>.
     *
     * @throws BufferStoppedException
     *             thrown if no request is available anymore and the buffer was already stopped or stopped
     *             when waiting for a request to become available
     * @throws InterruptedException
     *             when waiting for a request to become available the current {@link Thread} was interrupted
     */
    public MessageExchange take() throws BufferStoppedException, InterruptedException {
        MessageExchange currentExchange = null;
        try {
            idleConsumers.getAndIncrement();
            currentExchange = requests.take();
        } finally {
            idleConsumers.getAndDecrement();
        }

        if (status == Status.STOPPING && requests.size() <= 1) {
            status = Status.STOPPED;
        }

        if (currentExchange == POISON) {
            putPoison();
            throw new BufferStoppedException();
        } else {
            listener.valuesChanged(this, consumersIdle(), requestsWaiting());
            diagnose("Took one request from buffer.");
            return currentExchange;
        }
    }

    /**
     * Inserts the request wrapped in the given <code>MessageExchange</code> at the end of the buffer. The
     * response related to the request will be  put into the <code>MessageExchange</code> as soon as
     * available.
     *
     * @param messageExchange
     *             <code>MessageExchange</code> to insert into the buffer, must not be <code>null</code>.
     *
     * @throws BufferStoppedException
     *             thrown if the buffer is already closed and requests are not accepted anymore.
     * @throws InterruptedException
     *             should never happen as items can always immediately  be added to the buffer
     */
    public void  put(MessageExchange messageExchange) throws InterruptedException, BufferStoppedException {
        synchronized (status) {
            if (status.isRunning()) {
                requests.put(messageExchange);
                listener.valuesChanged(this, consumersIdle(), requestsWaiting());
                diagnose("Put one request into buffer.");
            } else {
                throw new BufferStoppedException();
            }
        }
    }

    /**
     * Immediately stops the buffer to accept further requests. Requests already in the buffer may stille be
     * retrieved by consumers.
     */
    public void stop() {
        synchronized (status) {
            if (status.isRunning()) {
                putPoison();
                status = Status.STOPPING;
            }
        }
    }

    /**
     * Indicates whether the buffer was stopped and all pending requests were removed and processed by
     * consumers.
     *
     * @return <code>true</code> iff buffer stopped and empty.
     */
    public boolean isStopped() {
        return status.isStopped();
    }

    public void setWorkloadListener(WorkloadListener workloadListener) {
        listener = (workloadListener != null) ? workloadListener : DUMMY_LISTENER;
        listener.initialValues(this, consumersIdle(), requestsWaiting());
    }

    /**
     * Return the number of consumers waiting in the buffer to process a request.
     *
     * @return number of waiting consumers
     */
    public int consumersIdle() {
        return idleConsumers.get();
    }

    /**
     * Return the number of requests waiting in the buffer to be processed.
     *
     * @return number of waiting requests
     */
    public int requestsWaiting() {
        int poisonItems = status.isRunning() ? 0 : 1;

        return requests.size() - poisonItems;
    }

    private void putPoison() {
        boolean success = false;
        while (!success) {
            try {
                requests.put(POISON);
                success = true;
            } catch (InterruptedException e) {
                LOG.throwing(this.getClass().getName(), "stop", e);
            }
        }
    }

    private void diagnose(String statusMsg) {
        if (LOG.isLoggable(Level.FINE)) {
            if (statusMsg != null && !statusMsg.isEmpty()) {
                LOG.fine(statusMsg);
            }
            LOG.fine(idleConsumers + " consumers waiting for requests,"
                    + requests.size() + " requests waiting to be processed.");
        }
    }
    /**
     * A <code>WorkloadListener</code> gets notified about the workload of a {@link MessageExchangeBuffer}.
     * The workload may change as result of putting a request which is not immediately taken by a consumer or
     * by a new consumer waiting to get served with a request.
     */
    public static interface WorkloadListener {
        void initialValues(MessageExchangeBuffer buffer, int idleConsumers, int waitingRequests);

        void valuesChanged(MessageExchangeBuffer buffer, int idleConsumers, int waitingRequests);
    }

    private static enum Status {
        RUNNING(0), STOPPING(1), STOPPED(2);

        private int id;

        Status(int id) {
            this.id = id;
        }

        public boolean isRunning() {
            return id == 0;
        }

        public boolean isStopped() {
            return id == 2;
        }
    }

    public static class BufferStoppedException extends Exception {

        private static final long serialVersionUID = 6139255074631002393L;

        public BufferStoppedException() { }

        public BufferStoppedException(String message) {
            super(message);
        }
    }
}
