/*
 * #%L
 * Service Activity Monitoring :: Agent
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
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

package org.talend.esb.sam.agent.eventadmin.translator;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.talend.esb.sam.agent.eventadmin.translator.subject.SamlTokenSubjectExtractor;
import org.talend.esb.sam.agent.eventadmin.translator.subject.SubjectExtractor;
import org.talend.esb.sam.agent.eventadmin.translator.subject.UsernameTokenSubjectExtractor;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.event.EventTypeEnum;

public class SamEventTranslator {
	
	public static final String SAM_EVENT_TYPE = "SAMEvent";

	public static final String ID = "id";

	public static final String EVENT_UUID = "eventUUID";

	public static final String CORRELATION_ID = "correlationId";

	public static final String CATEGORY = "category";

	public static final String EVENT_TYPE = "eventType";

	public static final String SEVERITY = "severity";

	public static final String LOG_MESSAGE = "logMessage";

	public static final String LOG_SOURCE = "logSource";

	public static final String SIGNED_LOG_MESSAGE = "signedLogMessage";

	public static final String LOG_TIMESTAMP = "logTimestamp";

	public static final String AGENT_ID = "agentId";

	public static final String AGENT_TIMESTAMP = "agentTimestamp";

	public static final String SERVER_TIMESTAMP = "serverTimestamp";

	public static final String AUDIT = "audit";

	public static final String AUDIT_SEQUENCE_NO = "auditSequenceNo";

	public static final String SUBJECT = "subject";

	public static final String CUSTOM_INFO = "customInfo";

	/* Subject */
	/** Identifier for hostname within log source attribute */
	public static final String LS_HOSTNAME = "host.name";
	/** Identifier for processId within log source attribute */
	public static final String LS_PROCESS_ID = "process.id";
	public static final String LS_HOST_IP = "host.ip";

	/* Custom Info */
	public static final String PORT_TYPE = "port.type";

	public static final String TRANSPORT_TYPE = "transport.type";

	public static final String OPERATION_NAME = "operation.name";

	public static final String MESSAGE_ID = "message.id";

	public static final String FLOW_ID = "flow.id";
	
	public static final String EVENT_TYPE_CUSTOM = "event.type";

	public static final String CONTENT_CUT = "content.cut";

	public SamEventTranslator() {
		super();
	}

	protected static String getEventType() {
		return "SAMEvent";
	}

	protected static Boolean getAudit() {
		// not implemented
		return Boolean.FALSE;
	}

	protected static String getAuditSequenceNo() {
		// not implemented
		return "";
	}

	protected static String getCategory() {
		// not implemented
		return "";
	}

	protected static String getSignedLogMessage(Event samEvent) {
		// not implemented
		return "";
	}

	protected static String getAgentID(Event samEvent) {
		// not implemented
		return "";
	}
	
	protected static boolean isUsernameTokenMappingEnabled() {
		return false;
	}
	
	protected static boolean isSamlTokenMappingEnabled() {
		return false;
	}

	protected static Map<String, String> getCustomInfo(Event samEvent) {
		Map<String, String> customInfo = new HashMap<String, String>();
		customInfo.put(MESSAGE_ID, samEvent.getMessageInfo().getMessageId());
		customInfo.put(FLOW_ID, samEvent.getMessageInfo().getFlowId());
		customInfo.put(OPERATION_NAME, samEvent.getMessageInfo().getOperationName());
		customInfo.put(TRANSPORT_TYPE, samEvent.getMessageInfo().getTransportType());
		customInfo.put(PORT_TYPE, samEvent.getMessageInfo().getPortType());
		customInfo.put(CONTENT_CUT, Boolean.toString(samEvent.isContentCut()));
		customInfo.put(EVENT_TYPE_CUSTOM, samEvent.getEventType().toString());
		

		Map<String, String> samCustomInfo = samEvent.getCustomInfo();
		if (null != samCustomInfo) {
			customInfo.putAll(samCustomInfo);
			customInfo.remove(CORRELATION_ID);
		}

		return customInfo;
	}

	protected static String getEventUUID(Event samEvent) throws Exception {
		return UUID.randomUUID().toString();
	}

	public static org.osgi.service.event.Event translate(Event samEvent, String topic) throws Exception {

		Map<String, Object> m = new HashMap<String, Object>();
		if (samEvent != null && samEvent.getMessageInfo() != null) {
			m.put(MESSAGE_ID, samEvent.getMessageInfo().getMessageId());
			m.put(EVENT_UUID, getEventUUID(samEvent));
			m.put(CORRELATION_ID, getCorrelationID(samEvent));
			m.put(CATEGORY, getCategory());
			m.put(EVENT_TYPE, SAM_EVENT_TYPE);
			m.put(SEVERITY, getSeverity(samEvent.getEventType()));
			m.put(LOG_MESSAGE, getLogMessage(samEvent));
			m.put(LOG_SOURCE, getLogSource(samEvent));
			m.put(SIGNED_LOG_MESSAGE, getSignedLogMessage(samEvent));
			m.put(LOG_TIMESTAMP, getEventTimestamp(samEvent));
			m.put(AGENT_ID, getAgentID(samEvent));
			m.put(AGENT_TIMESTAMP, getEventTimestamp(samEvent));
			m.put(SERVER_TIMESTAMP, getEventTimestamp(samEvent));
			m.put(AUDIT, getAudit());
			m.put(AUDIT_SEQUENCE_NO, getAuditSequenceNo());
			m.put(SUBJECT, getSubject(samEvent));
			m.put(CUSTOM_INFO, getCustomInfo(samEvent));
		}



		return new org.osgi.service.event.Event(topic, m);

	}

	private static String getSeverity(EventTypeEnum eventType) {
		String severity;
		switch (eventType) {
		case FAULT_IN:
		case FAULT_OUT:
			severity = "ERROR";
			break;
		case UNKNOWN:
			severity = "WARN";
			break;
		default:
			severity = "INFO";
			break;
		}
		return severity;
	}

	protected static Map<String, String> getLogSource(Event inputEvent) throws Exception {
		Map<String, String> source = new HashMap<String, String>();
		if (inputEvent != null && inputEvent.getOriginator() != null) {
			source.put(LS_HOSTNAME, inputEvent.getOriginator().getHostname());
			source.put(LS_HOST_IP, inputEvent.getOriginator().getIp());
			source.put(LS_PROCESS_ID, inputEvent.getOriginator().getProcessId());
		}
		return source;
	}

	protected static String getLogMessage(Event samEvent) throws Exception {
		return samEvent.getContent();
	}

	protected static String getCorrelationID(Event inputEvent) throws Exception {
		if (inputEvent == null || inputEvent.getCustomInfo() == null) {
			return null;
		}
		return inputEvent.getCustomInfo().get(CORRELATION_ID);
	}

	protected static Long getEventTimestamp(Event inputEvent) throws Exception {
		if (inputEvent == null || inputEvent.getTimestamp() == null) {
			return new Date().getTime();
		}
		return inputEvent.getTimestamp().getTime();
	}

	protected static String getSubject(Event samEvent) throws Exception {
		String msgContent = samEvent.getContent();

		if (msgContent == null || msgContent.trim().isEmpty()) {
			return null;
		}

		SubjectExtractor subjectExtractor = new SubjectExtractor();

		String answer = null;

		if (answer == null && samEvent.getOriginator() != null) {
			answer = samEvent.getOriginator().getPrincipal();
		}
		
		if (answer == null && isUsernameTokenMappingEnabled()) {
			answer = subjectExtractor.getSubject(msgContent, new UsernameTokenSubjectExtractor());
		}

		if (answer == null && isSamlTokenMappingEnabled()) {
			subjectExtractor.getSubject(msgContent, new SamlTokenSubjectExtractor());
		}
		
		

		return answer;
	}

}