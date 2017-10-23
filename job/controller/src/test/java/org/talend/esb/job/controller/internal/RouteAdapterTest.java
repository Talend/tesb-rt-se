package org.talend.esb.job.controller.internal;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import routines.system.api.TalendESBRoute;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import java.util.concurrent.atomic.AtomicBoolean;

public class RouteAdapterTest {

	@Test
	public void shouldRunJob() throws Exception {

		TalendESBRoute route = createMock(TalendESBRoute.class);
		expect(route.runJobInTOS(aryEq(new String[0]))).andReturn(0);
		replay(route);
		RouteAdapter r = new RouteAdapter(route, "route");
		r.run();
		verify(route);
	}

	@Test
	public void shouldRunJobWithRuntimeException() throws Exception {

		TalendESBRoute route = createMock(TalendESBRoute.class);
		expect(route.runJobInTOS(aryEq(new String[0]))).andThrow(new RuntimeException());
		replay(route);
		RouteAdapter r = new RouteAdapter(route, "route");
		r.run();
		verify(route);
	}

	@Test
	public void emptyParameters() throws Exception {

		RouteAdapter r = new RouteAdapter(createMock(TalendESBRoute.class), "route");
		r.updated(null);
	}

	@Test
	public void shouldStopJob() throws Exception {

		final AtomicBoolean jobIsCalled = new AtomicBoolean();
		TalendESBRoute route = new TalendESBRoute() {

			@Override
			public int runJobInTOS(String[] arg0) {
				return 0;
			}

			@Override
			public String[][] runJob(String[] arg0) {
				return null;
			}

			@Override
			public void stop() throws Exception {
				jobIsCalled.set(true);

			}

			@Override
			public void shutdown() throws Exception {
				jobIsCalled.set(true);

			}
		};

		RouteAdapter r = new RouteAdapter(route, "route");
		r.stop();

		assertTrue("job is not called", jobIsCalled.get());
	}

	@Test
	public void shouldStopJobWithException() throws Exception {

		final AtomicBoolean jobIsCalled = new AtomicBoolean();
		TalendESBRoute route = new TalendESBRoute() {

			@Override
			public int runJobInTOS(String[] arg0) {
				return 0;
			}

			@Override
			public String[][] runJob(String[] arg0) {
				return null;
			}

			@Override
			public void stop() throws Exception {
				jobIsCalled.set(true);
				throw new Exception();

			}

			@Override
			public void shutdown() throws Exception {
				jobIsCalled.set(true);
				throw new Exception();

			}
		};

		RouteAdapter r = new RouteAdapter(route, "route");
		r.stop();

		assertTrue("job is not called", jobIsCalled.get());
	}
}