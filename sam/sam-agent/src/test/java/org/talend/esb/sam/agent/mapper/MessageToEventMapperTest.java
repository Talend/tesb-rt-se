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
import org.junit.Test;
import org.talend.esb.sam.agent.interceptor.InterceptorType;
import org.talend.esb.sam.agent.mapper.MessageToEventMapperImpl;
import org.talend.esb.sam.common.event.Event;

public class MessageToEventMapperTest {
    
    @Test
    public void testMapEvent() {
        Message message = new MessageImpl();
        ExchangeImpl exchange = new ExchangeImpl();
        ServiceInfo serviceInfo = new ServiceInfo();
        InterfaceInfo interfaceInfo = new InterfaceInfo(serviceInfo, new QName("interfacens", "interfaceName"));
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
        Event event = new MessageToEventMapperImpl().mapToEvent(message, InterceptorType.IN, "content");
        // TODO add assertions
        System.out.println(event);
    }
}
