package org.talend.esb.transport.jms;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.transport.jms.JMSConduit;
import org.apache.cxf.transport.jms.JMSConfiguration;
import org.apache.cxf.transport.jms.JMSDestination;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertSame;
import org.junit.Test;

public class DurableSubscriptionFeatureTest {

	@Test
	public void testDurableSubscriptionFeature() throws Exception {

		DurableSubscriptionFeature f = new DurableSubscriptionFeature();
		f.setDurableSubscriptionName("durableSubscriptionName");
		f.setDurableSubscriptionClientId("durableSubscriptionClientId");

		JMSConfiguration jmsConfig = new JMSConfiguration();

		JMSConduit conduit = createMock(JMSConduit.class);
		expect(conduit.getJmsConfig()).andReturn(jmsConfig).anyTimes();
		replay(conduit);

		Client client = createMock(Client.class);
		expect(client.getConduit()).andReturn(conduit).anyTimes();
		replay(client);

		Bus bus = createNiceMock(Bus.class);

		f.initialize(client, bus);

		assertSame("durableSubscriptionName", jmsConfig.getDurableSubscriptionName());
		assertSame("durableSubscriptionClientId", jmsConfig.getDurableSubscriptionClientId());

	}

	@Test
	public void testDurableSubscriptionFeature2() throws Exception {

		DurableSubscriptionFeature f = new DurableSubscriptionFeature();
		f.setDurableSubscriptionName("durableSubscriptionName");
		f.setDurableSubscriptionClientId("durableSubscriptionClientId");

		JMSConfiguration jmsConfig = new JMSConfiguration();

		JMSDestination destination = createMock(JMSDestination.class);
		expect(destination.getJmsConfig()).andReturn(jmsConfig).anyTimes();
		replay(destination);

		Server server = createMock(Server.class);
		expect(server.getDestination()).andReturn(destination).anyTimes();
		replay(server);

		Bus bus = createNiceMock(Bus.class);

		f.initialize(server, bus);

		assertSame("durableSubscriptionName", jmsConfig.getDurableSubscriptionName());
		assertSame("durableSubscriptionClientId", jmsConfig.getDurableSubscriptionClientId());

	}
}