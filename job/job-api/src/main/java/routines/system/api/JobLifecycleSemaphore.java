/*******************************************************************************
 *  Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 *  All rights reserved.
 *
 *  This program and the accompanying materials are made available
 *  under the terms of the Apache License v2.0
 *  which accompanies this distribution, and is available at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 ******************************************************************************/
package routines.system.api;

/**
 * Simple semaphore for for internal synchronization of
 * jobs invoked by routes in "sticky", i.e. re-use mode.
 */
public class JobLifecycleSemaphore {

    private boolean up = false;

    public JobLifecycleSemaphore() {
        super();
    }

    /**
     * Wait at job startup.
     *
     * @throws InterruptedException on Thread interrupt.
     */
    public synchronized void waitForUpState() throws InterruptedException {
        while (!up) {
            wait(90000L);
        }
    }

    /**
     * Wait for the job being teared down.
     *
     * @throws InterruptedException on Thread interrupt.
     */
    public synchronized void waitForDownState() throws InterruptedException {
        while (up) {
            wait(90000L);
        }
    }

    /**
     * Set the state flag for the job as "up" and signal.
     */
    public synchronized void signalUpState() {
        up = true;
        notifyAll();
    }

    /**
     * Set the state flag for the job as "down" and signal.
     */
    public synchronized void signalDownState() {
        up = false;
        notifyAll();
    }
}
