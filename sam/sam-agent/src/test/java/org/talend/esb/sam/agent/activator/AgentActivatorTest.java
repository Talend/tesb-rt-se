package org.talend.esb.sam.agent.activator;

import java.util.Dictionary;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class AgentActivatorTest {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testAgentActivator() throws Exception {
		AgentActivator a = new AgentActivator();

		ServiceReference serviceRef = EasyMock.createMock(ServiceReference.class);

		Dictionary<String, Object> properties = EasyMock.createMock(Dictionary.class);
		EasyMock.expect(properties.get("collector.lifecycleEvent")).andReturn("true").anyTimes();
		EasyMock.expect(properties.get("service.url")).andReturn("http://localhost:3378/monitoringService").anyTimes();
		EasyMock.expect(properties.get("service.retry.number")).andReturn("1").anyTimes();
		EasyMock.expect(properties.get("service.retry.delay")).andReturn("10").anyTimes();

		EasyMock.replay(properties);

		Configuration config = EasyMock.createMock(Configuration.class);
		EasyMock.expect(config.getProperties()).andReturn(properties).anyTimes();
		EasyMock.replay(config);

		ConfigurationAdmin cfgAdmin = EasyMock.createMock(ConfigurationAdmin.class);
		EasyMock.expect(cfgAdmin.getConfiguration("org.talend.esb.sam.agent")).andReturn(config).anyTimes();
		EasyMock.replay(cfgAdmin);

		BundleContext context = EasyMock.createMock(BundleContext.class);
		EasyMock.expect(context.getServiceReference(ConfigurationAdmin.class.getName())).andReturn(serviceRef)
				.anyTimes();
		EasyMock.expect(context.getService(serviceRef)).andReturn(cfgAdmin).anyTimes();
		EasyMock.replay(context);

		try {
			a.start(context);
		}catch(Exception ex) {
			Assert.assertEquals("Send SERVER_START/SERVER_STOP event to SAM Server failed", ex.getMessage());
		}
		
		try {
			a.stop(context);
		}catch(Exception ex) {
			Assert.assertEquals("Send SERVER_START/SERVER_STOP event to SAM Server failed", ex.getMessage());
		}
	}

}
