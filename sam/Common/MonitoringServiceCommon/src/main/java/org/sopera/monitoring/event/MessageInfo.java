package org.sopera.monitoring.event;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class MessageInfo implements Serializable{

	@Transient
	private static final long serialVersionUID = -6464068913564098842L;

	@Basic(optional=false)
	@Column(name="MI_MESSAGE_ID")
	private String messageId;
	@Basic(optional=false)
	@Column(name="MI_FLOW_ID",length=64)
	private String flowId;
	@Basic(optional=false)
	@Column(name="MI_PORT_TYPE")
	private String portType;
	@Basic(optional=false)
	@Column(name="MI_OPERATION_NAME")
	private String operationName;
	@Basic(optional=false)
	@Column(name="MI_TRANSPORT_TYPE")
	private String transportType;
	
	public MessageInfo(String messageId, String flowId, String portType,
			String operationName, String transportType) {
		super();
		this.messageId = messageId;
		this.flowId = flowId;
		this.portType = portType;
		this.operationName = operationName;
		this.transportType = transportType;
	}
	
	public MessageInfo() {
		super();
	}

	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public String getFlowId() {
		return flowId;
	}
	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}
	public String getPortType() {
		return portType;
	}
	public void setPortType(String portType) {
		this.portType = portType;
	}
	public String getOperationName() {
		return operationName;
	}
	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}
	public String getTransportType() {
		return transportType;
	}
	public void setTransportType(String transportType) {
		this.transportType = transportType;
	}
}
