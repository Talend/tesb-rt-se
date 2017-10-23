package org.talend.esb.auxiliary.storage.commands;

import org.easymock.EasyMock;
import org.junit.Test;
import junit.framework.AssertionFailedError;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import java.net.URI;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;

public class StartAuxiliaryRepoTest {

	private static String FEATURE_NAME = "tesb-aux";
	private StartAuxiliaryRepo r = new StartAuxiliaryRepo();

	@Test
	public void doExecuteNewFeatureTest() throws Exception {

		Feature f = createMock(Feature.class);
		FeaturesService s = createMock(FeaturesService.class);

		expect(s.getFeature(FEATURE_NAME)).andReturn(f);
		expect(s.isInstalled(f)).andReturn(false);

		// new Feature must be installed
		s.installFeature(FEATURE_NAME);
		EasyMock.expectLastCall().once();

		replay(s);
		r.doExecute(s);
		EasyMock.verify(s);
	}

	@Test
	public void doExecuteExistingFeatureTest() throws Exception {

		Feature f = createMock(Feature.class);
		FeaturesService s = createMock(FeaturesService.class);

		expect(s.getFeature(FEATURE_NAME)).andReturn(f);
		expect(s.isInstalled(f)).andReturn(true);

		// Existing Feature must be not installed
		s.installFeature(FEATURE_NAME);
		EasyMock.expectLastCall().andThrow(new AssertionFailedError("Feature must be not installed")).anyTimes();

		replay(s);
		r.doExecute(s);
		EasyMock.verify(s);

	}

	@Test
	public void doExecuteNullFeatureTest() throws Exception {

		FeaturesService s = createMock(FeaturesService.class);

		expect(s.getFeature(FEATURE_NAME)).andReturn(null).anyTimes();
		expect(s.isInstalled(EasyMock.<Feature>anyObject())).andReturn(false).anyTimes();

		s.addRepository(EasyMock.<URI>anyObject());
		EasyMock.expectLastCall().atLeastOnce();

		// expect no error

		replay(s);
		r.doExecute(s);
		EasyMock.verify(s);
	}

	@Test
	public void doExecuteNullFeature2Test() throws Exception {

		FeaturesService s = createMock(FeaturesService.class);

		expect(s.getFeature(FEATURE_NAME)).andReturn(null);
		expect(s.isInstalled(EasyMock.<Feature>anyObject())).andReturn(false).anyTimes();

		Feature f = createMock(Feature.class);
		expect(s.getFeature(FEATURE_NAME)).andReturn(f);

		s.addRepository(EasyMock.<URI>anyObject());
		EasyMock.expectLastCall().atLeastOnce();

		s.installFeature(FEATURE_NAME);
		EasyMock.expectLastCall().once();

		replay(s);
		r.doExecute(s);
		EasyMock.verify(s);
	}

}