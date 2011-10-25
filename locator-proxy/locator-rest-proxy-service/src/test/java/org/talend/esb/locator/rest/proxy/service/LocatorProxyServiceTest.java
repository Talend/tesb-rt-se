/*
 * #%L
 * REST Service Locator Proxy :: Service
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
package org.talend.esb.locator.rest.proxy.service;

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
import org.junit.Before;
import org.junit.Test;
import org.talend.schemas.esb.locator._2011._11.RegisterEndpoint;
import org.talend.schemas.esb.locator.rest._2011._11.EndpointReferenceList;
import org.talend.schemas.esb.locator.rest._2011._11.RegisterEndpointRequest;
import org.talend.esb.servicelocator.client.BindingType;
import org.talend.esb.servicelocator.client.Endpoint;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.SimpleEndpoint;
import org.talend.esb.servicelocator.client.TransportType;

public class LocatorProxyServiceTest extends EasyMockSupport {

    private ServiceLocator sl;
    private QName SERVICE_NAME;
    private final String ENDPOINTURL = "http://Service";
    private final String QNAME_PREFIX1 = "http://services.talend.org/TestService";
    private final String QNAME_LOCALPART1 = "TestServiceProvider";
    private List<String> names;
    private LocatorRestProxyServiceImpl lps;

    @Before
    public void setup() {
        sl = createMock(ServiceLocator.class);
        names = new ArrayList<String>();
        SERVICE_NAME = new QName(QNAME_PREFIX1, QNAME_LOCALPART1);
        names = new ArrayList<String>();
        lps = new LocatorRestProxyServiceImpl();
        lps.setLocatorClient(sl);
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
        builder.serviceName(SERVICE_NAME);
        builder.address(ENDPOINTURL);
        expectedRef = builder.build();

        endpointRef = lps.lookupEndpoint(SERVICE_NAME.toString(),
                new ArrayList<String>());

        Assert.assertTrue(endpointRef.toString().equals(expectedRef.toString()));
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
        builder.serviceName(SERVICE_NAME);
        builder.address(ENDPOINTURL);
        expectedRef = builder.build();

        EndpointReferenceList erlt = lps.lookupEndpoints(
                SERVICE_NAME.toString(), new ArrayList<String>());
        if (erlt.getReturn().get(0).equals(expectedRef))
            fail();

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
}
