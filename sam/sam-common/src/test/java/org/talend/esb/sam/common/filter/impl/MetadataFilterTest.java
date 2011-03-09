package org.talend.esb.sam.common.filter.impl;

import junit.framework.Assert;

import org.junit.Test;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.filter.impl.MetadataFilter;
import org.talend.esb.sam.common.util.EventCreator;

public class MetadataFilterTest {

	private MetadataFilter createDefaultFilter() {
		MetadataFilter filter = new MetadataFilter();
		filter.setAndCondition(true);
		filter.setHostname("testHost");
		filter.setIp("127.0.0.1");
		filter.setOperationName("testOperationName");
		return filter;
	}

	private Event createDefaultEvent() {
		Event event = new EventCreator().generateEvent();
		event.getOriginator().setHostname("testHost");
		event.getOriginator().setIp("127.0.0.1");
		event.getMessageInfo().setOperationName("testOperationName");
		return event;
	}

	@Test
	public void filterAndLogicCompleteMatchTest() {
		MetadataFilter filter = createDefaultFilter();
		Event event = createDefaultEvent();

		// Filter and event match completely
		Assert.assertTrue(filter.filter(event));
	}

	@Test
	public void filterOrLogicCompleteMatchTest() {
		MetadataFilter filter = createDefaultFilter();
		Event event = createDefaultEvent();

		filter.setAndCondition(false);

		// Filter and event match
		Assert.assertTrue(filter.filter(event));
	}

	@Test
	public void filterOrLogicPartialMatchTest() {
		MetadataFilter filter = createDefaultFilter();
		Event event = createDefaultEvent();

		filter.setAndCondition(false);
		filter.setHostname("otherHost");

		// Filter and event match even with another hostname.
		Assert.assertTrue(filter.filter(event));
	}

	@Test
	public void filterAndLogicNotMatching() {
		MetadataFilter filter = createDefaultFilter();
		Event event = createDefaultEvent();

		filter.setHostname("otherHost");

		// Filter and event do not match
		Assert.assertFalse(filter.filter(event));
	}

	@Test
	public void filterEmptyStringIgnoredTest() {
		MetadataFilter filter = createDefaultFilter();
		Event event = createDefaultEvent();

		filter.setHostname("");

		// Filter and event match. Hostename is null for testing
		Assert.assertTrue(filter.filter(event));
	}
}
