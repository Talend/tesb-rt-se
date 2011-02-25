package org.sopera.monitoring.filter.impl;

import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.filter.EventFilter;

public class MetadataFilter<E extends Event> implements EventFilter<E> {

	private String hostname;
	private String ip;
	private String operationName;
	private String portType;
	private String transportType;
	private boolean andCondition = true;

	public boolean filter(E event) {

		Boolean hostFilter = hostname != null ? Boolean.FALSE : null;
		Boolean ipFilter = ip != null ? Boolean.FALSE : null;
		Boolean operationFilter = operationName != null ? Boolean.FALSE : null;
		Boolean portFilter = portType != null ? Boolean.FALSE : null;
		Boolean transportFilter = transportType != null ? Boolean.FALSE : null;

		if (hostname != null
				&& event.getEventInfo().getOriginator().getHostname() != null
				&& event.getEventInfo().getOriginator().getHostname()
						.equals(hostname)) {
			if (!andCondition)
				return true;
			hostFilter = Boolean.TRUE;
		}
		if (ip != null && event.getEventInfo().getOriginator().getIp() != null
				&& event.getEventInfo().getOriginator().getIp().equals(ip)) {
			if (!andCondition)
				return true;
			ipFilter = Boolean.TRUE;
		}

		if (operationName != null
				&& event.getMessageInfo().getOperationName() != null
				&& event.getMessageInfo().getOperationName()
						.equals(operationName)) {
			if (!andCondition)
				return true;
			operationFilter = Boolean.TRUE;
		}
		if (portType != null && event.getMessageInfo().getPortType() != null
				&& event.getMessageInfo().getPortType().equals(portType)) {
			if (!andCondition)
				return true;
			portFilter = Boolean.TRUE;
		}
		if (transportType != null
				&& event.getMessageInfo().getTransportType() != null
				&& event.getMessageInfo().getTransportType()
						.equals(transportType)) {
			if (!andCondition)
				return true;
			transportFilter = Boolean.TRUE;
		}

		if (andCondition) {
			return ((hostFilter == null || Boolean.TRUE.equals(hostFilter))
					&& (ipFilter == null || Boolean.TRUE.equals(ipFilter))
					&& (operationFilter == null || Boolean.TRUE
							.equals(operationFilter))
					&& (portFilter == null || Boolean.TRUE.equals(portFilter)) && (transportFilter == null || Boolean.TRUE
					.equals(transportFilter)));
		} else {
			return false;
		}
	}

	public void setHostname(String hostname) {
		if ("".equals(hostname))
			this.hostname = null;
		else
			this.hostname = hostname;
	}

	public void setIp(String ip) {
		if ("".equals(ip))
			this.ip = null;
		else
			this.ip = ip;
	}

	public void setOperationName(String operationName) {
		if ("".equals(operationName))
			this.operationName = null;
		else
			this.operationName = operationName;
	}

	public void setPortType(String portType) {
		if ("".equals(portType))
			this.portType = null;
		else
			this.portType = portType;
	}

	public void setTransportType(String transportType) {
		if ("".equals(transportType))
			this.transportType = null;
		else
			this.transportType = transportType;
	}

	public void setAndCondition(boolean andCondition) {
		this.andCondition = andCondition;
	}

}
