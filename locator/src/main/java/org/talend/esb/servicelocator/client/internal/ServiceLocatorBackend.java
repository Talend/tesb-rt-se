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

import org.talend.esb.servicelocator.client.ServiceLocator.PostConnectAction;
import org.talend.esb.servicelocator.client.ServiceLocatorException;

public interface ServiceLocatorBackend {

    /**
     * Establish a connection to the Service Locator. After successful
     * connection the specified {@link PostConnectAction} is run. If the session
     * to the server expires because the server could not be reached within the
     * {@link #setSessionTimeout(int) specified time}, a reconnect is
     * automatically executed as soon as the server can be reached again.
     * Because after a session time out all registered endpoints are removed it
     * is important to specify a {@link PostConnectAction} that re-registers all
     * endpoints.
     *
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting
     *             for a successful connection to the ServiceLocator
     * @throws ServiceLocatorException
     *             the connect operation failed
     */
    RootNode connect() throws InterruptedException, ServiceLocatorException;

    /**
     * Disconnects from a Service Locator server. All endpoints that were
     * registered before are removed from the server. To be able to communicate
     * with a Service Locator server again the client has to {@link #connect()
     * connect} again.
     *
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting
     *             for the disconnect to happen
     * @throws ServiceLocatorException
     */
    void disconnect() throws InterruptedException, ServiceLocatorException;

    boolean isConnected();

    /**
     * Specify the action to be be executed after the Service Locator has
     * connected to the server.
     *
     * @param postConnectAction
     *            the action to be executed, must not be <code>null</code>.
     */
    void addPostConnectAction(PostConnectAction postConnectAction);

    /**
     * Specify the action to be be executed after the Service Locator has
     * connected to the server.
     *
     * @param postConnectAction
     *            the action to be executed, must not be <code>null</code>.
     */
    void removePostConnectAction(PostConnectAction postConnectAction);

}
