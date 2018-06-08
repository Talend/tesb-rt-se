package org.talend.esb.sam.agent.eventadmin;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.EventAdmin;
import org.talend.esb.sam.agent.eventadmin.translator.SamEventTranslator;
import org.talend.esb.sam.common.event.Event;

public class EventAdminPublisher {

	public static final String TOPIC = "org/talend/esb/sam/events";

	private static EventAdmin eventAdmin = null;

	static {
		BundleContext context = FrameworkUtil.getBundle(EventAdminPublisher.class).getBundleContext();
		if (context != null) {
			ServiceReference<?> ref = context.getServiceReference(EventAdmin.class.getName());
			if (ref != null) {
				eventAdmin = (EventAdmin) context.getService(ref);
			}
		}
	}

	public static void publish(List<Event> samEvents) throws Exception {
		if (eventAdmin != null) {
			for (Event samEvent : samEvents) {
				eventAdmin.sendEvent(SamEventTranslator.translate(samEvent, TOPIC));
			}
		}
	}
}
