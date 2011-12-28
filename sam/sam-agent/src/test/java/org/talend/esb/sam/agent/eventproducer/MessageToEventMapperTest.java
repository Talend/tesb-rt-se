/*
 * #%L
 * Service Activity Monitoring :: Agent
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
package org.talend.esb.sam.agent.eventproducer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.x500.X500Principal;
import javax.xml.namespace.QName;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.binding.Binding;
import org.apache.cxf.binding.soap.SoapBinding;
import org.apache.cxf.binding.soap.model.SoapBindingInfo;
import org.apache.cxf.common.WSDLConstants;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.EndpointException;
import org.apache.cxf.endpoint.EndpointImpl;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.security.DefaultSecurityContext;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.security.SecurityContext;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.ServiceImpl;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.OperationInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.junit.Assert;
import org.junit.Test;
import org.talend.esb.sam.agent.eventproducer.MessageToEventMapper;
import org.talend.esb.sam.agent.message.CustomInfo;
import org.talend.esb.sam.agent.message.FlowIdHelper;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;

public class MessageToEventMapperTest {
    private static final String TESTCONTENT = 
    	"This is a long long long long long long long long long long content";
    private static final int MAXCONTENTLENGTH = 30;
    private static final String FlowID = "urn:uuid:1baf6286-89e6-42ba-a800-a6b52efa6e26";
    private static final String TransportType = "http://schemas.xmlsoap.org/soap/http";
    private static final String PrincipalString = "CN=Duke,OU=JavaSoft,O=Sun Microsystems,C=US";
    private static final String Address = "http://localhost:8080/test";
    
    @Test
    public void testMapEvent() throws IOException, EndpointException {
        Message message = getTestMessage();
        Event event = new MessageToEventMapper().mapToEvent(message);
        Assert.assertEquals(EventTypeEnum.REQ_IN, event.getEventType());
        Assert.assertEquals("{interfaceNs}interfaceName", event.getMessageInfo().getPortType());
        Assert.assertEquals("{namespace}opName", event.getMessageInfo().getOperationName());
        Assert.assertEquals(TransportType, event.getMessageInfo().getTransportType());
        Assert.assertEquals(FlowID, event.getMessageInfo().getFlowId());
        Assert.assertNull(event.getMessageInfo().getMessageId());

        // By default the content should not be cut
        Assert.assertEquals(TESTCONTENT, event.getContent());
        Assert.assertFalse(event.isContentCut());

        // Principal
        Assert.assertEquals(PrincipalString, event.getOriginator().getPrincipal());

        Map<String, String> customInfo = event.getCustomInfo();
        Assert.assertEquals(2, customInfo.keySet().size());
        Assert.assertEquals(Address, customInfo.get("address"));
        Assert.assertEquals("value1", customInfo.get("key1"));

    }
    
    @Test
    public void testMaxContentLength() throws IOException, EndpointException {
        Message message = getTestMessage();
        MessageToEventMapper mapper = new MessageToEventMapper();
        mapper.setMaxContentLength(MAXCONTENTLENGTH);
        Event event = mapper.mapToEvent(message);
        //System.out.println(event.getContent());
        Assert.assertEquals(MAXCONTENTLENGTH, event.getContent().length());
        Assert.assertEquals("<cut><![CDATA[" + TESTCONTENT.substring(0, MAXCONTENTLENGTH - 23) + "]]></cut>", event.getContent());
        Assert.assertTrue(event.isContentCut());
    }
    
    private Message getTestMessage() throws IOException, EndpointException {
        Message message = new MessageImpl();
        ExchangeImpl exchange = new ExchangeImpl();
        ServiceInfo serviceInfo = new ServiceInfo();
        InterfaceInfo interfaceInfo = new InterfaceInfo(serviceInfo, new QName("interfaceNs", "interfaceName"));
        serviceInfo.setInterface(interfaceInfo );
        SoapBindingInfo bInfo = new SoapBindingInfo(serviceInfo , WSDLConstants.NS_SOAP12);
        bInfo.setTransportURI(TransportType);
        OperationInfo opInfo = new OperationInfo();
        opInfo.setName(new QName("namespace", "opName"));
        BindingOperationInfo bindingOpInfo = new BindingOperationInfo(bInfo, opInfo);
        exchange.put(BindingOperationInfo.class, bindingOpInfo);
        SoapBinding binding = new SoapBinding(bInfo);
        exchange.put(Binding.class, binding);
        String ns = "ns";
		EndpointInfo ei = new EndpointInfo(serviceInfo, ns );
		ei.setAddress(Address);
		Service service = new ServiceImpl();
		Bus bus = BusFactory.getThreadDefaultBus();
		Endpoint endpoint = new EndpointImpl(bus, service, ei);
		exchange.put(Endpoint.class, endpoint );
        message.setExchange(exchange);

        FlowIdHelper.setFlowId(message,FlowID);

        Principal principal = new X500Principal(PrincipalString);
        SecurityContext sc = new DefaultSecurityContext(principal, new Subject());
        message.put(SecurityContext.class, sc);
        
        CachedOutputStream cos = new CachedOutputStream();
        
        InputStream is = new ByteArrayInputStream(TESTCONTENT.getBytes("UTF-8"));
        IOUtils.copy(is, cos);
        message.setContent(CachedOutputStream.class, cos);
        
        CustomInfo customInfo = CustomInfo.getOrCreateCustomInfo(message);
        customInfo.put("key1", "value1");

        return message;
    }
}
