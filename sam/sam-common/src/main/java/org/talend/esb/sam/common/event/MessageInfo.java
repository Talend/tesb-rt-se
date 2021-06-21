/*
 * #%L
 * Service Activity Monitoring :: Common
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

//@Embeddable
/**
 * The Class MessageInfo using to define information related to message.
 */
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

    /**
     * Instantiates a new message info.
     *
     * @param messageId the message id
     * @param flowId the flow id
     * @param portType the port type
     * @param operationName the operation name
     * @param transportType the transport type
     */
    public MessageInfo(String messageId, String flowId, String portType,
            String operationName, String transportType) {
        super();
        this.messageId = messageId;
        this.flowId = flowId;
        this.portType = portType;
        this.operationName = operationName;
        this.transportType = transportType;
    }

    /**
     * Instantiates a new message info.
     */
    public MessageInfo() {
        super();
    }

    /**
     * Gets the message id.
     *
     * @return the message id
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Sets the message id.
     *
     * @param messageId the new message id
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * Gets the flow id.
     *
     * @return the flow id
     */
    public String getFlowId() {
        return flowId;
    }

    /**
     * Sets the flow id.
     *
     * @param flowId the new flow id
     */
    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    /**
     * Gets the port type.
     *
     * @return the port type
     */
    public String getPortType() {
        return portType;
    }

    /**
     * Sets the port type.
     *
     * @param portType the new port type
     */
    public void setPortType(String portType) {
        this.portType = portType;
    }

    /**
     * Gets the operation name.
     *
     * @return the operation name
     */
    public String getOperationName() {
        return operationName;
    }

    /**
     * Sets the operation name.
     *
     * @param operationName the new operation name
     */
    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    /**
     * Gets the transport type.
     *
     * @return the transport type
     */
    public String getTransportType() {
        return transportType;
    }

    /**
     * Sets the transport type.
     *
     * @param transportType the new transport type
     */
    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }



    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
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

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
            if (other.flowId != null) {
                return false;
            }
        } else if (!flowId.equals(other.flowId)) {
            return false;
        }
        if (messageId == null) {
            if (other.messageId != null) {
                return false;
            }
        } else if (!messageId.equals(other.messageId)) {
            return false;
        }
        if (operationName == null) {
            if (other.operationName != null) {
                return false;
            }
        } else if (!operationName.equals(other.operationName)) {
            return false;
        }
        if (portType == null) {
            if (other.portType != null) {
                return false;
            }
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }
}
