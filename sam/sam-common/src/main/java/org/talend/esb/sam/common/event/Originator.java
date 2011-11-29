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

    public Originator(String processId, String ip, String hostname,
            String customId, String principal) {
        super();
        this.processId = processId;
        this.ip = ip;
        this.hostname = hostname;
        this.customId = customId;
        this.principal = principal;
    }

    public Originator() {
        super();
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getCustomId() {
        return customId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Originator other = (Originator)obj;
        if (customId == null) {
            if (other.customId != null)
                return false;
        } else if (!customId.equals(other.customId))
            return false;
        if (hostname == null) {
            if (other.hostname != null)
                return false;
        } else if (!hostname.equals(other.hostname))
            return false;
        if (ip == null) {
            if (other.ip != null)
                return false;
        } else if (!ip.equals(other.ip))
            return false;
        if (principal == null) {
            if (other.principal != null)
                return false;
        } else if (!principal.equals(other.principal))
            return false;
        if (processId == null) {
            if (other.processId != null)
                return false;
        } else if (!processId.equals(other.processId))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }
}
