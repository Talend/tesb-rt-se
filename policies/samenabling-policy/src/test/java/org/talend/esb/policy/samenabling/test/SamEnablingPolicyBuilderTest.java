package org.talend.esb.policy.samenabling.test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.talend.esb.policy.samenabling.SamEnablingPolicyBuilder;

public class SamEnablingPolicyBuilderTest {

	@Test
	public void testGetKnownElements() {
		SamEnablingPolicyBuilder pb = new SamEnablingPolicyBuilder();

		List<QName> l = Arrays.asList(pb.getKnownElements());

		assertTrue(l != null);
		assertTrue(l.contains(SamEnablingPolicyBuilder.SAM_ENABLE));
	}
}
