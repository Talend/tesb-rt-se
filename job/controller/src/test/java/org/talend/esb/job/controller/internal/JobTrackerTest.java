package org.talend.esb.job.controller.internal;

import org.easymock.EasyMock;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

public class JobTrackerTest {

	@Test
	public void shouldBind() throws Exception {

		JobTracker t = new JobTracker();
		
		JobListener jobListener = createMock(JobListener.class);
		t.setJobListener(jobListener);

		BundleContext bundleContext = createMock(BundleContext.class);
		expect(bundleContext.createFilter(EasyMock.<String>anyObject()))
				.andReturn(createNiceMock(org.osgi.framework.Filter.class));

		replay(bundleContext);

		t.setBundleContext(bundleContext);

		
		t.unbind();
		
	}

}