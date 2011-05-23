package org.talend.esb.servicelocator.cxf.internal;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import org.apache.cxf.clustering.FailoverStrategy;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.easymock.Capture;
import org.junit.Test;
import org.talend.esb.servicelocator.client.ServiceLocator;

public class LocatorClientEnablerTest {
	
	Endpoint ENDPOINT = createMock(Endpoint.class);

	@Test
	public void enableClient() {
		ServiceLocator sl = createMock(ServiceLocator.class);
		
		Capture<LocatorTargetSelector> capturedSelector = new Capture<LocatorTargetSelector>();
		
		Client client = createMock(Client.class);
		expect(client.getEndpoint()).andStubReturn(ENDPOINT);
		client.setConduitSelector(capture(capturedSelector));
		replay(client);

		LocatorClientEnabler clientRegistrar = new LocatorClientEnabler();
		clientRegistrar.setServiceLocator(sl);
		clientRegistrar.enable(client);
		
		LocatorTargetSelector selector = capturedSelector.getValue();
		assertEquals(ENDPOINT, selector.getEndpoint());

		FailoverStrategy strategy = selector.getStrategy();
		assertThat(strategy, instanceOf(LocatorSelectionStrategy.class));
		
		

		LocatorSelectionStrategy locatorstrategy = (LocatorSelectionStrategy) strategy; 
		assertSame(sl, locatorstrategy.getServiceLocator());
		
		verify(client);
	}
	
}
