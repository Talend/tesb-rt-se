/*
 * #%L
 * Service Locator Client for CXF
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
package org.talend.esb.servicelocator.client.internal;

import java.util.Date;

import org.talend.esb.servicelocator.client.ServiceLocatorException;

public interface EndpointNode {

    String getEndpointName();

    boolean exists() throws ServiceLocatorException, InterruptedException;

    void ensureExists(byte[] content) throws ServiceLocatorException, InterruptedException;

    void ensureRemoved() throws ServiceLocatorException, InterruptedException;

    void setLive(boolean persistent) throws ServiceLocatorException, InterruptedException;

    public void setOffline() throws ServiceLocatorException, InterruptedException;

    byte[] getContent() throws ServiceLocatorException, InterruptedException;

    void setContent(byte[] content) throws ServiceLocatorException, InterruptedException;

    boolean isLive() throws ServiceLocatorException, InterruptedException;

    Date getExpiryTime() throws ServiceLocatorException, InterruptedException;

    void setExpiryTime(Date expiryTime, boolean persistent) throws ServiceLocatorException, InterruptedException;

}

