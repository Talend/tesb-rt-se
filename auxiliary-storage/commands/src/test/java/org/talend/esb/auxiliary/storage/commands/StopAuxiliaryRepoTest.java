package org.talend.esb.auxiliary.storage.commands;

import org.easymock.EasyMock;
import org.junit.Test;
import junit.framework.AssertionFailedError;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import java.util.Arrays;
import java.util.List;

import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;

public class StopAuxiliaryRepoTest {

	private static List<String> FEATURES = Arrays.asList("tesb-aux", "tesb-aux-common", "tesb-aux-client-rest",
			"tesb-aux-service-rest", "tesb-aux-persistence", "tesb-aux-server");

	private static StopAuxiliaryRepo r = new StopAuxiliaryRepo();

	@Test
	public void doExecuteUninstallMissingFeaturesTest() throws Exception {

		FeaturesService s = createMock(FeaturesService.class);

		for (String featureName : FEATURES) {
			Feature f = createMock(Feature.class);
			expect(s.getFeature(featureName)).andReturn(f);
			expect(s.isInstalled(f)).andReturn(false);
			s.uninstallFeature(featureName);
			EasyMock.expectLastCall()
					.andThrow(new AssertionFailedError("Feature " + featureName + " must be not uninstalled"))
					.anyTimes();
		}

		replay(s);
		r.doExecute(s);
		EasyMock.verify(s);
	}

	@Test
	public void doExecuteUninstallExistingFeaturesTest() throws Exception {

		FeaturesService s = createMock(FeaturesService.class);

		for (String featureName : FEATURES) {
			Feature f = createMock(Feature.class);
			expect(s.getFeature(featureName)).andReturn(f);
			expect(s.isInstalled(f)).andReturn(true);
			s.uninstallFeature(featureName);
			EasyMock.expectLastCall().once();
		}

		replay(s);
		r.doExecute(s);
		EasyMock.verify(s);
	}

}