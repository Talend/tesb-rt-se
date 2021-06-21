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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.namespace.QName;

import org.apache.zookeeper.ZooKeeper;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.springframework.beans.factory.annotation.Value;

import org.talend.esb.servicelocator.client.Endpoint;
import org.talend.esb.servicelocator.client.EndpointNotFoundException;
import org.talend.esb.servicelocator.client.ExpiredEndpointCollector;
import org.talend.esb.servicelocator.client.SLEndpoint;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.SLPropertiesMatcher;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.SimpleEndpoint;
import org.talend.esb.servicelocator.client.WrongArgumentException;
import org.talend.esb.servicelocator.client.internal.zk.ZKBackend;

/**
 * This is the entry point for clients of the Service Locator. To access the
 * Service Locator clients have to first {@link #connect() connect} to the
 * Service Locator to get a session assigned. Once the connection is established
 * the client will periodically send heart beats to the server to keep the
 * session alive.
 * <p/>
 * The Service Locator provides the following operations.
 * <ul>
 * <li>An endpoint for a specific service can be registered.
 * <li>All endpoints for a specific service that were registered before by other
 * clients can be looked up.
 * </ul>
 */
@OsgiServiceProvider(classes={ServiceLocator.class,ExpiredEndpointCollector.class})
@Named
public class ServiceLocatorImpl implements ServiceLocator, ExpiredEndpointCollector {

    private static final Logger LOG = Logger.getLogger(ServiceLocatorImpl.class.getName());

    @Inject
    ServiceLocatorBackend backend;

    private EndpointTransformer transformer = new EndpointTransformerImpl();

    private Boolean endpointCollectionEnable;

    private Integer endpointCollectionInterval;

    private Timer timer;

    private int schedulerRequestCounter = 0;

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void connect() throws InterruptedException, ServiceLocatorException {
        getBackend().connect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void disconnect() throws InterruptedException, ServiceLocatorException {
        getBackend().disconnect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void register(QName serviceName, String endpoint) throws ServiceLocatorException,
            InterruptedException {
        register(new SimpleEndpoint(serviceName, endpoint), false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void register(QName serviceName, String endpoint, boolean persistent)
            throws ServiceLocatorException, InterruptedException {
        register(new SimpleEndpoint(serviceName, endpoint), persistent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(QName serviceName, String endpoint, SLProperties properties)
            throws ServiceLocatorException, InterruptedException {
        register(new SimpleEndpoint(serviceName, endpoint, properties), false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(QName serviceName, String endpoint, SLProperties properties, boolean persistent)
            throws ServiceLocatorException, InterruptedException {
        register(new SimpleEndpoint(serviceName, endpoint, properties), persistent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void register(Endpoint epProvider) throws ServiceLocatorException,
            InterruptedException {
        register(epProvider, false);
    }

    @Override
    public synchronized void register(Endpoint epProvider, boolean persistent)
            throws ServiceLocatorException, InterruptedException {

        QName serviceName = epProvider.getServiceName();
        String endpoint = epProvider.getAddress();

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Registering endpoint " + endpoint + " for service " + serviceName + "...");
        }

        long lastTimeStarted = System.currentTimeMillis();
        long lastTimeStopped = -1;

        RootNode rootNode = getBackend().connect();
        ServiceNode serviceNode = rootNode.getServiceNode(serviceName);
        serviceNode.ensureExists();

        EndpointNode endpointNode = serviceNode.getEndPoint(endpoint);
        if (endpointNode.exists()) {
            byte[] content = endpointNode.getContent();
            SLEndpoint oldEndpoint = transformer.toSLEndpoint(serviceName, content, false);
            lastTimeStopped = oldEndpoint.getLastTimeStopped();
        }

        byte[] content = createContent(epProvider, lastTimeStarted, lastTimeStopped);

        endpointNode.ensureExists(content);
        endpointNode.setLive(persistent);
    }

    @Override
    public synchronized void unregister(Endpoint epProvider) throws ServiceLocatorException,
            InterruptedException {

        QName serviceName = epProvider.getServiceName();
        String endpoint = epProvider.getAddress();

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Unregistering endpoint " + endpoint + " for service " + serviceName + "...");
        }

        long lastTimeStarted = -1;
        long lastTimeStopped = System.currentTimeMillis();

        RootNode rootNode = getBackend().connect();
        ServiceNode serviceNode = rootNode.getServiceNode(serviceName);
        EndpointNode endpointNode = serviceNode.getEndPoint(endpoint);

        if (endpointNode.exists()) {

            byte[] oldContent = endpointNode.getContent();
            SLEndpoint oldEndpoint = transformer.toSLEndpoint(serviceName, oldContent, false);
            lastTimeStarted = oldEndpoint.getLastTimeStarted();

            endpointNode.setOffline();

            byte[] content = createContent(epProvider, lastTimeStarted, lastTimeStopped);
            endpointNode.setContent(content);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void unregister(QName serviceName, String endpoint) throws ServiceLocatorException,
            InterruptedException {
        unregister(new SimpleEndpoint(serviceName, endpoint, null));
    }

    @Override
    public void updateTimetolive(QName serviceName, String endpoint, int timetolive)
            throws ServiceLocatorException, InterruptedException {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Updating expiring time to happen in " + timetolive + " seconds on endpoint " + endpoint
                    + " for service " + serviceName + "...");
        }

        if (timetolive < 0) {
            throw new WrongArgumentException("Time-to-live cannot be negative.");
        }

        if (timetolive == 0) {
            throw new WrongArgumentException("Time-to-live cannot be zero.");
        }

        RootNode rootNode = getBackend().connect();
        ServiceNode serviceNode = rootNode.getServiceNode(serviceName);
        EndpointNode endpointNode = serviceNode.getEndPoint(endpoint);

        if (endpointNode.exists()) {
            endpointNode.setLive(true);
            endpointNode.setExpiryTime(new Date(System.currentTimeMillis() + timetolive * 1000), true);
        } else {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Unable to update endpoint expiring time for endpoint " + endpoint + " for service "
                        + serviceName + " because it does not exist.");
            }
            throw new EndpointNotFoundException("Endpoint " + endpoint + " for service " + serviceName
                    + " does not exist.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void removeEndpoint(QName serviceName, String endpoint)
            throws ServiceLocatorException, InterruptedException {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Removing endpoint " + endpoint + " for service " + serviceName + "...");
        }

        RootNode rootNode = getBackend().connect();
        ServiceNode serviceNode = rootNode.getServiceNode(serviceName);

        EndpointNode endpointNode = serviceNode.getEndPoint(endpoint);
        endpointNode.ensureRemoved();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<QName> getServices() throws InterruptedException, ServiceLocatorException {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Getting all services...");
        }
        RootNode rootNode = getBackend().connect();

        return rootNode.getServiceNames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized public List<SLEndpoint> getEndpoints(final QName serviceName)
            throws ServiceLocatorException, InterruptedException {

        RootNode rootNode = getBackend().connect();
        ServiceNode serviceNode = rootNode.getServiceNode(serviceName);

        if (serviceNode.exists()) {
            List<EndpointNode> endpointNodes = serviceNode.getEndPoints();

            List<SLEndpoint> slEndpoints = new ArrayList<SLEndpoint>(endpointNodes.size());
            for (EndpointNode endpointNode : endpointNodes) {
                byte[] content = endpointNode.getContent();
                final boolean isLive = endpointNode.isLive();
                SLEndpoint slEndpoint = transformer.toSLEndpoint(serviceName, content, isLive);
                slEndpoints.add(slEndpoint);
            }
            return slEndpoints;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SLEndpoint getEndpoint(final QName serviceName, final String endpoint)
            throws ServiceLocatorException, InterruptedException {

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Get endpoint information for endpoint " + endpoint + " within service " + serviceName
                    + "...");
        }

        RootNode rootNode = getBackend().connect();
        ServiceNode serviceNode = rootNode.getServiceNode(serviceName);
        EndpointNode endpointNode = serviceNode.getEndPoint(endpoint);
        if (endpointNode.exists()) {
            byte[] content = endpointNode.getContent();
            final boolean isLive = endpointNode.isLive();

            return transformer.toSLEndpoint(serviceName, content, isLive);
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<String> getEndpointNames(QName serviceName) throws ServiceLocatorException,
            InterruptedException {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Get all endpoint names of service " + serviceName + "...");
        }
        List<String> children;

        RootNode rootNode = getBackend().connect();
        ServiceNode serviceNode = rootNode.getServiceNode(serviceName);
        if (serviceNode.exists()) {
            children = serviceNode.getEndpointNames();
        } else {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Lookup of service " + serviceName + " failed, service is not known.");
            }
            children = Collections.emptyList();
        }
        return children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> lookup(QName serviceName) throws ServiceLocatorException, InterruptedException {
        return lookup(serviceName, SLPropertiesMatcher.ALL_MATCHER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<String> lookup(QName serviceName, SLPropertiesMatcher matcher)
            throws ServiceLocatorException, InterruptedException {

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Looking up endpoints of service " + serviceName + "...");
        }

        List<String> liveEndpoints;

        RootNode rootNode = getBackend().connect();
        ServiceNode serviceNode = rootNode.getServiceNode(serviceName);
        if (serviceNode.exists()) {
            liveEndpoints = new ArrayList<String>();
            List<EndpointNode> endpointNodes = serviceNode.getEndPoints();

            for (EndpointNode endpointNode : endpointNodes) {

                if (endpointNode.isLive()) {
                    byte[] content = endpointNode.getContent();
                    SLEndpoint endpoint = transformer.toSLEndpoint(serviceName, content, true);
                    SLProperties props = endpoint.getProperties();

                    if (LOG.isLoggable(Level.FINE)) {
                        StringBuilder sb = new StringBuilder();
                        for (String prop : props.getPropertyNames()) {
                            sb.append(prop + " : ");
                            for (String value : props.getValues(prop)) {
                                sb.append(value + " ");
                            }
                            sb.append("\n");
                        }
                        LOG.fine("Lookup of service " + serviceName + " props = " + sb.toString());
                        LOG.fine("matcher = " + matcher.toString());
                    }
                    if (matcher.isMatching(props)) {
                        liveEndpoints.add(endpointNode.getEndpointName());
                        if (LOG.isLoggable(Level.FINE))
                            LOG.fine("matched =  " + endpointNode.getEndpointName());
                    } else if (LOG.isLoggable(Level.FINE))
                        LOG.fine("not matched =  " + endpointNode.getEndpointName());

                }
            }
        } else {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Lookup of service " + serviceName + " failed, service is not known.");
            }
            liveEndpoints = Collections.emptyList();
        }
        return liveEndpoints;
    }

    /**
     * Specify the endpoints of all the instances belonging to the service
     * locator ensemble this object might potentially be talking to when
     * {@link #connect() connecting}. The object will one by one pick an
     * endpoint (the order is non-deterministic) to connect to the service
     * locator until a connection is established.
     *
     * @param endpoints comma separated list of endpoints,each corresponding to a
     *                  service locator instance. Each endpoint is specified as a
     *                  host:port pair. At least one endpoint must be specified. Valid
     *                  exmaples are: "127.0.0.1:2181" or
     *                  "sl1.example.com:3210, sl2.example.com:3210, sl3.example.com:3210"
     */

    public void setLocatorEndpoints(String endpoints) {
        ((ZKBackend) getBackend()).setLocatorEndpoints(endpoints);
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Locator endpoints set to " + endpoints);
        }
    }

    /**
     * Specify the time out of the session established at the server. The
     * session is kept alive by requests sent by this client object. If the
     * session is idle for a period of time that would timeout the session, the
     * client will send a PING request to keep the session alive.
     *
     * @param timeout timeout in milliseconds, must be greater than zero and less
     *                       than 60000.
     */

    public void setSessionTimeout(int timeout) {
        ((ZKBackend) getBackend()).setSessionTimeout(timeout);

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Locator session timeout set to: " + timeout);
        }
    }

    /**
     * Specify the time this client waits {@link #connect() for a connection to
     * get established}.
     *
     * @param timeout timeout in milliseconds, must be greater than zero
     */

    public void setConnectionTimeout(int timeout) {
        ((ZKBackend) getBackend()).setConnectionTimeout(timeout);

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Locator connection timeout set to: " + timeout);
        }
    }

    public void setName(String name) {
        ((ZKBackend) getBackend()).setUserName(name);
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("User name set to: " + name);
        }
    }

    public void setPassword(String passWord) {
        ((ZKBackend) getBackend()).setPassword(passWord);
    }

    public void setEndpointTransformer(EndpointTransformer endpointTransformer) {
        transformer = endpointTransformer;
    }

    @Value("${locator.endpoints.timetolive.check}")
    public void setEndpointCollectionEnable(Boolean endpointCollectionDisable) {
        this.endpointCollectionEnable = endpointCollectionDisable;
    }

    @Value("${locator.endpoints.timetolive.interval}")
    public void setEndpointCollectionInterval(Integer endpointCollectionInterval) {
        this.endpointCollectionInterval = endpointCollectionInterval;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPostConnectAction(PostConnectAction postConnectAction) {
        backend.addPostConnectAction(postConnectAction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePostConnectAction(PostConnectAction postConnectAction) {
        backend.removePostConnectAction(postConnectAction);
    }

    private byte[] createContent(Endpoint eprProvider, long lastTimeStarted, long lastTimeStopped)
            throws ServiceLocatorException {
        return transformer.fromEndpoint(eprProvider, lastTimeStarted, lastTimeStopped);
    }

    private ServiceLocatorBackend getBackend() {
        if (backend == null) {
            backend = new ZKBackend();
        }
        return backend;
    }

    public void setBackend(ServiceLocatorBackend backend) {
        this.backend = backend;
    }

    protected ZooKeeper createZooKeeper(CountDownLatch connectionLatch) throws ServiceLocatorException {
        /*
         * try { return new ZooKeeper(locatorEndpoints, sessionTimeout, new
         * WatcherImpl(connectionLatch)); } catch (IOException e) { throw new
         * ServiceLocatorException("At least one of the endpoints " +
         * locatorEndpoints + " does not represent a valid address."); }
         */
        return null;
    }

    @Override
    public synchronized void startScheduledCollection() {
        if (endpointCollectionEnable != null && !endpointCollectionEnable) {
            LOG.info("Expired endpoint collection is disabled in configuration.");
            return;
        }

        if (endpointCollectionInterval == null) {
            LOG.severe("Expired endpoint collection interval is not set.");
            return;
        }

        Long collectionInterval = endpointCollectionInterval * 1000L;

        if (collectionInterval < 5000L) {
            LOG.severe("Expired endpoint collection interval has invalid value '" + collectionInterval
                    + "'. " + "It should be >= 5000.");
            return;
        }

        schedulerRequestCounter++;

        if (timer != null) {
            return;
        }

        if (schedulerRequestCounter != 1) {
            LOG.warning("Expired endpoint collector schedule is inconsistent.");
        }

        timer = new Timer("Expired-Endpoint-Collector-Timer", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                performCollection();
            }
        }, collectionInterval, collectionInterval);
    }

    @Override
    public synchronized void stopScheduledCollection() {
        if (timer == null) {
            return;
        }
        schedulerRequestCounter--;
        if (schedulerRequestCounter <= 0) {
            timer.cancel();
            timer = null;
            schedulerRequestCounter = 0;
        }
    }

    @Override
    public synchronized void performCollection() {
        LOG.fine("Performing expired endpoint collection.");

        Date now = new Date();

        try {
            RootNode root = getBackend().connect();
            List<QName> svcs = root.getServiceNames();

            for (QName svc : svcs) {
                ServiceNode svcNode = root.getServiceNode(svc);
                List<EndpointNode> epts = svcNode.getEndPoints();

                for (EndpointNode ept : epts) {
                    Date expTime = ept.getExpiryTime();
                    if (expTime != null && expTime.before(now)) {
                        unregisterEndpoint(svc, ept.getEndpointName());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void unregisterEndpoint(QName serviceName, String endpointName) {
        try {
            unregister(serviceName, endpointName);
        } catch (Exception e) {
            if (e instanceof ServiceLocatorException || e instanceof InterruptedException) {
                LOG.warning("Exception during unregistering expired endpoint: " + e);
            } else {
                throw new RuntimeException("Unexpected exception during unregistering expired endpoint.", e);
            }
        }
    }
}
