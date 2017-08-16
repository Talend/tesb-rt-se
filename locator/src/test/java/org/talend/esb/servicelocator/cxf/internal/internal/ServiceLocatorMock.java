package org.talend.esb.servicelocator.cxf.internal.internal;


import org.talend.esb.servicelocator.client.*;
import org.talend.esb.servicelocator.client.internal.SLEndpointProvider;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import java.util.List;

public class ServiceLocatorMock implements ServiceLocator {

    Endpoint ep = null;
    private SLEndpoint slEndpoint = null;

    @Override
    public void connect() throws InterruptedException, ServiceLocatorException {

    }

    @Override
    public void disconnect() throws InterruptedException, ServiceLocatorException {

    }

    @Override
    public void register(QName serviceName, String endpoint) throws ServiceLocatorException, InterruptedException {

    }

    @Override
    public void register(QName serviceName, String endpoint, boolean persistent) throws ServiceLocatorException, InterruptedException {

    }

    @Override
    public void register(QName serviceName, String endpoint, SLProperties properties) throws ServiceLocatorException, InterruptedException {

    }

    @Override
    public void register(QName serviceName, String endpoint, SLProperties properties, boolean persistent) throws ServiceLocatorException, InterruptedException {

    }

    @Override
    public void register(Endpoint eprProvider) throws ServiceLocatorException, InterruptedException {
        ep = eprProvider;

    }

    @Override
    public void register(Endpoint eprProvider, boolean persistent) throws ServiceLocatorException, InterruptedException {

    }

    @Override
    public void unregister(QName serviceName, String endpoint) throws ServiceLocatorException, InterruptedException {

    }

    @Override
    public void unregister(Endpoint epProvider) throws ServiceLocatorException, InterruptedException {

    }

    @Override
    public void updateTimetolive(QName serviceName, String endpoint, int timetolive) throws ServiceLocatorException, InterruptedException {

    }

    @Override
    public void removeEndpoint(QName serviceName, String endpoint) throws ServiceLocatorException, InterruptedException {

    }

    @Override
    public List<QName> getServices() throws InterruptedException, ServiceLocatorException {
        return null;
    }

    @Override
    public SLEndpoint getEndpoint(QName serviceName, String endpoint) throws ServiceLocatorException, InterruptedException {

        return new SLEndpoint() {
            @Override
            public long getLastTimeStarted() {
                return 0;
            }

            @Override
            public long getLastTimeStopped() {
                return 0;
            }

            @Override
            public boolean isLive() {
                return false;
            }

            @Override
            public QName forService() {
                return null;
            }

            @Override
            public QName getServiceName() {
                return null;
            }

            @Override
            public String getAddress() {
                return null;
            }

            @Override
            public BindingType getBinding() {
                return null;
            }

            @Override
            public TransportType getTransport() {
                return null;
            }

            @Override
            public SLProperties getProperties() {
                return ep.getProperties();
            }

            @Override
            public void writeEndpointReferenceTo(Result result, PropertiesTransformer transformer) throws ServiceLocatorException {

            }

            @Override
            public void addEndpointReference(Node parent) throws ServiceLocatorException {

            }
        };
    }

    @Override
    public List<SLEndpoint> getEndpoints(QName serviceName) throws ServiceLocatorException, InterruptedException {
        return null;
    }

    @Override
    public List<String> getEndpointNames(QName serviceName) throws ServiceLocatorException, InterruptedException {
        return null;
    }

    @Override
    public List<String> lookup(QName serviceName) throws ServiceLocatorException, InterruptedException {
        return null;
    }

    @Override
    public List<String> lookup(QName serviceName, SLPropertiesMatcher matcher) throws ServiceLocatorException, InterruptedException {
        return null;
    }

    @Override
    public void addPostConnectAction(PostConnectAction postConnectAction) {

    }

    @Override
    public void removePostConnectAction(PostConnectAction postConnectAction) {

    }
}
