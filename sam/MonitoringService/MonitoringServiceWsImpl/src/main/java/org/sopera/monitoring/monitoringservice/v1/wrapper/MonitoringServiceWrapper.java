package org.sopera.monitoring.monitoringservice.v1.wrapper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dozer.DozerBeanMapper;
import org.sopera.monitoring._2010._09.common.EventType;
import org.sopera.monitoring._2010._09.fault.FaultType;
import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.exception.MonitoringException;
import org.sopera.monitoring.monitoringservice.v1.MonitoringService;
import org.sopera.monitoring.monitoringservice.v1.PutEventsFault;

public class MonitoringServiceWrapper implements MonitoringService {

	private static Logger logger = Logger
			.getLogger(MonitoringServiceWrapper.class.getName());

	@SuppressWarnings("rawtypes")
	private org.sopera.monitoring.service.MonitoringService monitoringService;
	private DozerBeanMapper mapper;
	private String targetClassName;

	@SuppressWarnings("unchecked")
	public String putEvents(List<EventType> eventTypes) throws PutEventsFault {

		logger.info("Received Events");

		logger.fine("MonitoringService: "
				+ monitoringService.getClass().getName());
		logger.fine("DozerBeanMapper: " + mapper.getClass().getName());
		logger.fine("Map web service event to " + targetClassName);

		List<Event> events = new ArrayList<Event>();

		try {
			for (EventType eventType : eventTypes) {
				// Use dozer mapping for mapping Web Service Event to business
				// logic event.
				events.add(((Event) mapper.map(eventType, Event.class)));
			}
		} catch (RuntimeException e) {
			throwFault("004", "Could not map web service data to event.", e);
		}

		try {
			// Call Business logic
			monitoringService.putEvents(events);
		} catch (MonitoringException e) {
			e.logException(Level.SEVERE);
			throwFault(e.getCode(), e.getMessage(), e);
		} catch (Throwable t) {
			throwFault("000", "Unknown error", t);
		}

		return "success";
	}

	private static void throwFault(String code, String message, Throwable t)
			throws PutEventsFault {
		logger.severe("Throw Fault " + code + " " + message);

		FaultType faultType = new FaultType();
		faultType.setFaultCode(code);
		faultType.setFaultMessage(message);

		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		t.printStackTrace(printWriter);
		String exception = stringWriter.toString();

		faultType.setStackTrace(exception);

		logger.log(Level.SEVERE, "Exception", t);

		throw new PutEventsFault(message, faultType);
	}

	@SuppressWarnings("rawtypes")
	public org.sopera.monitoring.service.MonitoringService getMonitoringService() {
		return monitoringService;
	}

	@SuppressWarnings("rawtypes")
	public void setMonitoringService(
			org.sopera.monitoring.service.MonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}

	public DozerBeanMapper getMapper() {
		return mapper;
	}

	public void setMapper(DozerBeanMapper mapper) {
		this.mapper = mapper;
	}

	public String getTargetClassName() {
		return targetClassName;
	}

	public void setTargetClassName(String targetClassName) {
		this.targetClassName = targetClassName;
	}
}
