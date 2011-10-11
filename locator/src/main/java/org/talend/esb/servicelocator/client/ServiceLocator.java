/*
 * #%L
 * Service Locator Client for CXF
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
package org.talend.esb.servicelocator.client;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * This is the entry point for clients of the Service Locator. To access the Service Locator clients have to
 * first {@link #connect() connect} to the Service Locator to get a session assigned. Once the connection is
 * established the client will periodically send heart beats to the server to keep the session alive.
 * <p>
 * The Service Locator provides the following operations.
 * <ul>
 * <li>An endpoint for a specific service can be registered.</li>
 * <li>All live endpoints for a specific service that were registered before by other clients can be looked
 * up.</li>
 * <li>All services for which an endpoint was ever registered can be retrieved.</li>
 * <li>For a specific service all endpoints that were ever registered can be retrieved whether they are
 * currently live or not.</li>
 * </ul>
 * 
 */
public interface ServiceLocator {

    /**
     * Establish a connection to the Service Locator. After successful connection the specified
     * {@link PostConnectAction} is run. If the session to the server expires because the server could not be
     * reached within the {@link #setSessionTimeout(int) specified time}, a reconnect is automatically
     * executed as soon as the server can be reached again. Because after a session time out all registered
     * endpoints are removed it is important to specify a {@link PostConnectAction} that re-registers all
     * endpoints.
     * 
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting for a successful connection to
     *             the ServiceLocator
     * @throws ServiceLocatorException
     *             the connect operation failed
     */
    void connect() throws InterruptedException, ServiceLocatorException;

    /**
     * Disconnects from a Service Locator server. All endpoints that were registered before are removed from
     * the server. To be able to communicate with a Service Locator server again the client has to
     * {@link #connect() connect} again.
     * 
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting for the disconnect to happen
     * @throws ServiceLocatorException
     */
    void disconnect() throws InterruptedException, ServiceLocatorException;

    /**
     * For a given service register the endpoint of a concrete provider of this service. If the client is
     * destroyed, disconnected, or fails to successfully send the heartbeat for a period of time defined by
     * the {@link #setSessionTimeout(int) session timeout parameter} the endpoint is removed from the Service
     * Locator. To ensure that all available endpoints are re-registered when the client reconnects after a
     * session expired a {@link PostConnectAction} should be {@link #setPostConnectAction(PostConnectAction)
     * set} that registers all endpoints.
     * 
     * @param serviceName
     *            the name of the service the endpoint is registered for, must not be <code>null</code>
     * @param endpoint
     *            the endpoint to register, must not be <code>null</code>
     * @throws ServiceLocatorException
     *             the server returned an error
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting for a response of the
     *             ServiceLocator
     */
    void register(QName serviceName, String endpoint) throws ServiceLocatorException, InterruptedException;

    void register(QName serviceName, String endpoint, SLProperties properties)
        throws ServiceLocatorException, InterruptedException;

    void register(EndpointProvider eprProvider) throws ServiceLocatorException, InterruptedException;

    /**
     * For a given service unregister a previously registered endpoint.
     * 
     * @param serviceName
     *            the name of the service the endpoint is unregistered for, must not be <code>null</code>
     * @param endpoint
     *            the endpoint to unregister, must not be <code>null</code>
     * @param serviceName
     * @param endpoint
     * @throws ServiceLocatorException
     *             the server returned an error
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting for a response of the
     *             ServiceLocator
     */
    void unregister(QName serviceName, String endpoint) throws ServiceLocatorException, InterruptedException;

    void unregister(EndpointProvider epProvider) throws ServiceLocatorException, InterruptedException;

    void removeEndpoint(QName serviceName, String endpoint) throws ServiceLocatorException,
            InterruptedException;

    /**
     * Return all services for which endpoints are registered at the Service Locator Service.
     * 
     * @return a possibly empty list of services
     * 
     * @throws ServiceLocatorException
     *             the server returned an error
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting for a response of the
     *             ServiceLocator
     */
    List<QName> getServices() throws InterruptedException, ServiceLocatorException;

    SLEndpoint getEndpoint(final QName serviceName, final String endpoint) throws ServiceLocatorException,
            InterruptedException;

    List<SLEndpoint> getEndpoints(QName serviceName) throws ServiceLocatorException, InterruptedException;

    /**
     * For the given service return all endpoints that currently registered at the Service Locator Service.
     * 
     * @param serviceName
     *            the name of the service for which to get the endpoints, must not be <code>null</code>
     * @return a possibly empty list of endpoints
     * @throws ServiceLocatorException
     *             the server returned an error
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting for a response of the
     *             ServiceLocator
     */
    List<String> getEndpointNames(QName serviceName) throws ServiceLocatorException, InterruptedException;

    /**
     * For the given service return all endpoints that currently registered at the Service Locator Service.
     * 
     * @param serviceName
     *            the name of the service for which to get the endpoints, must not be <code>null</code>
     * @return a possibly empty list of endpoints
     * @throws ServiceLocatorException
     *             the server returned an error
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting for a response of the
     *             ServiceLocator
     */
    List<String> lookup(QName serviceName) throws ServiceLocatorException, InterruptedException;

    List<String> lookup(QName serviceName, SLPropertiesMatcher matcher) throws ServiceLocatorException,
            InterruptedException;

    void setPostConnectAction(PostConnectAction postConnectAction);

    /**
     * Callback interface to define actions that must be executed after a successful connect or reconnect.
     */
    static interface PostConnectAction {
        /**
         * Execute this after the connection to the Service Locator is established or re-established.
         * 
         * @param lc
         *            the Service Locator client that just successfully connected to the server, must not be
         *            <code>null</code>
         */
        void process(ServiceLocator lc);
    }

}