package org.sopera.monitoring.event;

import java.util.HashMap;
import java.util.Map;

/**
 * this Bean is used for store custom key/value info into Database
 * @author Xilai Dai
 *
 */
public class CustomInfo {

	private Long persistedId;
	
	private Map<String,Object> properties = new HashMap<String,Object>();
	
	public CustomInfo(){
		super();
	}

	public Long getPersistedId() {
		return persistedId;
	}

	public void setPersistedId(Long persistedId) {
		this.persistedId = persistedId;
	}

	public Map<String,Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String,Object> properties) {
		this.properties = properties;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((persistedId == null) ? 0 : persistedId.hashCode());
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
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
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
            return false;
        return true;
    }
	
	
}
