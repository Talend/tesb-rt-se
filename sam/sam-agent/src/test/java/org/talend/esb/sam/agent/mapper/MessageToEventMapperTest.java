package org.talend.esb.sam.agent.mapper;

import javax.xml.namespace.QName;

import org.apache.cxf.binding.Binding;
import org.apache.cxf.binding.soap.SoapBinding;
import org.apache.cxf.binding.soap.model.SoapBindingInfo;
import org.apache.cxf.common.WSDLConstants;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.OperationInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.junit.Assert;
import org.junit.Test;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;

public class MessageToEventMapperTest {
    
    @Test
    public void testMapEvent() {
        Message message = new MessageImpl();
        ExchangeImpl exchange = new ExchangeImpl();
        ServiceInfo serviceInfo = new ServiceInfo();
        InterfaceInfo interfaceInfo = new InterfaceInfo(serviceInfo, new QName("interfaceNs", "interfaceName"));
        serviceInfo.setInterface(interfaceInfo );
        SoapBindingInfo bInfo = new SoapBindingInfo(serviceInfo , WSDLConstants.NS_SOAP12);
        bInfo.setTransportURI("transportUri");
        OperationInfo opInfo = new OperationInfo();
        opInfo.setName(new QName("namespace", "opName"));
        BindingOperationInfo bindingOpInfo = new BindingOperationInfo(bInfo, opInfo);
        exchange.put(BindingOperationInfo.class, bindingOpInfo);
        SoapBinding binding = new SoapBinding(bInfo);
        exchange.put(Binding.class, binding);
        message.setExchange(exchange);
        Event event = new MessageToEventMapperImpl().mapToEvent(message);
        Assert.assertEquals(EventTypeEnum.REQ_IN, event.getEventType());
        Assert.assertEquals("{interfaceNs}interfaceName", event.getMessageInfo().getPortType());
        Assert.assertEquals("{namespace}opName", event.getMessageInfo().getOperationName());
        Assert.assertEquals("", event.getContent());
        Assert.assertEquals("transportUri", event.getMessageInfo().getTransportType());
        // TODO add assertions
        System.out.println(event);
    }
}
