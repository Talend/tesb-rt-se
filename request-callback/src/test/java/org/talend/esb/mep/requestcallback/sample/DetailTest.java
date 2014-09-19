package org.talend.esb.mep.requestcallback.sample;

import org.junit.Test;
import org.talend.esb.mep.requestcallback.beans.JmsUriConfiguration;

import static org.junit.Assert.*;

public class DetailTest {

	@Test
	public void testJmsUriParsing() {
		JmsUriConfiguration cfg = new JmsUriConfiguration();
		cfg.applyJmsUri("jms:queue:callbackRequestQueue.queue?jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory&jndiConnectionFactoryName=ConnectionFactory&jndiURL=tcp://localhost:61616");
		assertEquals(cfg.getVariant(), "queue");
		assertEquals(cfg.getDestinationName(), "callbackRequestQueue.queue");
		assertEquals(cfg.getParameter("jndiInitialContextFactory"), "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		assertEquals(cfg.getParameter("jndiConnectionFactoryName"), "ConnectionFactory");
		assertEquals(cfg.getParameter("jndiURL"), "tcp://localhost:61616");
	}
}
