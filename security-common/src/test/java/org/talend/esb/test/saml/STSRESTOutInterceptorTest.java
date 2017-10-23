package org.talend.esb.test.saml;

import org.apache.cxf.message.Message;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.trust.STSClient;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.verify;
import org.junit.Test;
import static org.junit.Assert.assertSame;
import org.talend.esb.security.saml.STSRESTOutInterceptor;

import static org.easymock.EasyMock.createMock;

public class STSRESTOutInterceptorTest {

	@Test
	public void handleMessage() throws Exception {

		STSRESTOutInterceptor i = new STSRESTOutInterceptor();

		Message message = createMock(Message.class);

		STSClient stsClient = createMock(STSClient.class);
		stsClient.requestSecurityToken(EasyMock.<String>anyObject());
		EasyMock.expectLastCall().andReturn(null).anyTimes();

		stsClient.setActAs(EasyMock.<String>anyObject());
		EasyMock.expectLastCall().atLeastOnce();

		stsClient.setOnBehalfOf(EasyMock.<Object>anyObject());
		EasyMock.expectLastCall().atLeastOnce();

		stsClient.setMessage(EasyMock.<Message>anyObject());
		EasyMock.expectLastCall().atLeastOnce();

		replay(stsClient);

		i.setStsClient(stsClient);
		assertSame(stsClient, i.getStsClient());

		message.get(Message.REQUESTOR_ROLE);
		expectLastCall().andReturn(true).anyTimes();

		message.getContextualProperty(SecurityConstants.STS_TOKEN_ACT_AS);
		expectLastCall().andReturn(new Object()).anyTimes();

		message.getContextualProperty(SecurityConstants.STS_APPLIES_TO);
		expectLastCall().andReturn(new Object()).anyTimes();

		message.getContextualProperty(SecurityConstants.STS_TOKEN_ON_BEHALF_OF);
		expectLastCall().andReturn(new Object()).anyTimes();

		replay(message);

		i.handleMessage(message);

		verify(message);

	}

	@Test
	public void handleMessageNotRequestor() throws Exception {

		STSRESTOutInterceptor i = new STSRESTOutInterceptor();

		Message message = createMock(Message.class);

		message.get(Message.REQUESTOR_ROLE);
		expectLastCall().andReturn(false).anyTimes();

		STSClient stsClient = createMock(STSClient.class);

		i.setStsClient(stsClient);
		assertSame(stsClient, i.getStsClient());

		replay(message);

		i.handleMessage(message);

		verify(message);

	}

	@Test
	public void handleMessageNoStsClient() throws Exception {

		STSRESTOutInterceptor i = new STSRESTOutInterceptor();

		Message message = createMock(Message.class);

		message.get(Message.REQUESTOR_ROLE);
		expectLastCall().andReturn(true).anyTimes();

		i.setStsClient(null);
		assertSame(null, i.getStsClient());

		replay(message);

		i.handleMessage(message);

		verify(message);

	}

}