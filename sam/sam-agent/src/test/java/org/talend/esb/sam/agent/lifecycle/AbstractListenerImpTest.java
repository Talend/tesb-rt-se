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
package org.talend.esb.sam.agent.lifecycle;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapBinding;
import org.apache.cxf.binding.soap.model.SoapBindingInfo;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.easymock.EasyMock;
import org.junit.Test;
import org.talend.esb.sam.agent.queue.EventQueue;
import org.talend.esb.sam.common.event.EventTypeEnum;
import org.talend.esb.sam.common.service.MonitoringService;

public class AbstractListenerImpTest {

	static {
		Logger.getLogger(AbstractListenerImpl.class.getName()).setLevel(Level.FINEST);
	}

	private EventQueue QUEUE = new EventQueue();
	private AbstractListenerImpl l = new AbstractListenerImpl();
	
	@Test
	public void testAbstractListenerImp() throws IOException {
		l.setSendLifecycleEvent(true);
		l.setQueue(QUEUE);
		MonitoringService monitoringServiceClient = EasyMock.createMock(MonitoringService.class);
		l.setMonitoringServiceClient(monitoringServiceClient);
		
		Endpoint endpoint = getEndpoint();
		l.processStart(endpoint, EventTypeEnum.JOB_START);
	}

	@Test
	public void testStartWithoutEvents() throws IOException {

		AbstractListenerImpl l = new AbstractListenerImpl();
		l.setSendLifecycleEvent(false);

		Endpoint endpoint = EasyMock.createMock(Endpoint.class);
		l.processStart(endpoint, EventTypeEnum.JOB_START);
	}
	
	@Test
	public void testStopAbstractListenerImp() throws IOException {
		l.setSendLifecycleEvent(true);
		l.setQueue(QUEUE);
		MonitoringService monitoringServiceClient = EasyMock.createMock(MonitoringService.class);
		l.setMonitoringServiceClient(monitoringServiceClient);
		
		Endpoint endpoint = getEndpoint();
		l.processStop(endpoint, EventTypeEnum.JOB_START);
	}
	
	@Test
	public void testStopWithoutEvents() throws IOException {

		AbstractListenerImpl l = new AbstractListenerImpl();
		l.setSendLifecycleEvent(false);

		Endpoint endpoint = EasyMock.createMock(Endpoint.class);
		l.processStop(endpoint, EventTypeEnum.JOB_START);
	}
	
	private Endpoint getEndpoint() {

		InterfaceInfo interfaceInfo = EasyMock.createMock(InterfaceInfo.class);
		EasyMock.expect(interfaceInfo.getName()).andReturn(new QName("ServiceName")).anyTimes();
		EasyMock.replay(interfaceInfo);

		ServiceInfo service = EasyMock.createMock(ServiceInfo.class);
		EasyMock.expect(service.getInterface()).andReturn(interfaceInfo).anyTimes();
		EasyMock.replay(service);

		SoapBindingInfo bindingInfo = EasyMock.createMock(SoapBindingInfo.class);
		EasyMock.expect(bindingInfo.getService()).andReturn(service).anyTimes();
		EasyMock.expect(bindingInfo.getTransportURI()).andReturn("http://").anyTimes();
		EasyMock.replay(bindingInfo);

		SoapBinding binding = EasyMock.createMock(SoapBinding.class);
		EasyMock.expect(binding.getBindingInfo()).andReturn(bindingInfo).anyTimes();
		EasyMock.replay(binding);

		EndpointInfo endpointInfo = EasyMock.createMock(EndpointInfo.class);
		EasyMock.expect(endpointInfo.getAddress()).andReturn("http://localhost:8090").anyTimes();
		EasyMock.replay(endpointInfo);

		Endpoint endpoint = EasyMock.createMock(Endpoint.class);
		EasyMock.expect(endpoint.getBinding()).andReturn(binding).anyTimes();
		EasyMock.expect(endpoint.getEndpointInfo()).andReturn(endpointInfo).anyTimes();
		EasyMock.replay(endpoint);
		
		return endpoint;
	}

}
