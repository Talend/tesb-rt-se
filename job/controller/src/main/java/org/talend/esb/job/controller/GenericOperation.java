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
package org.talend.esb.job.controller;

/**
 * A very generic interface describing an operation which is backed by a {@link TalendESBJob job}.
 */
public interface GenericOperation {

    /**
     * Invoke the operation. If the operation was not yet {@link #start(String[]) started} or was already
     * {@link #stop() stopped} invocation may fail.
     *
     * @param payload the request payload, must not be <code>null</code>
     * @param isRequestResponse flag whether a response is expected
     * @return the response or <code>null</code> if the operation is a one-way
     * @throws Exception a possible fault
     * @throws IllegalStateException if the operation was not yet started or already closed.
     */
    Object invoke(Object payload, boolean isRequestResponse) throws IllegalStateException, Exception;

    /**
     * Start this operation. After being started the operation is ready to get invoked.
     *
     * @param arguments pass these arguments to the backing {@link TalendESBJob job}
     */
    void start(String[] arguments);

    /**
     * Stop this operation. After being stopped the operation will fail to execute invocations.
     */
    void stop();
}
