/*
 * #%L
 * Locator Service :: REST
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
package org.talend.esb.locator.service.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.ops4j.pax.cdi.api.OsgiService;
import org.talend.esb.servicelocator.client.BindingType;
import org.talend.esb.servicelocator.client.Endpoint;
import org.talend.esb.servicelocator.client.EndpointNotFoundException;
import org.talend.esb.servicelocator.client.ExpiredEndpointCollector;
import org.talend.esb.servicelocator.client.SLEndpoint;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.SLPropertiesImpl;
import org.talend.esb.servicelocator.client.SLPropertiesMatcher;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.SimpleEndpoint;
import org.talend.esb.servicelocator.client.TransportType;
import org.talend.esb.servicelocator.client.WrongArgumentException;
import org.talend.esb.servicelocator.client.internal.EndpointTransformerImpl;
import org.talend.esb.servicelocator.client.internal.ServiceLocatorImpl;
import org.talend.schemas.esb.locator.rest._2011._11.EndpointReferenceList;
import org.talend.schemas.esb.locator.rest._2011._11.EntryType;
import org.talend.schemas.esb.locator.rest._2011._11.RegisterEndpointRequest;
import org.talend.services.esb.locator.rest.v1.LocatorService;
import org.w3c.dom.Document;

@Singleton
@Named("serviceLocatorBean")
public class LocatorRestServiceImpl implements LocatorService {

    private static final Logger LOG = Logger
            .getLogger(LocatorRestServiceImpl.class.getPackage().getName());

    private static final Random RANDOM = new Random();

    @OsgiService
    @Inject
    ServiceLocator locatorClient;

    @OsgiService
    @Inject
    ExpiredEndpointCollector endpointCollector;

    private String locatorEndpoints = "localhost:2181";

    private int sessionTimeout = 5000;

    private int connectionTimeout = 5000;

    public void setLocatorClient(ServiceLocator locatorClient) {
        this.locatorClient = locatorClient;
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Locator client was set for Rest Service.");
        }
    }

    public void setEndpointCollector(ExpiredEndpointCollector endpointCollector) {
        this.endpointCollector = endpointCollector;
    }

    public void setLocatorEndpoints(String locatorEndpoints) {
        this.locatorEndpoints = locatorEndpoints;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    @PostConstruct
    public void start() {
        if (endpointCollector != null) {
            endpointCollector.startScheduledCollection();
        }
    }

    /**
     * Instantiate Service Locator client. After successful instantiation
     * establish a connection to the Service Locator server. This method will be
     * called if property locatorClient is null. For this purpose was defined
     * additional properties to instantiate ServiceLocatorImpl.
     *
     * @throws InterruptedException
     * @throws ServiceLocatorException
     */
    public void initLocator() throws InterruptedException,
            ServiceLocatorException {
        if (locatorClient == null) {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Instantiate locatorClient client for Locator Server "
                        + locatorEndpoints + "...");
            }
            ServiceLocatorImpl client = new ServiceLocatorImpl();
            client.setLocatorEndpoints(locatorEndpoints);
            client.setConnectionTimeout(connectionTimeout);
            client.setSessionTimeout(sessionTimeout);
            locatorClient = client;
            locatorClient.connect();
        }
    }

    /**
     * Should use as destroy method. Disconnects from a Service Locator server.
     * All endpoints that were registered before are removed from the server.
     * Set property locatorClient to null.
     *
     * @throws InterruptedException
     * @throws ServiceLocatorException
     */
    @PreDestroy
    public void disconnectLocator() throws InterruptedException,
            ServiceLocatorException {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Destroy Locator client");
        }

        if (endpointCollector != null) {
            endpointCollector.stopScheduledCollection();
        }

        if (locatorClient != null) {
            locatorClient.disconnect();
            locatorClient = null;
        }
    }

    /**
     * Register the endpoint for given service.
     *
     * @param input
     *            RegisterEndpointRequestType encapsulate name of service and
     *            endpointURL. Must not be <code>null</code>
     */
    public void registerEndpoint(RegisterEndpointRequest arg0) {
        String endpointURL = arg0.getEndpointURL();
        QName serviceName = QName.valueOf(arg0.getServiceName());
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Registering endpoint " + endpointURL + " for service "
                    + serviceName + "...");
        }
        try {
            initLocator();
            BindingType bindingType = arg0.getBinding() == null ? BindingType.OTHER : BindingType
                    .valueOf(arg0.getBinding().value());
            TransportType transportType = arg0.getTransport() == null ? TransportType.OTHER
                    : TransportType.valueOf(arg0.getTransport().value());
            SLPropertiesImpl slProps = null;
            if (!arg0.getEntryType().isEmpty()) {
                slProps = new SLPropertiesImpl();
                List<EntryType> entries = arg0.getEntryType();
                for (EntryType entry : entries) {
                    slProps.addProperty(entry.getKey(), entry.getValue());
                }
            }
            Endpoint simpleEndpoint =
                new SimpleEndpoint(serviceName, endpointURL, bindingType, transportType, slProps);
            locatorClient.register(simpleEndpoint, true);
        } catch (ServiceLocatorException e) {
            // throw new ServiceLocatorFault(e.getMessage(), e);
            throw new WebApplicationException(Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build());
        } catch (InterruptedException e) {
            // throw new InterruptedExceptionFault(e.getMessage(), e);
            throw new WebApplicationException(Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build());
        }
    }

    public void registerEndpointRegisterEndpointRequestXml(RegisterEndpointRequest registerendpointrequest) {
        registerEndpoint(registerendpointrequest);
    }

    public void registerEndpointRegisterEndpointRequestJson(RegisterEndpointRequest registerendpointrequest) {
        registerEndpoint(registerendpointrequest);
    }

    /**
     * Unregister the endpoint for given service.
     *
     * @param input
     *            String encoded name of service
     * @param input
     *            String encoded name of endpoint
     *
     */
    public void unregisterEndpoint(String arg0, String arg1) {
        String endpointURL = null;
        QName serviceName = null;
        try {
            serviceName = QName.valueOf(URLDecoder.decode(arg0, "UTF-8"));
            endpointURL = URLDecoder.decode(arg1, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            throw new WebApplicationException(Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity(e1.getMessage()).build());
        }
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Unregistering endpoint " + endpointURL + " for service "
                    + serviceName + "...");
        }
        try {
            initLocator();
            locatorClient.unregister(serviceName, endpointURL);
        } catch (ServiceLocatorException e) {
            throw new WebApplicationException(Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build());
        } catch (InterruptedException e) {
            throw new WebApplicationException(Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build());
        }
    }

    @Override
    public void updateTimetolive(String arg0, String arg1, int timetolive) {
        String endpointURL = null;
        QName serviceName = null;
        try {
            serviceName = QName.valueOf(URLDecoder.decode(arg0, "UTF-8"));
            endpointURL = URLDecoder.decode(arg1, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(e1.getMessage()).build());
        }
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Updating expiring time to happen in " + timetolive + " seconds on endpoint " + endpointURL
                    + " for service " + serviceName + "...");
        }
        try {
            initLocator();
            locatorClient.updateTimetolive(serviceName, endpointURL, timetolive);
        } catch (ServiceLocatorException e) {
            if (e instanceof EndpointNotFoundException) {
                throw new WebApplicationException(Response.status(Status.NOT_FOUND)
                        .entity(e.getMessage()).build());
            }
            if (e instanceof WrongArgumentException) {
                throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
                        .entity(e.getMessage()).build());
            }
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build());
        } catch (InterruptedException e) {
            throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build());
        }
    }

    /**
     * For the given service return endpoint reference randomly selected from
     * list of endpoints currently registered at the service locator server.
     *
     * @param input
     *            String encoded name of service
     * @param input
     *            List of encoded additional parameters separated by comma
     *
     * @return endpoint references or <code>null</code>
     */
    @Override
    public W3CEndpointReference lookupEndpoint(String arg0, List<String> arg1) {
        QName serviceName = null;
        try {
            serviceName = QName.valueOf(URLDecoder.decode(arg0, "UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            throw new WebApplicationException(Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error during decoding serviceName").build());
        }
        List<String> names = null;
        String adress = null;
        SLPropertiesMatcher matcher = null;
        try {
            matcher = createMatcher(arg1);
        } catch (UnsupportedEncodingException e1) {
            throw new WebApplicationException(Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Error during decoding serviceName").build());
        }
        try {
            initLocator();
            if (matcher == null) {
                names = locatorClient.lookup(serviceName);
            } else {
                names = locatorClient.lookup(serviceName, matcher);
            }
        } catch (ServiceLocatorException e) {
            throw new WebApplicationException(Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build());
        } catch (InterruptedException e) {
            throw new WebApplicationException(Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build());
        }
        if (names != null && !names.isEmpty()) {
            names = getRotatedList(names);
            adress = names.get(0);
        } else {
            String msg = "lookup Endpoint for " + removeCRLF(serviceName.toString()) + " failed, service is not known.";
            if (LOG.isLoggable(Level.WARNING)) {
                LOG.log(Level.WARNING, msg);
            }
            throw new WebApplicationException(Response
                    .status(Status.NOT_FOUND)
                    .entity(msg).build());
        }
        return buildEndpoint(serviceName, adress);
    }

    /**
     * For the given service return endpoint reference randomly selected from
     * list of endpoints currently registered at the service locator server.
     *
     * @param input
     *            String encoded name of service
     * @param input
     *            List of encoded additional parameters separated by comma
     *
     * @return EndpointReferenceListType encapsulate list of endpoint references
     *         or <code>null</code>
     */
    public EndpointReferenceList lookupEndpoints(String arg0, List<String> arg1) {
        QName serviceName = null;
        try {
            serviceName = QName.valueOf(URLDecoder.decode(arg0, "UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            throw new WebApplicationException(Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity(e1.getMessage()).build());
        }
        List<String> names = null;
        String adress = null;
        SLPropertiesMatcher matcher = null;
        try {
            matcher = createMatcher(arg1);
        } catch (UnsupportedEncodingException e1) {
            throw new WebApplicationException(Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity(e1.getMessage()).build());
        }
        EndpointReferenceList refs = new EndpointReferenceList();
        try {
            initLocator();
            if (matcher == null) {
                names = locatorClient.lookup(serviceName);
            } else {
                names = locatorClient.lookup(serviceName, matcher);
            }
        } catch (ServiceLocatorException e) {
            throw new WebApplicationException(Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build());
        } catch (InterruptedException e) {
            throw new WebApplicationException(Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build());
        }
        if (names != null && !names.isEmpty()) {
            for (int i = 0; i < names.size(); i++) {
                adress = names.get(i);
                refs.getEndpointReference().add(buildEndpoint(serviceName, adress));
            }
        } else {
            if (LOG.isLoggable(Level.WARNING)) {
                LOG.log(Level.WARNING, "lookup Endpoints for " + removeCRLF(serviceName.toString())
                        + " failed, service is not known.");
            }
            throw new WebApplicationException(Response.status(Status.NOT_FOUND)
                    .entity("Service not found").build());
        }
        return refs;
    }

    private SLPropertiesMatcher createMatcher(List<String> input)
        throws UnsupportedEncodingException {
        SLPropertiesMatcher matcher = null;
        if (input != null && input.size() > 0) {
            matcher = new SLPropertiesMatcher();
            for (String pair : input) {
                String[] assertion = URLDecoder.decode(pair, "UTF-8")
                        .split(",");
                if (assertion.length == 2) {
                    matcher.addAssertion(assertion[0], assertion[1]);
                }
            }
        }
        return matcher;
    }

    /**
     * Rotate list of String. Used for randomize selection of received endpoints
     *
     * @param strings
     *            list of Strings
     * @return the same list in random order
     */
    private List<String> getRotatedList(List<String> strings) {
        int index = RANDOM.nextInt(strings.size());
        List<String> rotated = new ArrayList<String>(strings.size());
        for (int i = 0; i < strings.size(); i++) {
            rotated.add(strings.get(index));
            index = (index + 1) % strings.size();
        }
        return rotated;
    }

    /**
     * Build Endpoint Reference for giving service name and address
     *
     * @param serviceName
     * @param adress
     * @return
     */
    private W3CEndpointReference buildEndpoint(QName serviceName, String adress) {
        W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        // builder.serviceName(serviceName);
        builder.address(adress);
        SLEndpoint endpoint = null;
        try {
            endpoint = locatorClient.getEndpoint(serviceName, adress);
        } catch (ServiceLocatorException e) {
            throw new WebApplicationException(Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build());
        } catch (InterruptedException e) {
            throw new WebApplicationException(Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage()).build());
        }
        if (endpoint != null) {
            SLProperties properties = endpoint.getProperties();
            if (properties != null && !properties.getPropertyNames().isEmpty()) {
                EndpointTransformerImpl transformer = new EndpointTransformerImpl();

                DOMResult result = new DOMResult();
                transformer.writePropertiesTo(properties, result);
                Document docResult = (Document) result.getNode();

                builder.metadata(docResult.getDocumentElement());
            }
        }
        return builder.build();
    }

    private static String removeCRLF(String s) {
        return s.replaceAll("\r|\n", "");
    }
}
