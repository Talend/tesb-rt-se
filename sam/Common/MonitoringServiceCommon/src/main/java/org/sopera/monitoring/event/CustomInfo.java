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
	
}
