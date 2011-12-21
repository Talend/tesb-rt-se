/*
 * #%L
 * Locator Service :: SOAP
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

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import junit.framework.Assert;

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
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;

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
    }
    
    @Test
    public void disconnectLocator() throws InterruptedException, ServiceLocatorException {
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
    public void registerEndpointExpectedLocatorException() throws ServiceLocatorException,
            InterruptedException, ServiceLocatorFault, InterruptedExceptionFault {
        sl.register(endpoint(), EasyMock.eq(true));
        EasyMock.expectLastCall().andStubThrow(new ServiceLocatorException("test"));

        replayAll();
        lps.registerEndpoint(SERVICE_NAME, ENDPOINTURL, null, null, null);
    }
    
    @Test(expected = InterruptedExceptionFault.class)
    public void registerEndpointExpectedInterruptedException() throws ServiceLocatorException,
            InterruptedException, ServiceLocatorFault, InterruptedExceptionFault {
        sl.register(endpoint(), EasyMock.eq(true));
        EasyMock.expectLastCall().andStubThrow(new InterruptedException("test"));

        replayAll();
        lps.registerEndpoint(SERVICE_NAME, ENDPOINTURL, null, null, null);
    }

    @Test
    public void registerEndpointWithBindingAndTransport() throws InterruptedExceptionFault,
            ServiceLocatorFault, ServiceLocatorException, InterruptedException {
        sl.register(endpoint(), EasyMock.eq(true));
        EasyMock.expectLastCall();
        
        replayAll();
        LocatorSoapServiceImpl lps = new LocatorSoapServiceImpl();
        lps.setLocatorClient(sl);
        lps.registerEndpoint(SERVICE_NAME, ENDPOINTURL, BindingType.SOAP_12, TransportType.HTTPS, null);
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
    public void unregisterEndpoint() throws InterruptedExceptionFault,
            ServiceLocatorFault {
        LocatorSoapServiceImpl lps = new LocatorSoapServiceImpl();
        lps.setLocatorClient(sl);
        lps.unregisterEndpoint(SERVICE_NAME, ENDPOINTURL);
    }
    
    @Test(expected = ServiceLocatorFault.class)
    public void unregisterEndpointExpectedLocatorException() throws ServiceLocatorException,
            InterruptedException, ServiceLocatorFault, InterruptedExceptionFault {
    	sl.unregister(SERVICE_NAME, ENDPOINTURL);
        EasyMock.expectLastCall().andStubThrow(new ServiceLocatorException("test"));
        replayAll();
        lps.unregisterEndpoint(SERVICE_NAME, ENDPOINTURL);
    }
    
    @Test(expected = InterruptedExceptionFault.class)
    public void unregisterEndpointExpectedInterruptedException() throws ServiceLocatorException,
            InterruptedException, ServiceLocatorFault, InterruptedExceptionFault {
        sl.unregister(SERVICE_NAME, ENDPOINTURL);
        EasyMock.expectLastCall().andStubThrow(new InterruptedException("test"));
        replayAll();
        lps.unregisterEndpoint(SERVICE_NAME, ENDPOINTURL);
    }

    @Test
    public void lookUpEndpoint() throws InterruptedExceptionFault,
            ServiceLocatorFault, ServiceLocatorException, InterruptedException {
        names.clear();
        names.add(ENDPOINTURL);
        expect(sl.lookup(SERVICE_NAME)).andStubReturn(names);
        replayAll();

        W3CEndpointReference endpointRef, expectedRef;
        W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        //builder.serviceName(SERVICE_NAME);
        builder.address(ENDPOINTURL);
        expectedRef = builder.build();

        endpointRef = lps.lookupEndpoint(SERVICE_NAME, null);

        Assert.assertTrue(endpointRef.toString().equals(expectedRef.toString()));

    }
    
    @Test
    public void lookUpEndpointWithLookupRequestType() throws InterruptedExceptionFault,
            ServiceLocatorFault, ServiceLocatorException, InterruptedException {
        names.clear();
        names.add(ENDPOINTURL);
        expect(sl.lookup(SERVICE_NAME)).andStubReturn(names);
        replayAll();

        W3CEndpointReference endpointRef, expectedRef;
        W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        //builder.serviceName(SERVICE_NAME);
        builder.address(ENDPOINTURL);
        expectedRef = builder.build();

        LookupRequestType lrt = new LookupRequestType();
        lrt.setServiceName(SERVICE_NAME);
        LookupEndpointResponse ler = lps.lookupEndpoint(lrt);
        endpointRef = ler.getEndpointReference();
        Assert.assertTrue(endpointRef.toString().equals(expectedRef.toString()));

    }
    
    @Test(expected = ServiceLocatorFault.class)
    public void lookUpEndpointExpectedLocatorException() throws ServiceLocatorException,
            InterruptedException, ServiceLocatorFault, InterruptedExceptionFault {
    	sl.lookup(SERVICE_NAME);
        EasyMock.expectLastCall().andStubThrow(new ServiceLocatorException("test"));
        replayAll();
        lps.lookupEndpoint(SERVICE_NAME, null);
    }
    
    @Test(expected = InterruptedExceptionFault.class)
    public void lookUpEndpointExpectedInterruptedException() throws ServiceLocatorException,
            InterruptedException, ServiceLocatorFault, InterruptedExceptionFault {
        sl.lookup(SERVICE_NAME);
        EasyMock.expectLastCall().andStubThrow(new InterruptedException("test"));
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
    public void lookUpEndpointsWithLookupRequestType() throws InterruptedExceptionFault,
            ServiceLocatorFault, ServiceLocatorException, InterruptedException {

        names.clear();
        names.add(ENDPOINTURL);
        expect(sl.lookup(SERVICE_NAME)).andStubReturn(names);
        replayAll();

        W3CEndpointReference endpointRef, expectedRef;
        W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        //builder.serviceName(SERVICE_NAME);
        builder.address(ENDPOINTURL);
        expectedRef = builder.build();

        LookupRequestType lrt = new LookupRequestType();
        lrt.setServiceName(SERVICE_NAME);
        LookupEndpointsResponse ler = lps.lookupEndpoints(lrt);
        endpointRef = ler.getEndpointReference().get(0);

        Assert.assertTrue(endpointRef.toString().equals(expectedRef.toString()));

    }
    
    @Test
    public void lookUpEndpoints() throws InterruptedExceptionFault,
            ServiceLocatorFault, ServiceLocatorException, InterruptedException {

        names.clear();
        names.add(ENDPOINTURL);
        expect(sl.lookup(SERVICE_NAME)).andStubReturn(names);
        replayAll();

        W3CEndpointReference endpointRef, expectedRef;
        W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
        //builder.serviceName(SERVICE_NAME);
        builder.address(ENDPOINTURL);
        expectedRef = builder.build();
        List<W3CEndpointReference> refs;

        refs = lps.lookupEndpoints(SERVICE_NAME, null);
        endpointRef = refs.get(0);

        Assert.assertTrue(endpointRef.toString().equals(expectedRef.toString()));

    }

    @Test(expected = ServiceLocatorFault.class)
    public void lookUpEndpointsExpectedLocatorException() throws ServiceLocatorException,
            InterruptedException, ServiceLocatorFault, InterruptedExceptionFault {
    	sl.lookup(SERVICE_NAME);
        EasyMock.expectLastCall().andStubThrow(new ServiceLocatorException("test"));
        replayAll();
        lps.lookupEndpoints(SERVICE_NAME, null);
    }
    
    @Test(expected = InterruptedExceptionFault.class)
    public void lookUpEndpointsExpectedInterruptedException() throws ServiceLocatorException,
            InterruptedException, ServiceLocatorFault, InterruptedExceptionFault {
        sl.lookup(SERVICE_NAME);
        EasyMock.expectLastCall().andStubThrow(new InterruptedException("test"));
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
