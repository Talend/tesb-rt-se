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

package org.talend.esb.sam.agent.eventadmin;

import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.EventAdmin;
import org.talend.esb.sam.agent.eventadmin.translator.SamEventTranslator;
import org.talend.esb.sam.common.event.Event;

public class EventAdminPublisher {

	public static final String TOPIC = "org/talend/esb/sam/events";

	private static EventAdmin eventAdmin = getEventAdmin(EventAdmin.class);

	private static boolean isOSGiDeployment() {
        Bundle b = FrameworkUtil.getBundle(EventAdminPublisher.class);
        return (b != null);
    }
	
	private static EventAdmin getEventAdmin(Class<?> serviceClass) {
        if (!isOSGiDeployment()) {
            return null;
        }

        BundleContext context = FrameworkUtil.getBundle(EventAdminPublisher.class).getBundleContext();
        if (context != null) {
			ServiceReference<?> ref = context.getServiceReference(EventAdmin.class.getName());
			if (ref != null) {
				return (EventAdmin) context.getService(ref);
			}
		}

        return null;
    }

	public static void publish(List<Event> samEvents) throws Exception {
		if (eventAdmin != null) {
			for (Event samEvent : samEvents) {
				eventAdmin.postEvent(SamEventTranslator.translate(samEvent, TOPIC));
			}
		}
	}
	
	
}
