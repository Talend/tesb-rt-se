package org.talend.esb.servicelocator.cxf.internal;

import static org.easymock.EasyMock.expect;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.ClientImpl;
import org.apache.cxf.endpoint.ClientLifeCycleManager;
import org.apache.cxf.endpoint.ClientLifeCycleManagerImpl;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.EndpointException;
import org.apache.cxf.endpoint.EndpointImpl;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.EndpointInfo;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.esb.servicelocator.cxf.LocatorFeature;

public class LocatorFeatureTest extends EasyMockSupport {

	Bus busMock;
	LocatorRegistrar locatorRegistrarMock;
	Map<String, LocatorSelectionStrategy> locatorSelectionStrategies;

	@Before
	public void setup() {
		busMock = createMock(Bus.class);

		locatorRegistrarMock = createMock(LocatorRegistrar.class);
		locatorRegistrarMock.startListenForServers();
		EasyMock.expectLastCall().anyTimes();

		locatorSelectionStrategies = new HashMap<String, LocatorSelectionStrategy>();
		locatorSelectionStrategies.put("defaultSelectionStrategy",
				new DefaultSelectionStrategy());
		locatorSelectionStrategies.put("randomSelectionStrategy",
				new RandomSelectionStrategy());
		locatorSelectionStrategies.put("evenDistributionSelectionStrategy",
				new EvenDistributionSelectionStrategy());
	}

	@Test
	public void initializeClient() throws EndpointException {
		LocatorClientEnabler enabler = new LocatorClientEnabler();
		enabler.setBus(busMock);

		enabler.setLocatorSelectionStrategies(locatorSelectionStrategies);
		enabler.setLocatorSelectionStrategy("defaultSelectionStrategy");

		ServiceLocatorManager slm = new ServiceLocatorManager();

		slm.setBus(busMock);
		slm.setLocatorRegistrar(locatorRegistrarMock);
		slm.setLocatorClientEnabler(enabler);

		expect(busMock.getExtension(ServiceLocatorManager.class))
				.andStubReturn(slm);

		ClientLifeCycleManager clcm = new ClientLifeCycleManagerImpl();
		expect(busMock.getExtension(ClientLifeCycleManager.class))
				.andStubReturn(clcm);

		replayAll();

		EndpointInfo ei = new EndpointInfo();
		Service service = new org.apache.cxf.service.ServiceImpl();
		Endpoint endpoint = new EndpointImpl(busMock, service, ei);
		Client client = new ClientImpl(busMock, endpoint);

		LocatorTargetSelector selector = new LocatorTargetSelector();
		selector.setEndpoint(endpoint);

		client.setConduitSelector(selector);

		LocatorFeature lf = new LocatorFeature();
		lf.setSelectionStrategy("randomSelectionStrategy");

		lf.initialize(client, busMock);
		
		Assert.assertTrue(((LocatorTargetSelector) client.getConduitSelector()).getStrategy() instanceof RandomSelectionStrategy);

	}

	@Test
	public void initializeClientNegative() throws EndpointException {

		LocatorClientEnabler enabler = new LocatorClientEnabler();
		enabler.setBus(busMock);

		enabler.setLocatorSelectionStrategies(locatorSelectionStrategies);
		enabler.setLocatorSelectionStrategy("defaultSelectionStrategy");

		ServiceLocatorManager slm = new ServiceLocatorManager();

		slm.setBus(busMock);
		slm.setLocatorRegistrar(locatorRegistrarMock);
		slm.setLocatorClientEnabler(enabler);

		expect(busMock.getExtension(ServiceLocatorManager.class))
				.andStubReturn(slm);

		ClientLifeCycleManager clcm = new ClientLifeCycleManagerImpl();
		expect(busMock.getExtension(ClientLifeCycleManager.class))
				.andStubReturn(clcm);

		replayAll();

		EndpointInfo ei = new EndpointInfo();
		Service service = new org.apache.cxf.service.ServiceImpl();
		Endpoint endpoint = new EndpointImpl(busMock, service, ei);
		Client client = new ClientImpl(busMock, endpoint);

		// **********************************************************************************************
		LocatorTargetSelector selector = new LocatorTargetSelector();
		selector.setEndpoint(endpoint);

		client.setConduitSelector(selector);

		LocatorFeature lf = new LocatorFeature();
		lf.setSelectionStrategy("defaultSelectionStrategy");

		lf.initialize(client, busMock);

		Assert.assertFalse(((LocatorTargetSelector) client.getConduitSelector()).getStrategy() instanceof RandomSelectionStrategy);
		
	}

	@Test
	public void initializeClients() throws EndpointException {

		LocatorClientEnabler enabler = new LocatorClientEnabler();
		enabler.setBus(busMock);

		enabler.setLocatorSelectionStrategies(locatorSelectionStrategies);
		enabler.setLocatorSelectionStrategy("defaultSelectionStrategy");

		ServiceLocatorManager slm = new ServiceLocatorManager();

		slm.setBus(busMock);
		slm.setLocatorRegistrar(locatorRegistrarMock);
		slm.setLocatorClientEnabler(enabler);

		expect(busMock.getExtension(ServiceLocatorManager.class))
				.andStubReturn(slm);

		ClientLifeCycleManager clcm = new ClientLifeCycleManagerImpl();
		expect(busMock.getExtension(ClientLifeCycleManager.class))
				.andStubReturn(clcm);

		replayAll();

		LocatorFeature lf = new LocatorFeature();
		{
			EndpointInfo ei = new EndpointInfo();
			Service service = new org.apache.cxf.service.ServiceImpl();
			Endpoint endpoint = new EndpointImpl(busMock, service, ei);
			Client client = new ClientImpl(busMock, endpoint);

			LocatorTargetSelector selector = new LocatorTargetSelector();
			selector.setEndpoint(endpoint);

			client.setConduitSelector(selector);

			lf.setSelectionStrategy("randomSelectionStrategy");

			lf.initialize(client, busMock);

			Assert.assertTrue(((LocatorTargetSelector) client.getConduitSelector()).getStrategy() instanceof RandomSelectionStrategy);
		}
		{
			EndpointInfo ei = new EndpointInfo();
			Service service = new org.apache.cxf.service.ServiceImpl();
			Endpoint endpoint = new EndpointImpl(busMock, service, ei);
			Client client = new ClientImpl(busMock, endpoint);
			LocatorTargetSelector selector = new LocatorTargetSelector();
			selector.setEndpoint(endpoint);
			client.setConduitSelector(selector);

			lf.setSelectionStrategy("evenDistributionSelectionStrategy");

			lf.initialize(client, busMock);

			Assert.assertTrue(((LocatorTargetSelector) client.getConduitSelector()).getStrategy() instanceof EvenDistributionSelectionStrategy);
		}
	}
	
	@Test
	@Ignore
	public void initializeClientConfiguration() throws EndpointException {
		LocatorClientEnabler enabler = new LocatorClientEnabler();
		enabler.setBus(busMock);

		enabler.setLocatorSelectionStrategies(locatorSelectionStrategies);
		enabler.setLocatorSelectionStrategy("defaultSelectionStrategy");

		ServiceLocatorManager slm = new ServiceLocatorManager();

		slm.setBus(busMock);
		slm.setLocatorRegistrar(locatorRegistrarMock);
		slm.setLocatorClientEnabler(enabler);

		expect(busMock.getExtension(ServiceLocatorManager.class))
				.andStubReturn(slm);

		ClientLifeCycleManager clcm = new ClientLifeCycleManagerImpl();
		expect(busMock.getExtension(ClientLifeCycleManager.class))
				.andStubReturn(clcm);

		replayAll();

		EndpointInfo ei = new EndpointInfo();
		Service service = new org.apache.cxf.service.ServiceImpl();
		Endpoint endpoint = new EndpointImpl(busMock, service, ei);
		ClientConfiguration client = new ClientConfiguration();

		LocatorTargetSelector selector = new LocatorTargetSelector();
		selector.setEndpoint(endpoint);

		client.setConduitSelector(selector);

		LocatorFeature lf = new LocatorFeature();
		lf.setSelectionStrategy("randomSelectionStrategy");

		lf.initialize(client, busMock);

		Assert.assertTrue(((LocatorTargetSelector) client.getConduitSelector()).getStrategy() instanceof RandomSelectionStrategy);
		
	}
	
	@Test
	@Ignore
	public void initializeInterceptorProvider() throws EndpointException {
		LocatorClientEnabler enabler = new LocatorClientEnabler();
		enabler.setBus(busMock);

		enabler.setLocatorSelectionStrategies(locatorSelectionStrategies);
		enabler.setLocatorSelectionStrategy("defaultSelectionStrategy");

		ServiceLocatorManager slm = new ServiceLocatorManager();

		slm.setBus(busMock);
		slm.setLocatorRegistrar(locatorRegistrarMock);
		slm.setLocatorClientEnabler(enabler);

		expect(busMock.getExtension(ServiceLocatorManager.class))
				.andStubReturn(slm);

		ClientLifeCycleManager clcm = new ClientLifeCycleManagerImpl();
		expect(busMock.getExtension(ClientLifeCycleManager.class))
				.andStubReturn(clcm);

		replayAll();

		EndpointInfo ei = new EndpointInfo();
		Service service = new org.apache.cxf.service.ServiceImpl();
		Endpoint endpoint = new EndpointImpl(busMock, service, ei);
		ClientConfiguration client = new ClientConfiguration();

		LocatorTargetSelector selector = new LocatorTargetSelector();
		selector.setEndpoint(endpoint);

		client.setConduitSelector(selector);

		LocatorFeature lf = new LocatorFeature();
		lf.setSelectionStrategy("randomSelectionStrategy");

		lf.initialize((InterceptorProvider)client, busMock);

		Assert.assertTrue(((LocatorTargetSelector) client.getConduitSelector()).getStrategy() instanceof RandomSelectionStrategy);
		
	}
	
}
