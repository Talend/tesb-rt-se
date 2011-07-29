package org.talend.esb.sam.agent.lifecycle;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Queue;

import org.apache.cxf.binding.soap.SoapBinding;
import org.apache.cxf.binding.soap.model.SoapBindingInfo;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerLifeCycleListener;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.common.event.MessageInfo;
import org.talend.esb.sam.common.event.Originator;
import org.talend.esb.sam.agent.util.Converter;

/**
 * This ServerLifeCycleListener impl used to implement the feature of 
 * support web service start/stop event
 */
public class ServiceListenerImpl implements ServerLifeCycleListener{

	private Queue<Event> queue;
	
    public void setQueue(Queue<Event> queue) {
        this.queue = queue;
    }	
	
	@Override
	public void startServer(Server server) {
		Event startEvent = createEvent(server, EventTypeEnum.SERVICE_START);
		queue.add(startEvent);
	}

	@Override
	public void stopServer(Server server) {
		Event startEvent = createEvent(server, EventTypeEnum.SERVICE_STOP);
		queue.add(startEvent);
	}
	
	private Event createEvent(Server server, EventTypeEnum type){
		String portType = server.getEndpoint().getBinding().getBindingInfo().getService().getInterface().getName().toString();
		//System.out.println("port_type: " + portType);
		
		String transportType = null;
		if (server.getEndpoint().getBinding() instanceof SoapBinding){
			SoapBinding soapBinding = (SoapBinding)server.getEndpoint().getBinding();
            if (soapBinding.getBindingInfo() instanceof SoapBindingInfo) {
                SoapBindingInfo soapBindingInfo = (SoapBindingInfo)soapBinding.getBindingInfo();
                transportType = soapBindingInfo.getTransportURI();
                //System.out.println("transport_type: " + transportType);
            }    
		}
       
		String address = server.getEndpoint().getEndpointInfo().getAddress();
		//System.out.println("address: " + address);
		
		Event event = new Event();
        MessageInfo messageInfo = new MessageInfo();
        Originator originator = new Originator();
        event.setMessageInfo(messageInfo);
        event.setOriginator(originator);
        
        Date date = new Date();
        event.setTimestamp(date);
        event.setEventType(type);
        
        messageInfo.setPortType(portType);
        messageInfo.setTransportType(transportType);
        
        if (messageInfo.getTransportType() == null) {
            messageInfo.setTransportType("Unknown transport type");
        }
        
        originator.setProcessId(Converter.getPID());
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            originator.setIp(inetAddress.getHostAddress());
            originator.setHostname(inetAddress.getHostName());
        } catch (UnknownHostException e) {
        	originator.setHostname("Unknown hostname");
        	originator.setIp("Unknown ip address");
        }        
        
        event.getCustomInfo().put("address", address);
        
		return event;
	}

}
