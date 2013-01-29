package org.talend.esb.sam.service;

import javax.xml.bind.annotation.XmlRootElement;
import org.talend.esb.sam.common.event.EventTypeEnum;

@XmlRootElement
public class Flow {

    private String flowID;
    
    private long timeStamp;
    
    private EventTypeEnum eventType;
    
    private String host;
    
    private String ip;
    
    private String port;
    
    private String operation;
    
    private String transport;
    
	public void setflowID(String flowID) {
		this.flowID = flowID;
	}
	public String getflowID() {
		return flowID;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getHost() {
		return host;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getIp() {
		return ip;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getPort() {
		return port;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getOperation() {
		return operation;
	}
	public void setTransport(String transport) {
		this.transport = transport;
	}
	public String getTransport() {
		return transport;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setEventType(EventTypeEnum eventType) {
		this.eventType = eventType;
	}
	public EventTypeEnum getEventType() {
		return eventType;
	}

}
