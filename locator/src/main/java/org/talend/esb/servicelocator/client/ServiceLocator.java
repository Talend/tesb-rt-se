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
package org.talend.esb.servicelocator.client;

import java.util.List;

import javax.xml.namespace.QName;

import org.talend.esb.servicelocator.client.ServiceLocator.PostConnectAction;

/**
 * This is the entry point for clients of the Service Locator. To access the
 * Service Locator clients have to first {@link #connect() connect} to the
 * Service Locator to get a session assigned at the server. Once the connection
 * is established the client will periodically send heart beats to the server to
 * keep the session alive. The session gets invalidated if the client is
 * destroyed, disconnected, or fails to successfully send the heart beat for a
 * period of time defined by the {@link #setSessionTimeout(int) session timeout
 * parameter}.
 * <p>
 * The Service Locator provides the following operations.
 * <ul>
 * <li>Register an endpoint for a specific service. If registered as non
 * persistent an endpoint is marked live as long as the session of this client
 * is alive or it is explicitly unregistered. If registered as persistent the
 * endpoint is marked live until the endpoint is explicitly unregistered.</li>
 *
 * <li>Unregister an endpoint for a specific service. The endpoint is marked as
 * non live, but still in the list of all endpoints for a service.</li>
 *
 * <li>Remove an endpoint for a specific service. The endpoint is removed from
 * the list of endpoints for the given service.</li>
 *
 * <li>Look up all live endpoints for a specific service that were registered
 * before by other clients.</li>
 *
 * <li>All services for which an endpoint was ever registered can be retrieved.</li>
 *
 * <li>For a specific service all endpoints that were ever registered can be
 * retrieved whether they are currently live or not.</li>
 * </ul>
 *
 * To ensure that all available endpoints are re-registered when the client
 * reconnects after a session expired a {@link PostConnectAction} should be
 * {@link #setPostConnectAction(PostConnectAction) set} that registers all
 * endpoints again and so marks them as live.
 */
public interface ServiceLocator {

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
    void connect() throws InterruptedException, ServiceLocatorException;

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

    /**
     * For a given service register the endpoint of a concrete provider of this
     * service. The endpoint is marked as live as long as this client is
     * connected and the session on the server is valid.
     * <p>
     * The endpoint is categorized as being a SOAP / HTTP endpoint.
     *
     * @param serviceName
     *            the name of the service the endpoint is registered for, must
     *            not be <code>null</code>
     * @param endpoint
     *            the endpoint to register, must not be <code>null</code>
     * @throws ServiceLocatorException
     *             the server returned an error
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting
     *             for a response of the ServiceLocator
     */
    void register(QName serviceName, String endpoint)
        throws ServiceLocatorException, InterruptedException;

    /**
     *
     * @param serviceName
     * @param endpoint
     * @param presistent
     * @throws ServiceLocatorException
     * @throws InterruptedException
     */
    void register(QName serviceName, String endpoint, boolean persistent) throws ServiceLocatorException,
            InterruptedException;

    /**
     * For a given service register the endpoint of a concrete provider of this
     * service. The endpoint is marked as live as long as this client is
     * connected and the session on the server is valid. In addition a set of
     * custom properties is defined for the endpoint. They may be used as
     * additional selection criteria during the lookup.
     *
     * @param serviceName
     *            the name of the service the endpoint is registered for, must
     *            not be <code>null</code>
     * @param endpoint
     *            the endpoint to register, must not be <code>null</code>
     * @param properties
     *            custom properties of the endpoint that can be used as
     *            additional selection criteria when doing a lookup.
     * @throws ServiceLocatorException
     *             the server returned an error
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting
     *             for a response of the ServiceLocator
     */
    void register(QName serviceName, String endpoint, SLProperties properties)
        throws ServiceLocatorException, InterruptedException;

    /**
     *
     * @param serviceName
     * @param endpoint
     * @param properties
     * @param persistent
     * @throws ServiceLocatorException
     * @throws InterruptedException
     */
    void register(QName serviceName, String endpoint,
            SLProperties properties, boolean persistent)
        throws ServiceLocatorException, InterruptedException;

    /**
     * For a given service register the endpoint as defined in the given {@link Endpoint
     * EndpointProvider}. The endpoint is marked as live as long as this client is connected and the
     * session on the server is valid.
     *
     * @param eprProvider
     *             provides all the necessary information to register an endpoint like name of the service
     *             for which to register the endpoint, the endpoint URL, must not be <code>null</code>.
     * @throws ServiceLocatorException
     *             the server returned an error
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting for a response of the
     *             ServiceLocator
     */
    void register(Endpoint eprProvider) throws ServiceLocatorException, InterruptedException;

    /**
     * For a given service register the endpoint as defined in the given {@link Endpoint
     * EndpointProvider}. If the given persistent flag is not set the endpoint is marked as live as long
     * as this client is connected and the session on the server is valid. Otherwise it is marked live
     * independent of the sesion state.
     *
     * @param eprProvider
     *             provides all the necessary information to register an endpoint like name of the service
     *             for which to register the endpoint, the endpoint URL, must not be <code>null</code>.
     * @param persistent
     *             iff true the endpoint is marked live until it is explicitly unregistered.
     * @throws ServiceLocatorException
     *             the server returned an error
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting for a response of the
     *             ServiceLocator
     */
    void register(Endpoint eprProvider, boolean persistent)
        throws ServiceLocatorException, InterruptedException;

    /**
     * For a given service unregister a previously registered endpoint.
     *
     * @param serviceName
     *            the name of the service the endpoint is unregistered for, must
     *            not be <code>null</code>
     * @param endpoint
     *            the endpoint to unregister, must not be <code>null</code>
     * @throws ServiceLocatorException
     *             the server returned an error
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting
     *             for a response of the ServiceLocator
     */
    void unregister(QName serviceName, String endpoint)
        throws ServiceLocatorException, InterruptedException;

    /**
     * For a given service unregister a previously registered endpoint.
     *
     * @param eprProvider
     *             provides all the necessary information to unregister an endpoint like name of the service
     *             for which to register the endpoint and the endpoint URL, must not be <code>null</code>.
     * @throws ServiceLocatorException
     *             the server returned an error
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting for a response of the
     *             ServiceLocator
     */
    void unregister(Endpoint epProvider) throws ServiceLocatorException, InterruptedException;

    /**
     * Updates the time-to-live (ttl) for specified endpoint. If the endpoint did not have ttl before
     * then this method sets it up.
     *
     * @param serviceName
     *          name of the service the endpoint belongs to
     * @param endpoint
     *          name of the endpoint
     * @param timetolive
     *          period of time (in seconds) when this endpoint is considered "alive"
     * @throws ServiceLocatorException
     *          thrown if a problem happened. For example if the endpoint does not exist,
     *          if given time-to-live is negative or zero, etc.
     * @throws InterruptedException
     *          the current <code>Thread</code> was interrupted when waiting for a response of the
     *          ServiceLocator
     */
    void updateTimetolive(QName serviceName, String endpoint, int timetolive)
            throws ServiceLocatorException, InterruptedException;

    /**
     * Remove the given endpoint from the list of endpoints of the given
     * service. When calling
     * <code>getEndpoint(serviceName)<code> the given endpoint will not be contained in the returned
     * list. In case no service with the given name exists or the given endpoint is not registered
     * with the service the state of the Service Locator remians unchanged.
     *
     * @param serviceName
     *            the name of the service the endpoint is removed from, must not
     *            be <code>null</code>
     * @param endpoint
     *            the endpoint to remove, must not be <code>null</code>
     * @throws ServiceLocatorException
     *             the server returned an error
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting
     *             for a response of the ServiceLocator
     */
    void removeEndpoint(QName serviceName, String endpoint)
        throws ServiceLocatorException, InterruptedException;

    /**
     * Return all services for which endpoints are registered at the Service
     * Locator Service.
     *
     * @return a possibly empty list of services
     *
     * @throws ServiceLocatorException
     *             the server returned an error
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting
     *             for a response of the ServiceLocator
     */
    List<QName> getServices() throws InterruptedException,
            ServiceLocatorException;

    /**
     * Return the complete endpoint information for the given endpoint
     * registered for the given service.
     *
     * @param serviceName
     *            the name of the service for which the endpoint is registered,
     *            must not be <code>null</code>
     * @param endpoint
     *            the endpoint for which to return the information, must not be
     *            <code>null</code>
     * @return the end point information, may be <code>null</code> if the
     *         endpoint was not registered before
     * @throws ServiceLocatorException
     *             the server returned an error
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting
     *             for a response of the ServiceLocator
     */
    SLEndpoint getEndpoint(final QName serviceName, final String endpoint)
        throws ServiceLocatorException, InterruptedException;

    /**
     * Return the complete endpoint informations for all endpoints registered
     * for the given service.
     *
     * @param serviceName
     *            the name of the service for which the endpoints ares
     *            registered, must not be <code>null</code>
     * @return the list of end point informations, may be empty if no the
     *         endpoints are registered for the service.
     * @throws ServiceLocatorException
     *             the server returned an error
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting
     *             for a response of the ServiceLocator
     */
    List<SLEndpoint> getEndpoints(QName serviceName)
        throws ServiceLocatorException, InterruptedException;

    /**
     * For the given service return all endpoints that currently registered at
     * the Service Locator Service.
     *
     * @param serviceName
     *            the name of the service for which to get the endpoints, must
     *            not be <code>null</code>
     * @return a possibly empty list of endpoints
     * @throws ServiceLocatorException
     *             the server returned an error
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting
     *             for a response of the ServiceLocator
     */
    List<String> getEndpointNames(QName serviceName)
        throws ServiceLocatorException, InterruptedException;

    /**
     * For the given service return all endpoints that are currently registered
     * at the Service Locator.
     *
     * @param serviceName
     *            the name of the service for which to get the endpoints, must
     *            not be <code>null</code>
     * @return a possibly empty list of endpoints
     * @throws ServiceLocatorException
     *             the server returned an error
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting
     *             for a response of the ServiceLocator
     */
    List<String> lookup(QName serviceName) throws ServiceLocatorException, InterruptedException;

    /**
     * For the given service return all endpoints that are currently registered
     * at the Service Locator and where the custom properties match the given
     * matcher.
     *
     * @param serviceName
     *            the name of the service for which to get the endpoints, must
     *            not be <code>null</code>
     * @param matcher
     *            custom properties of the endpoints returned match the
     *            criterias specified by this matcher
     * @return a possibly empty list of endpoints
     * @throws ServiceLocatorException
     *             the server returned an error
     * @throws InterruptedException
     *             the current <code>Thread</code> was interrupted when waiting
     *             for a response of the ServiceLocator
     */
    List<String> lookup(QName serviceName, SLPropertiesMatcher matcher)
        throws ServiceLocatorException, InterruptedException;

//    void updateActiveTimestamp(Endpoint endpoint, long activeTimestamp)
//            throws ServiceLocatorException, InterruptedException;

    /**
     * Specify the action to be be executed after the Service Locator has
     * connected to the server.
     *
     * @param postConnectAction
     *            the action to be executed, must not be <code>null</code>.
     */
    void addPostConnectAction(PostConnectAction postConnectAction);

    /**
     * Remove the action to be be executed after the Service Locator has
     * connected to the server.
     *
     * @param postConnectAction
     *            the action to be executed, must not be <code>null</code>.
     */
    void removePostConnectAction(PostConnectAction postConnectAction);

    /**
     * Callback interface to define actions that must be executed after a
     * successful connect or reconnect.
     */
    interface PostConnectAction {
        /**
         * Execute this after the connection to the Service Locator is
         * established or re-established.
         *
         * @param lc
         *            the Service Locator client that just successfully
         *            connected to the server, must not be <code>null</code>
         */
        void process(ServiceLocator lc);
    }

}
