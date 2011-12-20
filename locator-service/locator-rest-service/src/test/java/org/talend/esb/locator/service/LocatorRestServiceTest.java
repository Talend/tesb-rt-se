/*
 * #%L
 * Locator Service :: REST
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
package org.talend.esb.locator.service;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.xml.namespace.QName;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;
import junit.framework.Assert;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.easymock.IArgumentMatcher;
import org.junit.Before;
import org.junit.Test;
import org.talend.schemas.esb.locator._2011._11.BindingType;
import org.talend.schemas.esb.locator.rest._2011._11.EndpointReferenceList;
import org.talend.schemas.esb.locator.rest._2011._11.EntryType;
import org.talend.schemas.esb.locator.rest._2011._11.RegisterEndpointRequest;
import org.talend.schemas.esb.locator._2011._11.TransportType;
import org.talend.esb.servicelocator.client.Endpoint;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;

public class LocatorRestServiceTest extends EasyMockSupport {

    private ServiceLocator sl;
    private static QName SERVICE_NAME;
    private final static String ENDPOINTURL = "http://Service";
    private final static String QNAME_PREFIX1 = "http://services.talend.org/TestService";
    private final static String QNAME_LOCALPART1 = "TestServiceProvider";
    private List<String> names;
    private LocatorRestServiceImpl lps;

    @Before
    public void setup() {
        sl = createMock(ServiceLocator.class);
        names = new ArrayList<String>();
        SERVICE_NAME = new QName(QNAME_PREFIX1, QNAME_LOCALPART1);
        names = new ArrayList<String>();
        lps = new LocatorRestServiceImpl();
        lps.setLocatorClient(sl);
        lps.setLocatorEndpoints("localhost:2181");
        lps.setConnectionTimeout(5000);
        lps.setSessionTimeout(5000);
    }
    
    @Test
    public void disconnectLocator() throws InterruptedException, ServiceLocatorException {
        sl.disconnect();
        EasyMock.expectLastCall();
        replayAll();
        lps.disconnectLocator();
    }
    
    @Test
    public void lookUpEndpointTest() throws ServiceLocatorException,
            InterruptedException {
        names.clear();
        names.add(ENDPOINTURL);
        expect(sl.lookup(SERVICE_NAME)).andStubReturn(names);
        replayAll();

        W3CEndpointReference endpointRef, expectedRef;
        W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        //builder.serviceName(SERVICE_NAME);
        builder.address(ENDPOINTURL);
        expectedRef = builder.build();

        endpointRef = lps.lookupEndpoint(SERVICE_NAME.toString(),
                new ArrayList<String>());

        Assert.assertTrue(endpointRef.toString().equals(expectedRef.toString()));
    }
    
    @Test(expected = WebApplicationException.class)
    public void lookUpEndpointExpectedLocatorException() throws ServiceLocatorException,
            InterruptedException {
        names.clear();
        names.add(ENDPOINTURL);
        expect(sl.lookup(SERVICE_NAME)).andStubThrow(new ServiceLocatorException("test"));
        replayAll();
        lps.lookupEndpoint(SERVICE_NAME.toString(), new ArrayList<String>());
    }
    
    @Test(expected = WebApplicationException.class)
    public void lookUpEndpointExpectedInterruptedException() throws ServiceLocatorException,
            InterruptedException {
        names.clear();
        names.add(ENDPOINTURL);
        expect(sl.lookup(SERVICE_NAME)).andStubThrow(new InterruptedException("test"));
        replayAll();
        lps.lookupEndpoint(SERVICE_NAME.toString(), new ArrayList<String>());
    }

    @Test
    public void lookUpEndpointTestNotFound() throws ServiceLocatorException,
            InterruptedException {
        names.clear();
        names.add(ENDPOINTURL);
        expect(sl.lookup(SERVICE_NAME)).andStubReturn(null);
        replayAll();

        try {
            lps.lookupEndpoint(SERVICE_NAME.toString(), new ArrayList<String>());
        } catch (WebApplicationException ex) {
            Assert.assertTrue(ex.getResponse().getStatus() == 404);
        }
    }

    @Test
    public void lookUpEndpoints() throws ServiceLocatorException,
            InterruptedException {
        names.clear();
        names.add(ENDPOINTURL);
        expect(sl.lookup(SERVICE_NAME)).andStubReturn(names);
        replayAll();

        W3CEndpointReference expectedRef;
        W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        //builder.serviceName(SERVICE_NAME);
        builder.address(ENDPOINTURL);
        expectedRef = builder.build();

        EndpointReferenceList erlt = lps.lookupEndpoints(
                SERVICE_NAME.toString(), new ArrayList<String>());
        if (erlt.getEndpointReference().get(0).equals(expectedRef))
            fail();

    }
    
    @Test(expected = WebApplicationException.class)
    public void lookUpEndpointsExpectedLocatorException() throws ServiceLocatorException,
            InterruptedException {
        names.clear();
        names.add(ENDPOINTURL);
        expect(sl.lookup(SERVICE_NAME)).andStubThrow(new ServiceLocatorException("test"));
        replayAll();
        lps.lookupEndpoints(SERVICE_NAME.toString(), new ArrayList<String>());
    }
    
    @Test(expected = WebApplicationException.class)
    public void lookUpEndpointsExpectedInterruptedException() throws ServiceLocatorException,
            InterruptedException {
        names.clear();
        names.add(ENDPOINTURL);
        expect(sl.lookup(SERVICE_NAME)).andStubThrow(new InterruptedException("test"));
        replayAll();
        lps.lookupEndpoints(SERVICE_NAME.toString(), new ArrayList<String>());
    }

    @Test
    public void lookUpEndpointsNotFound() throws ServiceLocatorException,
            InterruptedException {
        names.clear();
        names.add(ENDPOINTURL);
        expect(sl.lookup(SERVICE_NAME)).andStubReturn(null);
        replayAll();

        try {
            lps.lookupEndpoints(SERVICE_NAME.toString(),
                    new ArrayList<String>());
        } catch (WebApplicationException ex) {
            if (ex.getResponse().getStatus() != 404)
                fail();
        }
    }

    @Test
    public void unregisterEndpoint() throws ServiceLocatorException,
            InterruptedException {
        sl.unregister(SERVICE_NAME, ENDPOINTURL);
        EasyMock.expectLastCall();
        replayAll();
        try {
            lps.unregisterEndpoint(SERVICE_NAME.toString(), ENDPOINTURL);
        } catch (WebApplicationException ex) {
            fail();
        }
    }
    
    @Test(expected = WebApplicationException.class)
    public void unregisterEndpointExpectedLocatorException() throws ServiceLocatorException,
            InterruptedException {
        sl.unregister(SERVICE_NAME, ENDPOINTURL);
        EasyMock.expectLastCall().andStubThrow(new ServiceLocatorException("test"));
        replayAll();
        lps.unregisterEndpoint(SERVICE_NAME.toString(), ENDPOINTURL);
    }
    
    @Test(expected = WebApplicationException.class)
    public void unregisterEndpointExpectedInterruptedException() throws ServiceLocatorException,
            InterruptedException {
        sl.unregister(SERVICE_NAME, ENDPOINTURL);
        EasyMock.expectLastCall().andStubThrow(new InterruptedException("test"));
        replayAll();
        lps.unregisterEndpoint(SERVICE_NAME.toString(), ENDPOINTURL);
    }

    @Test
    public void registerEndpoint() throws ServiceLocatorException,
            InterruptedException {
        sl.register(endpoint(), EasyMock.eq(true));
        EasyMock.expectLastCall();

        replayAll();
        RegisterEndpointRequest req = new RegisterEndpointRequest();
        req.setEndpointURL(ENDPOINTURL);
        req.setServiceName(SERVICE_NAME.toString());
        lps.registerEndpoint(req);
    }
    
    @Test
    public void registerEndpointWithProps() throws ServiceLocatorException,
            InterruptedException {
        sl.register(endpoint(), EasyMock.eq(true));
        EasyMock.expectLastCall();

        replayAll();
        RegisterEndpointRequest req = new RegisterEndpointRequest();
        EntryType entryType = new EntryType();
        entryType.setKey("test");
        entryType.getValue().add("test");
        req.getEntryType().add(entryType);
        req.setEndpointURL(ENDPOINTURL);
        req.setServiceName(SERVICE_NAME.toString());
        lps.registerEndpoint(req);
    }
    
    @Test(expected = WebApplicationException.class)
    public void registerEndpointExpectedLocatorException() throws ServiceLocatorException,
            InterruptedException {
        sl.register(endpoint(), EasyMock.eq(true));
        EasyMock.expectLastCall().andStubThrow(new ServiceLocatorException("test"));

        replayAll();
        RegisterEndpointRequest req = new RegisterEndpointRequest();
        EntryType entryType = new EntryType();
        entryType.setKey("test");
        entryType.getValue().add("test");
        req.getEntryType().add(entryType);
        req.setEndpointURL(ENDPOINTURL);
        req.setServiceName(SERVICE_NAME.toString());
        lps.registerEndpoint(req);
    }
    
    @Test(expected = WebApplicationException.class)
    public void registerEndpointExpectedInterruptedException() throws ServiceLocatorException,
            InterruptedException {
        sl.register(endpoint(), EasyMock.eq(true));
        EasyMock.expectLastCall().andStubThrow(new InterruptedException("test"));

        replayAll();
        RegisterEndpointRequest req = new RegisterEndpointRequest();
        EntryType entryType = new EntryType();
        entryType.setKey("test");
        entryType.getValue().add("test");
        req.getEntryType().add(entryType);
        req.setEndpointURL(ENDPOINTURL);
        req.setServiceName(SERVICE_NAME.toString());
        lps.registerEndpoint(req);
    }

    @Test
    public void registerEndpointWithOptParam() throws ServiceLocatorException,
            InterruptedException {
        sl.register(endpoint(), EasyMock.eq(true));
        EasyMock.expectLastCall();

        replayAll();
        RegisterEndpointRequest req = new RegisterEndpointRequest();
        req.setEndpointURL(ENDPOINTURL);
        req.setServiceName(SERVICE_NAME.toString());
        req.setBinding(BindingType.JAXRS);
        req.setTransport(TransportType.HTTPS);
        lps.registerEndpoint(req);
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
