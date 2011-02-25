package org.sopera.monitoring.interceptor.transport;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.message.Message;
import org.sopera.monitoring.event.MessageContextCodec;


public class HttpMessageContextCodec implements MessageContextCodec {
	
	public static final String FLOWID_HTTP_HEADER_NAME = "flowid";
	
	protected static Logger logger = Logger.getLogger(HttpMessageContextCodec.class.getName());
	
	public HttpMessageContextCodec() {
	}
	
	public String readFlowId(Message message) {
		
		String flowId = null;
		//Map<String, List<String>> headers = (Map<String, List<String>>)message.get(Message.PROTOCOL_HEADERS);
		Map<String, List<String>> headers = CastUtils.cast((Map<?, ?>)message.get(Message.PROTOCOL_HEADERS));
		if (headers != null) {
			List<String> flowIds = headers.get(FLOWID_HTTP_HEADER_NAME);
			if (flowIds != null && flowIds.size() > 0) {
				flowId = flowIds.get(0);
				logger.info("HTTP header '" + FLOWID_HTTP_HEADER_NAME + "' found: " + flowId);
			}
			else {
				logger.fine("No HTTP header '" + FLOWID_HTTP_HEADER_NAME + "' found");
			}
		}
		return flowId;
	}

	public void writeFlowId(Message message, String flowId) {
		// 
		//Map<String, List<String>> headers = (Map<String, List<String>>)message.get(Message.PROTOCOL_HEADERS);
		Map<String, List<String>> headers = CastUtils.cast((Map<?, ?>)message.get(Message.PROTOCOL_HEADERS));
		if (headers != null) {
			headers.put(FLOWID_HTTP_HEADER_NAME, Arrays.asList(new String[]{flowId}));
			logger.info("HTTP header '" + FLOWID_HTTP_HEADER_NAME + "' set to: " + flowId);
		}
	}

	

}
