/*
 * #%L
 * Talend :: ESB :: Job :: Controller
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
package org.talend.esb.job.controller;

/**
 * A very generic interface describing an operation.
 *
 */
public interface GenericOperation {

    /**
     * Invoke the operation.
     * 
     * @param payload the request payload, must not be <code>null</code>
     * @param isRequestResponse flag whether a response is expected
     * @return the response or <code>null</code> if the operation is a one-way
     * @throws Exception a possible fault
     */
    Object invoke(Object payload, boolean isRequestResponse) throws Exception;
}
