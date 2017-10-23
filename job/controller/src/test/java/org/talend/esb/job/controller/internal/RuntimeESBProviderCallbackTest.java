package org.talend.esb.job.controller.internal;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.easymock.IAnswer;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.esb.job.controller.internal.RuntimeESBProviderCallback.MessageExchange;
import org.talend.esb.job.controller.internal.MessageExchangeBuffer.BufferStoppedException;

import routines.system.api.ESBJobInterruptedException;
import routines.system.api.TalendESBJob;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import java.util.concurrent.atomic.AtomicBoolean;

public class RuntimeESBProviderCallbackTest {

	@Test
	public void getRequestTest() throws Exception {

		Object request = new Object();

		RuntimeESBProviderCallback p = createRuntimeESBProviderCallbackMock(null, request, null, null);

		assertSame(p.getRequest(), request);

	}

	@Test
	public void getResponseTest() throws Exception {

		Object request = new Object();
		Object response = new Object();

		MessageExchange exchange = new MessageExchange(request);
		exchange.setResponse(response);

		RuntimeESBProviderCallback p = createRuntimeESBProviderCallbackMock(null, null, null, exchange);

		p.getRequest();
		p.sendResponse(response);

		assertSame(p.getRequest(), request);
		assertSame(exchange.waitForResponse(), response);

	}

	@Test
	public void messageExchangeTest() throws Exception {

		Object request = createNiceMock(Object.class);

		MessageExchange exchange = new MessageExchange(request);

		assertSame(exchange.getRequest(), request);

	}

	@Test
	public void toStringTest() throws Exception {

		RuntimeESBProviderCallback p = createRuntimeESBProviderCallbackMock(null, null, null, null);
		assertTrue(p.toString().matches("RuntimeESBProviderCallback.*TalendESBJob"));

	}

	@Ignore
	public void getRequestBufferStoppedExceptionTest() throws Exception {

		MessageExchangeBuffer messageExchanges = getMessageExchanges(new BufferStoppedException());

		RuntimeESBProviderCallback p = createRuntimeESBProviderCallbackMock(null, null, messageExchanges, null);

		try {
			p.getRequest();
		} catch (ESBJobInterruptedException ex) {
			return;
		}

		fail("ESBJobInterruptedException is not thrown");

	}

	@Ignore
	public void getRequestInterruptedExceptionTest() throws Exception {

		MessageExchangeBuffer messageExchanges = getMessageExchanges(new InterruptedException());

		RuntimeESBProviderCallback p = createRuntimeESBProviderCallbackMock(null, null, messageExchanges, null);

		try {
			p.getRequest();
		} catch (ESBJobInterruptedException ex) {
			return;
		}

		fail("ESBJobInterruptedException is not thrown");

	}

	@Test
	public void shouldRunJob() throws Exception {

		TalendESBJob job = createNiceMock(TalendESBJob.class);

		final AtomicBoolean jobIsCalled = new AtomicBoolean();
		expect(job.runJobInTOS(null)).andAnswer(new IAnswer<Integer>() {
			@Override
			public Integer answer() {
				jobIsCalled.set(true);
				return 1;
			}
		});

		replay(job);

		RuntimeESBProviderCallback p = createRuntimeESBProviderCallbackMock(job, null, null, null);
		p.run();

		assertTrue("job is not called", jobIsCalled.get());
	}

	@Test
	public void shouldRunJobWithExceptionTest() throws Exception {

		TalendESBJob job = createNiceMock(TalendESBJob.class);

		expect(job.runJobInTOS(null)).andThrow(new RuntimeException());

		replay(job);

		RuntimeESBProviderCallback p = createRuntimeESBProviderCallbackMock(job, null, null, null);

		p.run();

	}

	private RuntimeESBProviderCallback createRuntimeESBProviderCallbackMock(TalendESBJob job, Object request,
			MessageExchangeBuffer messageExchanges, MessageExchange exchange) throws Exception {

		if (job == null) {
			job = createNiceMock(TalendESBJob.class);
		}

		if (request == null) {
			request = createNiceMock(Object.class);
		}

		if (exchange == null) {
			exchange = createNiceMock(MessageExchange.class);
			expect(exchange.getRequest()).andReturn(request).anyTimes();
			replay(exchange);
		}

		if (messageExchanges == null) {
			messageExchanges = getMessageExchanges(null);
			expect(messageExchanges.take()).andReturn(exchange).anyTimes();
			replay(messageExchanges);
		}

		return new RuntimeESBProviderCallback(messageExchanges, job, null, null);

	}

	private <T extends Exception> MessageExchangeBuffer getMessageExchanges(T exception) throws Exception {
		MessageExchangeBuffer messageExchanges = createStrictMock(MessageExchangeBuffer.class);

		if (exception != null) {
			expect(messageExchanges.take()).andThrow(exception);
			replay(messageExchanges);
		}

		return messageExchanges;
	}



}