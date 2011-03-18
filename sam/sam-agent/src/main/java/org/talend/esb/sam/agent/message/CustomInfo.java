package org.talend.esb.sam.agent.message;

import java.util.HashMap;

import org.apache.cxf.message.Message;

/**
 * Customer interceptors should store the data that should go into the monitoring event
 * in this map. All key/value pairs will be automatically copied to the customInfo map of the Event
 */
public class CustomInfo extends HashMap<String, String> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3011186676959189278L;

	/**
	 * Access the customInfo of a message using this accessor. The CustomInfo
	 * map will be automatically created and stored in the event if it is not yet present
	 * 
	 * @param message
	 * @return
	 */
	public static CustomInfo getOrCreateCustomInfo(Message message) {
		CustomInfo customInfo = message.get(CustomInfo.class);
		if (customInfo == null) {
			customInfo = new CustomInfo();
			message.put(CustomInfo.class, customInfo);
		}
		return customInfo;
	}

}
