/*
 * #%L
 * Service Activity Monitoring :: Common
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
package org.talend.esb.sam.common.filter.impl;

import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.spi.EventFilter;

public class MetadataFilter implements EventFilter {

	private String hostname;
	private String ip;
	private String operationName;
	private String portType;
	private String transportType;
	private boolean andCondition = true;

	public boolean filter(Event event) {

		Boolean hostFilter = hostname != null ? Boolean.FALSE : null;
		Boolean ipFilter = ip != null ? Boolean.FALSE : null;
		Boolean operationFilter = operationName != null ? Boolean.FALSE : null;
		Boolean portFilter = portType != null ? Boolean.FALSE : null;
		Boolean transportFilter = transportType != null ? Boolean.FALSE : null;

		if (hostname != null
				&& event.getOriginator().getHostname() != null
				&& event.getOriginator().getHostname()
						.equals(hostname)) {
			if (!andCondition)
				return true;
			hostFilter = Boolean.TRUE;
		}
		if (ip != null && event.getOriginator().getIp() != null
				&& event.getOriginator().getIp().equals(ip)) {
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
