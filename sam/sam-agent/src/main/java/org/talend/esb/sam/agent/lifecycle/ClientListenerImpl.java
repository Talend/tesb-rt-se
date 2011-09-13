package org.talend.esb.sam.agent.lifecycle;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;

import org.apache.cxf.binding.soap.SoapBinding;
import org.apache.cxf.binding.soap.model.SoapBindingInfo;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.ClientLifeCycleListener;
import org.talend.esb.sam.agent.util.Converter;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.common.event.MessageInfo;
import org.talend.esb.sam.common.event.Originator;
import org.talend.esb.sam.common.service.MonitoringService;

/**
 * This ClientLifeCycleListener impl used to implement the feature of 
 * support web service start/stop event
 */
public class ClientListenerImpl implements ClientLifeCycleListener{
	private static Logger logger = Logger.getLogger(ClientListenerImpl.class.getName());

	private static String AGENT_PORT_TYPE = "{http://www.talend.org/esb/sam/MonitoringService/v1}MonitoringService";
	
	private boolean sendLifecycleEvent;
	private Queue<Event> queue;
	private MonitoringService monitoringServiceClient;
	
    public void setSendLifecycleEvent(boolean sendLifecycleEvent) {
        this.sendLifecycleEvent = sendLifecycleEvent;
    }

    public void setQueue(Queue<Event> queue) {
        this.queue = queue;
    }
    
    public void setMonitoringServiceClient(MonitoringService monitoringServiceClient) {
        this.monitoringServiceClient = monitoringServiceClient;
    }
    
	@Override
	public void clientCreated(Client client) {
	    if (!sendLifecycleEvent) {
	        return;
	    }
	    
		Event event = createEvent(client, EventTypeEnum.CLIENT_CREATE);
		if (null != event){
		    queue.add(event);
		}
/*		List<Event> eventList = new ArrayList<Event>();
		eventList.add(event);
		monitoringServiceClient.putEvents(eventList);
		logger.info("Send CLIENT_CREATE event to SAM Server successful!");*/
	}

	@Override
	public void clientDestroyed(Client client) {
        if (!sendLifecycleEvent) {
            return;
        }
	       
		Event event = createEvent(client, EventTypeEnum.CLIENT_DESTROY);
		if (null != event){
    		List<Event> eventList = new ArrayList<Event>();
    		eventList.add(event);
    		monitoringServiceClient.putEvents(eventList);
    		logger.info("Send CLIENT_DESTROY event to SAM Server successful!");
		}
	}

	private Event createEvent(Client client, EventTypeEnum type){
		String portType = client.getEndpoint().getBinding().getBindingInfo().getService().getInterface().getName().toString();
		//System.out.println("port_type: " + portType);
		
		//ignore the event that agent itself
		if (AGENT_PORT_TYPE.equals(portType)){
		    return null;
		}
		
		String transportType = null;
		if (client.getEndpoint().getBinding() instanceof SoapBinding){
			SoapBinding soapBinding = (SoapBinding)client.getEndpoint().getBinding();
            if (soapBinding.getBindingInfo() instanceof SoapBindingInfo) {
                SoapBindingInfo soapBindingInfo = (SoapBindingInfo)soapBinding.getBindingInfo();
                transportType = soapBindingInfo.getTransportURI();
                //System.out.println("transport_type: " + transportType);
            }    
		}
       
		String address = client.getEndpoint().getEndpointInfo().getAddress();
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
