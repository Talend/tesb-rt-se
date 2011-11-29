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
package org.talend.esb.sam.common.event;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

//@Embeddable
public class MessageInfo implements Serializable {

    //@Transient
    private static final long serialVersionUID = -6464068913564098842L;

    //@Basic(optional=false)
    //@Column(name="MI_MESSAGE_ID")
    private String messageId;
    //@Basic(optional=false)
    //@Column(name="MI_FLOW_ID",length=64)
    private String flowId;
    //@Basic(optional=false)
    //@Column(name="MI_PORT_TYPE")
    private String portType;
    //@Basic(optional=false)
    ///@Column(name="MI_OPERATION_NAME")
    private String operationName;
    //@Basic(optional=false)
    //@Column(name="MI_TRANSPORT_TYPE")
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


    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((flowId == null) ? 0 : flowId.hashCode());
        result = prime * result + ((messageId == null) ? 0 : messageId.hashCode());
        result = prime * result + ((operationName == null) ? 0 : operationName.hashCode());
        result = prime * result + ((portType == null) ? 0 : portType.hashCode());
        result = prime * result + ((transportType == null) ? 0 : transportType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MessageInfo other = (MessageInfo)obj;
        if (flowId == null) {
            if (other.flowId != null)
                return false;
        } else if (!flowId.equals(other.flowId))
            return false;
        if (messageId == null) {
            if (other.messageId != null)
                return false;
        } else if (!messageId.equals(other.messageId))
            return false;
        if (operationName == null) {
            if (other.operationName != null)
                return false;
        } else if (!operationName.equals(other.operationName))
            return false;
        if (portType == null) {
            if (other.portType != null)
                return false;
        } else if (!portType.equals(other.portType)) {
            return false;
        }
        if (transportType == null) {
            if (other.transportType != null) {
                return false;
            }
        } else if (!transportType.equals(other.transportType)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }
}
