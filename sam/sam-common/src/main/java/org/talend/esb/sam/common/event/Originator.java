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
 * The Class Originator.
 */
public class Originator implements Serializable {

    //@Transient
    private static final long serialVersionUID = 3926684116318585338L;

    //@Basic(optional = false)
    //@Column(name = "ORIG_PROCESS_ID")
    private String processId;
    //@Basic(optional = false)
    //@Column(name = "ORIG_IP", length=64)
    private String ip;
    //@Basic(optional = false)
    //@Column(name = "ORIG_HOSTENAME", length=128)
    private String hostname;
    //@Column(name = "ORIG_CUSTOM_ID")
    private String customId;
    //@Column(name = "ORIG_PRINCIPAL")
    private String principal;

    /**
     * Instantiates a new originator.
     *
     * @param processId the process id
     * @param ip the ip address
     * @param hostname the hostname
     * @param customId the custom id
     * @param principal the principal
     */
    public Originator(String processId, String ip, String hostname,
            String customId, String principal) {
        super();
        this.processId = processId;
        this.ip = ip;
        this.hostname = hostname;
        this.customId = customId;
        this.principal = principal;
    }

    /**
     * Instantiates a new originator.
     */
    public Originator() {
        super();
    }

    /**
     * Gets the process id.
     *
     * @return the process id
     */
    public String getProcessId() {
        return processId;
    }

    /**
     * Sets the process id.
     *
     * @param processId the new process id
     */
    public void setProcessId(String processId) {
        this.processId = processId;
    }

    /**
     * Gets the ip address.
     *
     * @return the ip address
     */
    public String getIp() {
        return ip;
    }

    /**
     * Sets the ip address.
     *
     * @param ip the new ip address
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Gets the hostname.
     *
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Sets the hostname.
     *
     * @param hostname the new hostname
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * Gets the custom id.
     *
     * @return the custom id
     */
    public String getCustomId() {
        return customId;
    }

    /**
     * Sets the custom id.
     *
     * @param customId the new custom id
     */
    public void setCustomId(String customId) {
        this.customId = customId;
    }

    /**
     * Gets the principal.
     *
     * @return the principal
     */
    public String getPrincipal() {
        return principal;
    }

    /**
     * Sets the principal.
     *
     * @param principal the new principal
     */
    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((customId == null) ? 0 : customId.hashCode());
        result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        result = prime * result + ((principal == null) ? 0 : principal.hashCode());
        result = prime * result + ((processId == null) ? 0 : processId.hashCode());
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
        Originator other = (Originator)obj;
        if (customId == null) {
            if (other.customId != null) {
                return false;
            }
        } else if (!customId.equals(other.customId)) {
            return false;
        }
        if (hostname == null) {
            if (other.hostname != null) {
                return false;
            }
        } else if (!hostname.equals(other.hostname)) {
            return false;
        }
        if (ip == null) {
            if (other.ip != null) {
                return false;
            }
        } else if (!ip.equals(other.ip)) {
            return false;
        }
        if (principal == null) {
            if (other.principal != null) {
                return false;
            }
        } else if (!principal.equals(other.principal)) {
            return false;
        }
        if (processId == null) {
            if (other.processId != null) {
                return false;
            }
        } else if (!processId.equals(other.processId)) {
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
