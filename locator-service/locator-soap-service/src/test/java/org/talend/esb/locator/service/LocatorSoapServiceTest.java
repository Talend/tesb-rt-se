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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.easymock.IArgumentMatcher;
import org.junit.Before;
import org.junit.Test;
import org.talend.schemas.esb.locator._2011._11.EntryType;
import org.talend.schemas.esb.locator._2011._11.LookupEndpointResponse;
import org.talend.schemas.esb.locator._2011._11.LookupEndpointsResponse;
import org.talend.schemas.esb.locator._2011._11.LookupRequestType;
import org.talend.schemas.esb.locator._2011._11.SLPropertiesType;
import org.talend.schemas.esb.locator._2011._11.BindingType;
import org.talend.schemas.esb.locator._2011._11.TransportType;
import org.talend.services.esb.locator.v1.InterruptedExceptionFault;
import org.talend.services.esb.locator.v1.ServiceLocatorFault;
import org.talend.esb.servicelocator.client.Endpoint;
import org.talend.esb.servicelocator.client.EndpointNotFoundException;
import org.talend.esb.servicelocator.client.SLEndpoint;
import org.talend.esb.servicelocator.client.SLPropertiesImpl;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.WrongArgumentException;
import org.talend.esb.servicelocator.client.internal.EndpointTransformerImpl;
import org.w3c.dom.Document;

public class LocatorSoapServiceTest extends EasyMockSupport {

    private ServiceLocator sl;
    private static QName SERVICE_NAME;
    private QName NOT_EXIST_SERVICE_NAME;
    private final String PROPERTY_KEY = "Key1";
    private final String PROPERTY_VALUE1 = "Value1";
    private final String PROPERTY_VALUE2 = "Value2";
    private final static String ENDPOINTURL = "http://Service";;
    private final static String QNAME_PREFIX1 = "http://services.talend.org/TestService";
    private final static String QNAME_LOCALPART1 = "TestServiceProvider";
    private final static String QNAME_PREFIX2 = "http://services.talend.org/NoNameService";
    private final static String QNAME_LOCALPART2 = "NoNameServiceProvider";
    private List<String> names;
    private LocatorSoapServiceImpl lps;
    private SLEndpoint endpoint;

    @Before
    public void setup() {

        sl = createMock(ServiceLocator.class);
        SERVICE_NAME = new QName(QNAME_PREFIX1, QNAME_LOCALPART1);
        NOT_EXIST_SERVICE_NAME = new QName(QNAME_PREFIX2, QNAME_LOCALPART2);
        names = new ArrayList<String>();
        lps = new LocatorSoapServiceImpl();
        lps.setLocatorClient(sl);
        lps.setLocatorEndpoints("localhost:2181");
        lps.setConnectionTimeout(5000);
        lps.setSessionTimeout(5000);
        endpoint = createMock(SLEndpoint.class);
    }

    @Test
    public void disconnectLocator() throws InterruptedException,
            ServiceLocatorException {
        sl.disconnect();
        EasyMock.expectLastCall();
        replayAll();
        lps.disconnectLocator();
    }

    @Test
    public void registerEndpoint() throws InterruptedExceptionFault,
            ServiceLocatorFault, ServiceLocatorException, InterruptedException {
        sl.register(endpoint(), EasyMock.eq(true));
        EasyMock.expectLastCall();
        LocatorSoapServiceImpl lps = new LocatorSoapServiceImpl();
        lps.setLocatorClient(sl);
        lps.registerEndpoint(SERVICE_NAME, ENDPOINTURL, null, null, null);
    }

    @Test(expected = ServiceLocatorFault.class)
    public void registerEndpointExpectedLocatorException()
            throws ServiceLocatorException, InterruptedException,
            ServiceLocatorFault, InterruptedExceptionFault {
        sl.register(endpoint(), EasyMock.eq(true));
        EasyMock.expectLastCall().andStubThrow(
                new ServiceLocatorException("test"));

        replayAll();
        lps.registerEndpoint(SERVICE_NAME, ENDPOINTURL, null, null, null);
    }

    @Test(expected = InterruptedExceptionFault.class)
    public void registerEndpointExpectedInterruptedException()
            throws ServiceLocatorException, InterruptedException,
            ServiceLocatorFault, InterruptedExceptionFault {
        sl.register(endpoint(), EasyMock.eq(true));
        EasyMock.expectLastCall()
                .andStubThrow(new InterruptedException("test"));

        replayAll();
        lps.registerEndpoint(SERVICE_NAME, ENDPOINTURL, null, null, null);
    }

    @Test
    public void registerEndpointWithBindingAndTransport()
            throws InterruptedExceptionFault, ServiceLocatorFault,
            ServiceLocatorException, InterruptedException {
        sl.register(endpoint(), EasyMock.eq(true));
        EasyMock.expectLastCall();

        replayAll();
        LocatorSoapServiceImpl lps = new LocatorSoapServiceImpl();
        lps.setLocatorClient(sl);
        lps.registerEndpoint(SERVICE_NAME, ENDPOINTURL, BindingType.SOAP_12,
                TransportType.HTTPS, null);
    }

    @Test
    public void registerEndpointWithOptionalParameter()
            throws InterruptedExceptionFault, ServiceLocatorFault {
        LocatorSoapServiceImpl lps = new LocatorSoapServiceImpl();
        lps.setLocatorClient(sl);

        SLPropertiesType value = new SLPropertiesType();
        EntryType e = new EntryType();

        e.setKey(PROPERTY_KEY);
        e.getValue().add(PROPERTY_VALUE1);
        e.getValue().add(PROPERTY_VALUE2);
        value.getEntry().add(e);

        lps.registerEndpoint(SERVICE_NAME, ENDPOINTURL, null, null, value);
    }

    @Test
    public void updateEndpointExpiringTime() throws Exception {
        final int ttl = 95;

        sl.updateTimetolive(SERVICE_NAME, ENDPOINTURL, ttl);
        replay(sl);

        lps.updateTimetolive(SERVICE_NAME, ENDPOINTURL, ttl);

        verify(sl);
    }

    @Test
    public void updateEndpointExpiringTimeMissingEndpoint() throws Exception {
        final int ttl = 95;

        sl.updateTimetolive(SERVICE_NAME, ENDPOINTURL, ttl);
        expectLastCall().andThrow(new EndpointNotFoundException());
        replay(sl);

        try {
            lps.updateTimetolive(SERVICE_NAME, ENDPOINTURL, ttl);
            fail();
        } catch (ServiceLocatorFault e) {
            // pass
        }

        verify(sl);
    }

    @Test
    public void updateEndpointExpiringTimeWrongTime() throws Exception {
        final int ttl = 95;

        sl.updateTimetolive(SERVICE_NAME, ENDPOINTURL, ttl);
        expectLastCall().andThrow(new WrongArgumentException());
        replay(sl);

        try {
            lps.updateTimetolive(SERVICE_NAME, ENDPOINTURL, ttl);
            fail();
        } catch (ServiceLocatorFault e) {
            // pass
        }

        verify(sl);
    }

    @Test
    public void unregisterEndpoint() throws InterruptedExceptionFault,
            ServiceLocatorFault {
        LocatorSoapServiceImpl lps = new LocatorSoapServiceImpl();
        lps.setLocatorClient(sl);
        lps.unregisterEndpoint(SERVICE_NAME, ENDPOINTURL);
    }

    @Test(expected = ServiceLocatorFault.class)
    public void unregisterEndpointExpectedLocatorException()
            throws ServiceLocatorException, InterruptedException,
            ServiceLocatorFault, InterruptedExceptionFault {
        sl.unregister(SERVICE_NAME, ENDPOINTURL);
        EasyMock.expectLastCall().andStubThrow(
                new ServiceLocatorException("test"));
        replayAll();
        lps.unregisterEndpoint(SERVICE_NAME, ENDPOINTURL);
    }

    @Test(expected = InterruptedExceptionFault.class)
    public void unregisterEndpointExpectedInterruptedException()
            throws ServiceLocatorException, InterruptedException,
            ServiceLocatorFault, InterruptedExceptionFault {
        sl.unregister(SERVICE_NAME, ENDPOINTURL);
        EasyMock.expectLastCall()
                .andStubThrow(new InterruptedException("test"));
        replayAll();
        lps.unregisterEndpoint(SERVICE_NAME, ENDPOINTURL);
    }

    @Test
    public void lookUpEndpoint() throws InterruptedExceptionFault,
            ServiceLocatorFault, ServiceLocatorException, InterruptedException {
        names.clear();
        names.add(ENDPOINTURL);
        expect(sl.lookup(SERVICE_NAME)).andStubReturn(names);
        expect(sl.getEndpoint(SERVICE_NAME, ENDPOINTURL)).andStubReturn(
                endpoint);
        expect(endpoint.getProperties()).andStubReturn(
                SLPropertiesImpl.EMPTY_PROPERTIES);
        replayAll();

        W3CEndpointReference endpointRef, expectedRef;
        W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        // builder.serviceName(SERVICE_NAME);
        builder.address(ENDPOINTURL);
        expectedRef = builder.build();

        endpointRef = lps.lookupEndpoint(SERVICE_NAME, null);

        assertTrue(endpointRef.toString().equals(expectedRef.toString()));

    }

    @Test
    public void lookUpEndpointWithReturnProps() throws InterruptedExceptionFault,
            ServiceLocatorFault, ServiceLocatorException, InterruptedException {
        names.clear();
        names.add(ENDPOINTURL);

        SLPropertiesImpl slPropertiesImpl = new SLPropertiesImpl();
        List<String> list = new ArrayList<String>();
        slPropertiesImpl.addProperty("test", list);

        expect(sl.lookup(SERVICE_NAME)).andStubReturn(names);
        expect(sl.getEndpoint(SERVICE_NAME, ENDPOINTURL)).andStubReturn(
                endpoint);
        expect(endpoint.getProperties()).andStubReturn(slPropertiesImpl);
        replayAll();

        W3CEndpointReference endpointRef, expectedRef;
        W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        // builder.serviceName(SERVICE_NAME);
        builder.address(ENDPOINTURL);

        EndpointTransformerImpl transformer = new EndpointTransformerImpl();

        DOMResult result = new DOMResult();
        transformer.writePropertiesTo(slPropertiesImpl, result);
        Document docResult = (Document) result.getNode();

        builder.metadata(docResult.getDocumentElement());

        expectedRef = builder.build();

        endpointRef = lps.lookupEndpoint(SERVICE_NAME, null);

        assertTrue(endpointRef.toString().equals(expectedRef.toString()));

    }

    @Test
    public void lookUpEndpointWithLookupRequestType()
            throws InterruptedExceptionFault, ServiceLocatorFault,
            ServiceLocatorException, InterruptedException {
        names.clear();
        names.add(ENDPOINTURL);
        expect(sl.lookup(SERVICE_NAME)).andStubReturn(names);
        expect(sl.getEndpoint(SERVICE_NAME, ENDPOINTURL)).andStubReturn(
                endpoint);
        expect(endpoint.getProperties()).andStubReturn(
                SLPropertiesImpl.EMPTY_PROPERTIES);
        replayAll();

        W3CEndpointReference endpointRef, expectedRef;
        W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        // builder.serviceName(SERVICE_NAME);
        builder.address(ENDPOINTURL);
        expectedRef = builder.build();

        LookupRequestType lrt = new LookupRequestType();
        lrt.setServiceName(SERVICE_NAME);
        LookupEndpointResponse ler = lps.lookupEndpoint(lrt);
        endpointRef = ler.getEndpointReference();
        assertTrue(endpointRef.toString().equals(expectedRef.toString()));

    }

    @Test(expected = ServiceLocatorFault.class)
    public void lookUpEndpointExpectedLocatorException()
            throws ServiceLocatorException, InterruptedException,
            ServiceLocatorFault, InterruptedExceptionFault {
        sl.lookup(SERVICE_NAME);
        EasyMock.expectLastCall().andStubThrow(
                new ServiceLocatorException("test"));
        replayAll();
        lps.lookupEndpoint(SERVICE_NAME, null);
    }

    @Test(expected = InterruptedExceptionFault.class)
    public void lookUpEndpointExpectedInterruptedException()
            throws ServiceLocatorException, InterruptedException,
            ServiceLocatorFault, InterruptedExceptionFault {
        sl.lookup(SERVICE_NAME);
        EasyMock.expectLastCall()
                .andStubThrow(new InterruptedException("test"));
        replayAll();
        lps.lookupEndpoint(SERVICE_NAME, null);
    }

    @Test(expected = ServiceLocatorFault.class)
    public void lookUpEndpointFault() throws InterruptedExceptionFault,
            ServiceLocatorFault, ServiceLocatorException, InterruptedException {

        expect(sl.lookup(NOT_EXIST_SERVICE_NAME)).andStubReturn(null);
        replayAll();

        lps.lookupEndpoint(NOT_EXIST_SERVICE_NAME, null);
    }

    @Test
    public void lookUpEndpointsWithLookupRequestType()
            throws InterruptedExceptionFault, ServiceLocatorFault,
            ServiceLocatorException, InterruptedException {

        names.clear();
        names.add(ENDPOINTURL);
        expect(sl.lookup(SERVICE_NAME)).andStubReturn(names);
        expect(sl.getEndpoint(SERVICE_NAME, ENDPOINTURL)).andStubReturn(
                endpoint);
        expect(endpoint.getProperties()).andStubReturn(
                SLPropertiesImpl.EMPTY_PROPERTIES);
        replayAll();

        W3CEndpointReference endpointRef, expectedRef;
        W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        // builder.serviceName(SERVICE_NAME);
        builder.address(ENDPOINTURL);
        expectedRef = builder.build();

        LookupRequestType lrt = new LookupRequestType();
        lrt.setServiceName(SERVICE_NAME);
        LookupEndpointsResponse ler = lps.lookupEndpoints(lrt);
        endpointRef = ler.getEndpointReference().get(0);

        assertTrue(endpointRef.toString().equals(expectedRef.toString()));

    }

    @Test
    public void lookUpEndpoints() throws InterruptedExceptionFault,
            ServiceLocatorFault, ServiceLocatorException, InterruptedException {

        names.clear();
        names.add(ENDPOINTURL);
        expect(sl.lookup(SERVICE_NAME)).andStubReturn(names);
        expect(sl.getEndpoint(SERVICE_NAME, ENDPOINTURL)).andStubReturn(
                endpoint);
        expect(endpoint.getProperties()).andStubReturn(
                SLPropertiesImpl.EMPTY_PROPERTIES);
        replayAll();

        W3CEndpointReference endpointRef, expectedRef;
        W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        // builder.serviceName(SERVICE_NAME);
        builder.address(ENDPOINTURL);
        expectedRef = builder.build();
        List<W3CEndpointReference> refs;

        refs = lps.lookupEndpoints(SERVICE_NAME, null);
        endpointRef = refs.get(0);

        assertTrue(endpointRef.toString().equals(expectedRef.toString()));

    }

    @Test(expected = ServiceLocatorFault.class)
    public void lookUpEndpointsExpectedLocatorException()
            throws ServiceLocatorException, InterruptedException,
            ServiceLocatorFault, InterruptedExceptionFault {
        sl.lookup(SERVICE_NAME);
        EasyMock.expectLastCall().andStubThrow(
                new ServiceLocatorException("test"));
        replayAll();
        lps.lookupEndpoints(SERVICE_NAME, null);
    }

    @Test(expected = InterruptedExceptionFault.class)
    public void lookUpEndpointsExpectedInterruptedException()
            throws ServiceLocatorException, InterruptedException,
            ServiceLocatorFault, InterruptedExceptionFault {
        sl.lookup(SERVICE_NAME);
        EasyMock.expectLastCall()
                .andStubThrow(new InterruptedException("test"));
        replayAll();
        lps.lookupEndpoints(SERVICE_NAME, null);
    }

    @Test(expected = ServiceLocatorFault.class)
    public void lookUpEndpointsFault() throws InterruptedExceptionFault,
            ServiceLocatorFault, ServiceLocatorException, InterruptedException {

        names.clear();
        names.add(ENDPOINTURL);
        expect(sl.lookup(NOT_EXIST_SERVICE_NAME)).andStubReturn(null);
        replayAll();

        lps.lookupEndpoints(NOT_EXIST_SERVICE_NAME, null);

    }

    public static Endpoint endpoint() {
        EasyMock.reportMatcher(new simpleEndpointMatcher());
        return null;
    }

    public static class simpleEndpointMatcher implements IArgumentMatcher {

        @Override
        public boolean matches(Object argument) {
            if (argument != null && argument instanceof Endpoint) {
                Endpoint result = (Endpoint) argument;
                if (!ENDPOINTURL.equals(result.getAddress()))
                    return false;
                if (!SERVICE_NAME.equals(result.getServiceName()))
                    return false;
            }
            return true;
        }

        @Override
        public void appendTo(StringBuffer buffer) {
        }
    }

}
