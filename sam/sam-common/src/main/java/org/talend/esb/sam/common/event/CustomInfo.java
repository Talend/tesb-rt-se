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

/**
 * this Bean is used for store custom key/value info into Database
 * @author Xilai Dai
 *
 */
public class CustomInfo {

	private Long persistedId;
	
	private String custKey;
	
	private Object custValue;
	
	public CustomInfo(){
		super();
	}

	public Long getPersistedId() {
		return persistedId;
	}

	public void setPersistedId(Long persistedId) {
		this.persistedId = persistedId;
	}

    public String getCustKey() {
		return custKey;
	}

	public void setCustKey(String custKey) {
		this.custKey = custKey;
	}

	public Object getCustValue() {
		return custValue;
	}

	public void setCustValue(Object custValue) {
		this.custValue = custValue;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((persistedId == null) ? 0 : persistedId.hashCode());
        result = prime * result + ((custKey == null) ? 0 : custKey.hashCode());
        result = prime * result + ((custValue == null) ? 0 : custValue.hashCode());
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
        CustomInfo other = (CustomInfo)obj;
        if (persistedId == null) {
            if (other.persistedId != null)
                return false;
        } else if (!persistedId.equals(other.persistedId))
            return false;
        if (custKey == null) {
            if (other.custKey != null)
                return false;
        } else if (!custKey.equals(other.custKey))
            return false;
        if (custValue == null) {
            if (other.custValue != null)
                return false;
        } else if (!custValue.equals(other.custValue))
            return false;        
        return true;
    }
	
	
}
