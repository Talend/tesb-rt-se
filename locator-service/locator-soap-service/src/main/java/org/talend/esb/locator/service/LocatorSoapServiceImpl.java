/*
 * #%L
 * Locator Service :: SOAP
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
package org.talend.esb.locator.service;

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
import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.ops4j.pax.cdi.api.OsgiService;
import org.talend.esb.servicelocator.client.BindingType;
import org.talend.esb.servicelocator.client.ExpiredEndpointCollector;
import org.talend.esb.servicelocator.client.SLEndpoint;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.SLPropertiesImpl;
import org.talend.esb.servicelocator.client.SLPropertiesMatcher;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.SimpleEndpoint;
import org.talend.esb.servicelocator.client.TransportType;
import org.talend.esb.servicelocator.client.internal.EndpointTransformerImpl;
import org.talend.esb.servicelocator.client.internal.ServiceLocatorImpl;
import org.talend.schemas.esb.locator._2011._11.AssertionType;
import org.talend.schemas.esb.locator._2011._11.EntryType;
import org.talend.schemas.esb.locator._2011._11.InterruptionFaultDetail;
import org.talend.schemas.esb.locator._2011._11.LookupEndpointResponse;
import org.talend.schemas.esb.locator._2011._11.LookupEndpointsResponse;
import org.talend.schemas.esb.locator._2011._11.LookupRequestType;
import org.talend.schemas.esb.locator._2011._11.MatcherDataType;
import org.talend.schemas.esb.locator._2011._11.SLPropertiesType;
import org.talend.schemas.esb.locator._2011._11.ServiceLocatorFaultDetail;
import org.talend.services.esb.locator.v1.InterruptedExceptionFault;
import org.talend.services.esb.locator.v1.LocatorService;
import org.talend.services.esb.locator.v1.ServiceLocatorFault;
import org.w3c.dom.Document;

@Singleton
@Named("serviceLocatorBean")
public class LocatorSoapServiceImpl implements LocatorService {

    private static final Logger LOG = Logger
            .getLogger(LocatorSoapServiceImpl.class.getPackage().getName());

    private static final Random RANDOM = new Random();

    @OsgiService
    @Inject
    private ServiceLocator locatorClient;

    @OsgiService
    @Inject
    private ExpiredEndpointCollector endpointCollector;

    private String locatorEndpoints = "localhost:2181";

    private int sessionTimeout = 5000;

    private int connectionTimeout = 5000;

    private String authenticationName;

    private String authenticationPassword;

    public void setLocatorClient(ServiceLocator locatorClient) {
        this.locatorClient = locatorClient;
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Locator client was set for Soap service.");
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
            if (null != authenticationName)
                client.setName(authenticationName);
            if (null != authenticationPassword)
                client.setPassword(authenticationPassword);
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
    @Override
    public void registerEndpoint(QName serviceName, String endpointURL,
            org.talend.schemas.esb.locator._2011._11.BindingType binding,
            org.talend.schemas.esb.locator._2011._11.TransportType transport,
            SLPropertiesType properties) throws ServiceLocatorFault,
            InterruptedExceptionFault {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Registering endpoint " + endpointURL + " for service "
                    + serviceName + "...");
        }
        try {
            initLocator();
            BindingType bindingType = binding == null ? BindingType.SOAP11
                    : BindingType.valueOf(binding.value());
            TransportType transportType = transport == null ? TransportType.HTTP
                    : TransportType.valueOf(transport.value());
            SLPropertiesImpl slProps = null;
            if (properties != null) {
                slProps = new SLPropertiesImpl();
                List<EntryType> entries = properties.getEntry();
                for (EntryType entry : entries) {
                    slProps.addProperty(entry.getKey(), entry.getValue());
                }
            }
            SimpleEndpoint eprProvider = new SimpleEndpoint(serviceName,
                    endpointURL, bindingType, transportType, slProps);

            locatorClient.register(eprProvider, true);
        } catch (ServiceLocatorException e) {
            ServiceLocatorFaultDetail serviceFaultDetail = new ServiceLocatorFaultDetail();
            serviceFaultDetail.setLocatorFaultDetail(serviceName.toString()
                    + "throws ServiceLocatorFault");
            throw new ServiceLocatorFault(e.getMessage(), serviceFaultDetail);
        } catch (InterruptedException e) {
            InterruptionFaultDetail interruptionFaultDetail = new InterruptionFaultDetail();
            interruptionFaultDetail.setInterruptionDetail(serviceName
                    .toString() + "throws InterruptionFault");
            throw new InterruptedExceptionFault(e.getMessage(),
                    interruptionFaultDetail);
        }
    }

    /**
     * Unregister the endpoint for given service.
     *
     * @param input
     *            UnregisterEndpointRequestType encapsulate name of service and
     *            endpointURL. Must not be <code>null</code>
     */
    @Override
    public void unregisterEndpoint(QName serviceName, String endpointURL)
            throws ServiceLocatorFault, InterruptedExceptionFault {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Unregistering endpoint " + endpointURL + " for service "
                    + serviceName + "...");
        }
        try {
            initLocator();
            locatorClient.unregister(serviceName, endpointURL);
        } catch (ServiceLocatorException e) {
            ServiceLocatorFaultDetail serviceFaultDetail = new ServiceLocatorFaultDetail();
            serviceFaultDetail.setLocatorFaultDetail(serviceName.toString()
                    + "throws ServiceLocatorFault");
            throw new ServiceLocatorFault(e.getMessage(), serviceFaultDetail);
        } catch (InterruptedException e) {
            InterruptionFaultDetail interruptionFaultDetail = new InterruptionFaultDetail();
            interruptionFaultDetail.setInterruptionDetail(serviceName
                    .toString() + "throws InterruptionFault");
            throw new InterruptedExceptionFault(e.getMessage(),
                    interruptionFaultDetail);
        }
    }

    /**
     * @see ServiceLocator
     */
    @Override
    public void updateTimetolive(QName serviceName, String endpointURL,
            int timetolive) throws ServiceLocatorFault, InterruptedExceptionFault {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Updating expiring time to happen in " + timetolive + " seconds on endpoint " + endpointURL
                    + " for service " + serviceName + "...");
        }
        try {
            initLocator();
            locatorClient.updateTimetolive(serviceName, endpointURL, timetolive);
        } catch (ServiceLocatorException e) {
            ServiceLocatorFaultDetail serviceFaultDetail = new ServiceLocatorFaultDetail();
            serviceFaultDetail.setLocatorFaultDetail(serviceName.toString() + "throws ServiceLocatorFault");
            throw new ServiceLocatorFault(e.getMessage(), serviceFaultDetail);
        } catch (InterruptedException e) {
            InterruptionFaultDetail interruptionFaultDetail = new InterruptionFaultDetail();
            interruptionFaultDetail
                    .setInterruptionDetail(serviceName.toString() + "throws InterruptionFault");
            throw new InterruptedExceptionFault(e.getMessage(), interruptionFaultDetail);
        }
    }

    @Override
    public LookupEndpointResponse lookupEndpoint(LookupRequestType parameters)
            throws ServiceLocatorFault, InterruptedExceptionFault {

        W3CEndpointReference epr = lookupEndpoint(parameters.getServiceName(),
                parameters.getMatcherData());
        LookupEndpointResponse response = new LookupEndpointResponse();
        response.setEndpointReference(epr);
        return response;
    }

    /**
     * For the given service return endpoint reference randomly selected from
     * list of endpoints currently registered at the service locator server.
     *
     * @param serviceName
     *            the name of the service for which to get the endpoints, must
     *            not be <code>null</code>
     * @return endpoint references or <code>null</code>
     */
    W3CEndpointReference lookupEndpoint(QName serviceName,
            MatcherDataType matcherData) throws ServiceLocatorFault,
            InterruptedExceptionFault {
        List<String> names = null;
        String adress;
        try {
            initLocator();
            SLPropertiesMatcher matcher = createMatcher(matcherData);
            if (matcher == null) {
                names = locatorClient.lookup(serviceName);
            } else {
                names = locatorClient.lookup(serviceName, matcher);
            }
        } catch (ServiceLocatorException e) {
            ServiceLocatorFaultDetail serviceFaultDetail = new ServiceLocatorFaultDetail();
            serviceFaultDetail.setLocatorFaultDetail(serviceName.toString()
                    + "throws ServiceLocatorFault");
            throw new ServiceLocatorFault(e.getMessage(), serviceFaultDetail);
        } catch (InterruptedException e) {
            InterruptionFaultDetail interruptionFaultDetail = new InterruptionFaultDetail();
            interruptionFaultDetail.setInterruptionDetail(serviceName
                    .toString() + "throws InterruptionFault");
            throw new InterruptedExceptionFault(e.getMessage(),
                    interruptionFaultDetail);
        }
        if (names != null && !names.isEmpty()) {
            names = getRotatedList(names);
            adress = names.get(0);
        } else {
            if (LOG.isLoggable(Level.WARNING)) {
                LOG.log(Level.WARNING, "lookup Endpoint for " + serviceName
                        + " failed, service is not known.");
            }
            ServiceLocatorFaultDetail serviceFaultDetail = new ServiceLocatorFaultDetail();
            serviceFaultDetail.setLocatorFaultDetail("lookup Endpoint for "
                    + serviceName + " failed, service is not known.");
            throw new ServiceLocatorFault("Can not find Endpoint",
                    serviceFaultDetail);
        }
        return buildEndpoint(serviceName, adress);
    }

    @Override
    public LookupEndpointsResponse lookupEndpoints(LookupRequestType parameters)
            throws ServiceLocatorFault, InterruptedExceptionFault {
        List<W3CEndpointReference> eprs = lookupEndpoints(
                parameters.getServiceName(), parameters.getMatcherData());

        LookupEndpointsResponse response = new LookupEndpointsResponse();
        response.getEndpointReference().addAll(eprs);
        return response;
    }

    /**
     * For the given service name return list of endpoint references currently
     * registered at the service locator server endpoints.
     *
     * @param serviceName
     *            the name of the service for which to get the endpoints, must
     *            not be <code>null</code>
     * @return EndpointReferenceListType encapsulate list of endpoint references
     *         or <code>null</code>
     *
     */
    List<W3CEndpointReference> lookupEndpoints(QName serviceName,
            MatcherDataType matcherData) throws ServiceLocatorFault,
            InterruptedExceptionFault {
        SLPropertiesMatcher matcher = createMatcher(matcherData);
        List<String> names = null;
        List<W3CEndpointReference> result = new ArrayList<W3CEndpointReference>();
        String adress;
        try {
            initLocator();
            if (matcher == null) {
                names = locatorClient.lookup(serviceName);
            } else {
                names = locatorClient.lookup(serviceName, matcher);
            }
        } catch (ServiceLocatorException e) {
            ServiceLocatorFaultDetail serviceFaultDetail = new ServiceLocatorFaultDetail();
            serviceFaultDetail.setLocatorFaultDetail(serviceName.toString()
                    + "throws ServiceLocatorFault");
            throw new ServiceLocatorFault(e.getMessage(), serviceFaultDetail);
        } catch (InterruptedException e) {
            InterruptionFaultDetail interruptionFaultDetail = new InterruptionFaultDetail();
            interruptionFaultDetail.setInterruptionDetail(serviceName
                    .toString() + "throws InterruptionFault");
            throw new InterruptedExceptionFault(e.getMessage(),
                    interruptionFaultDetail);
        }
        if (names != null && !names.isEmpty()) {
            for (int i = 0; i < names.size(); i++) {
                adress = names.get(i);
                result.add(buildEndpoint(serviceName, adress));
            }
        } else {
            if (LOG.isLoggable(Level.WARNING)) {
                LOG.log(Level.WARNING, "lookup Endpoints for " + serviceName
                        + " failed, service is not known.");
            }
            ServiceLocatorFaultDetail serviceFaultDetail = new ServiceLocatorFaultDetail();
            serviceFaultDetail.setLocatorFaultDetail("lookup Endpoint for "
                    + serviceName + " failed, service is not known.");
            throw new ServiceLocatorFault("Can not find Endpoint",
                    serviceFaultDetail);
        }
        return result;
    }

    private SLPropertiesMatcher createMatcher(MatcherDataType matcherData) {
        SLPropertiesMatcher matcher = null;
        if (matcherData != null) {
            matcher = new SLPropertiesMatcher();
            List<AssertionType> assertions = matcherData.getEntry();
            for (AssertionType assertion : assertions) {
                matcher.addAssertion(assertion.getKey(), assertion.getValue());
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
        List<String> rotated = new ArrayList<String>();
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
    private W3CEndpointReference buildEndpoint(QName serviceName, String adress)
            throws ServiceLocatorFault, InterruptedExceptionFault {
        W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        // builder.serviceName(serviceName);
        builder.address(adress);
        SLEndpoint endpoint = null;
        try {
            endpoint = locatorClient.getEndpoint(serviceName, adress);
        } catch (ServiceLocatorException e) {
            ServiceLocatorFaultDetail serviceFaultDetail = new ServiceLocatorFaultDetail();
            serviceFaultDetail.setLocatorFaultDetail(serviceName.toString()
                    + "throws ServiceLocatorFault");
            throw new ServiceLocatorFault(e.getMessage(), serviceFaultDetail);
        } catch (InterruptedException e) {
            InterruptionFaultDetail interruptionFaultDetail = new InterruptionFaultDetail();
            interruptionFaultDetail.setInterruptionDetail(serviceName
                    .toString() + "throws InterruptionFault");
            throw new InterruptedExceptionFault(e.getMessage(),
                    interruptionFaultDetail);
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

    public void setAuthenticationName(String authenticationName) {
        this.authenticationName = authenticationName;
    }

    public String getAuthenticationName() {
        return authenticationName;
    }

    public void setAuthenticationPassword(String authenticationPassword) {
        this.authenticationPassword = authenticationPassword;
    }

    public String getAuthenticationPassword() {
        return authenticationPassword;
    }
}
