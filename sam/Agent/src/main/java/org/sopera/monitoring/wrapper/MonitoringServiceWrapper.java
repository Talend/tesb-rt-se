package org.sopera.monitoring.wrapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

//import org.dozer.DozerBeanMapper;
import org.sopera.monitoring._2010._09.base.EventEnumType;
import org.sopera.monitoring._2010._09.base.TransportType;
import org.sopera.monitoring._2010._09.base.UuidType;
import org.sopera.monitoring._2010._09.common.CustomInfoType;
import org.sopera.monitoring._2010._09.common.EventType;
import org.sopera.monitoring._2010._09.common.EventInfoType;
import org.sopera.monitoring._2010._09.common.MessageInfoType;
import org.sopera.monitoring._2010._09.common.OriginatorType;
import org.sopera.monitoring.event.CustomInfo;
import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.event.MonitoringException;
import org.sopera.monitoring.monitoringservice.v1.MonitoringService;
import org.sopera.monitoring.monitoringservice.v1.PutEventsFault;

/**
 * Wraps business logic to web service logic. So web service should be
 * changeable.
 * 
 * @author cschmuelling
 * 
 */
public class MonitoringServiceWrapper implements org.sopera.monitoring.event.service.MonitoringService {
	// private static Logger logger = Logger
	// .getLogger(MonitoringServiceWrapper.class.getName());

	private MonitoringService monitoringService;
	//private DozerBeanMapper mapper;

	private int numberOfRetries;
	private long delayBetweenRetry;

	public void setNumberOfRetries(int numberOfRetries) {
		this.numberOfRetries = numberOfRetries;
	}

	public void setDelayBetweenRetry(long delayBetweenRetry) {
		this.delayBetweenRetry = delayBetweenRetry;
	}

	/**
	 * Set by Spring. Sets the web service implementation.
	 * 
	 * @param monitoringService
	 */
	public void setMonitoringService(MonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}

	/**
	 * Set by Spring. Sets the dozer mapper for transforming objects.
	 * 
	 * @param mapper
	 */
/*	public void setMapper(DozerBeanMapper mapper) {
		this.mapper = mapper;
	}*/

	/**
	 * Sends all events to the web service. Events will be transformed with
	 * mapper before sending.
	 */
	public void putEvents(List<Event> events) {
		try {
			List<EventType> eventTypes = new ArrayList<EventType>();
			for (Event event : events) {
				// If you want prevent null values.
				// checkEvent(event);
				//EventType eventType = (EventType) mapper.map(event,EventType.class);
				EventType eventType = convertEvent(event);
				eventTypes.add(eventType);
			}

			int i = 0;
			if (numberOfRetries == 0)
				numberOfRetries = 5;

			while (i < numberOfRetries) {
				try {
					monitoringService.putEvents(eventTypes);
					break;
				} catch (WebServiceException wse) {
					i++;
					if (delayBetweenRetry <= 0) {
						delayBetweenRetry = 1000;
					}
					try {
						Thread.sleep(delayBetweenRetry);
					} catch (InterruptedException e) {
						throw new MonitoringException(
								"1103",
								"Could not send events to monitoring service. Retry interrupted.",
								e, events);
					}
				}
			}
			if (i == numberOfRetries) {
				throw new MonitoringException("1104",
						"Could not send events to monitoring service after "
								+ numberOfRetries + " retries.", null, events);
			}
		} catch (PutEventsFault e) {
			MonitoringException me = new MonitoringException("1102",
					"Could not send events to monitoring service", e, events);
			throw me;
		}
	}
	
	/**
	 * convert Event bean to EventType manually
	 * @param event
	 * @return
	 */
	private EventType convertEvent(Event event){
		EventType eventType = new EventType();
		
		EventInfoType eiType = new EventInfoType();
		eiType.setTimestamp(convertDate(event.getEventInfo().getTimestamp()));
		eiType.setEventType(convertEventType(event.getEventInfo().getEventType()));
		
		OriginatorType origType = new OriginatorType();
		origType.setProcessId(event.getEventInfo().getOriginator().getProcessId());
		origType.setIp(event.getEventInfo().getOriginator().getIp());
		origType.setHostname(event.getEventInfo().getOriginator().getHostname());
		origType.setCustomId(event.getEventInfo().getOriginator().getCustomId());
		eiType.setOriginator(origType);
		eventType.setEventInfo(eiType);
		
		MessageInfoType miType = new MessageInfoType();
		UuidType messageId = new UuidType();
		messageId.setValue(event.getMessageInfo().getMessageId());
		miType.setMessageId(messageId);
		UuidType flowId = new UuidType();
		flowId.setValue(event.getMessageInfo().getFlowId());
		miType.setFlowId(flowId);
		miType.setPorttype(convertString(event.getMessageInfo().getPortType()));
		miType.setOperationName(event.getMessageInfo().getOperationName());
		TransportType tranType = new TransportType();
		tranType.setValue(event.getMessageInfo().getTransportType());
		miType.setTransport(tranType);
		eventType.setMessageInfo(miType);
		
		eventType.setContent(StringToXmlAnyConverter.convertToContentType(event.getContent()));
		eventType.setExtension(StringToXmlAnyConverter.convertToExtensionType(event.getExtension()));
		
		eventType.setCustomInfo(convertCustomInfo(event.getCustomInfo()));
		
		return eventType;
	}
	
	private CustomInfoType convertCustomInfo(CustomInfo cInfo){
		if (cInfo == null){
			return null;
		}
		
		CustomInfoType ciType = new CustomInfoType();
		
		Map<String,Object> prop = cInfo.getProperties();
		
		for (Map.Entry<String, Object> entry : prop.entrySet()){
			CustomInfoType.Item cItem = new CustomInfoType.Item();
			cItem.setKey(entry.getKey());
			cItem.setValue(entry.getValue());
			ciType.getItem().add(cItem);
		}
		
		return ciType;
	}
	
	private XMLGregorianCalendar convertDate(Date date){
		XMLGregorianCalendar gCal = null;
		try {
			gCal = DatatypeFactory.newInstance().newXMLGregorianCalendar();
		} catch (DatatypeConfigurationException ex) {
            ex.printStackTrace();
            return null;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		gCal.setYear(cal.get(Calendar.YEAR));
		gCal.setMonth(cal.get(Calendar.MONTH) +1);
		gCal.setDay(cal.get(Calendar.DAY_OF_MONTH));
		gCal.setHour(cal.get(Calendar.HOUR_OF_DAY));
		gCal.setMinute(cal.get(Calendar.MINUTE));
		gCal.setSecond(cal.get(Calendar.SECOND));
		gCal.setMillisecond(cal.get(Calendar.MILLISECOND));
		gCal.setTimezone(cal.get(Calendar.ZONE_OFFSET) / 60000 );
		
		return gCal;
		
	}
	
	private EventEnumType convertEventType(org.sopera.monitoring.event.EventType eventType){
		if (org.sopera.monitoring.event.EventType.REQ_IN.equals(eventType)) {
			return EventEnumType.REQ_IN;
		} else if (org.sopera.monitoring.event.EventType.REQ_OUT.equals(eventType)) {
			return EventEnumType.REQ_OUT;
		} else if (org.sopera.monitoring.event.EventType.RESP_IN.equals(eventType)) {
			return EventEnumType.RESP_IN;
		} else if (org.sopera.monitoring.event.EventType.RESP_OUT.equals(eventType)) {
			return EventEnumType.RESP_OUT;
		} else if (org.sopera.monitoring.event.EventType.FAULT_IN.equals(eventType)) {
			return EventEnumType.FAULT_IN;
		} else if (org.sopera.monitoring.event.EventType.FAULT_OUT.equals(eventType)) {
			return EventEnumType.FAULT_OUT;
		}else{
			return null;
		}
	}

	private QName convertString(String str){
		if (str != null){
			return QName.valueOf(str);
		}else{
			return null;
		}
	}
}
