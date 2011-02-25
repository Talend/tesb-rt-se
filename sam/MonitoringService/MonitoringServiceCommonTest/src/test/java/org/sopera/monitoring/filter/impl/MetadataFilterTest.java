package org.sopera.monitoring.filter.impl;

import junit.framework.Assert;

import org.junit.Test;
import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.util.EventCreator;

public class MetadataFilterTest {

	private MetadataFilter<Event> createDefaultFilter() {
		MetadataFilter<Event> filter = new MetadataFilter<Event>();
		filter.setAndCondition(true);
		filter.setHostname("testHost");
		filter.setIp("127.0.0.1");
		filter.setOperationName("testOperationName");
		return filter;
	}

	private Event createDefaultEvent() {
		Event event = new EventCreator<Event>(Event.class).generateEvent();
		event.getEventInfo().getOriginator().setHostname("testHost");
		event.getEventInfo().getOriginator().setIp("127.0.0.1");
		event.getMessageInfo().setOperationName("testOperationName");
		return event;
	}

	@Test
	public void filterAndLogicCompleteMatchTest() {
		MetadataFilter<Event> filter = createDefaultFilter();
		Event event = createDefaultEvent();

		// Filter and event match completely
		Assert.assertTrue(filter.filter(event));
	}

	@Test
	public void filterOrLogicCompleteMatchTest() {
		MetadataFilter<Event> filter = createDefaultFilter();
		Event event = createDefaultEvent();

		filter.setAndCondition(false);

		// Filter and event match
		Assert.assertTrue(filter.filter(event));
	}

	@Test
	public void filterOrLogicPartialMatchTest() {
		MetadataFilter<Event> filter = createDefaultFilter();
		Event event = createDefaultEvent();

		filter.setAndCondition(false);
		filter.setHostname("otherHost");

		// Filter and event match even with another hostname.
		Assert.assertTrue(filter.filter(event));
	}

	@Test
	public void filterAndLogicNotMatching() {
		MetadataFilter<Event> filter = createDefaultFilter();
		Event event = createDefaultEvent();

		filter.setHostname("otherHost");

		// Filter and event do not match
		Assert.assertFalse(filter.filter(event));
	}

	@Test
	public void filterEmptyStringIgnoredTest() {
		MetadataFilter<Event> filter = createDefaultFilter();
		Event event = createDefaultEvent();

		filter.setHostname("");

		// Filter and event match. Hostename is null for testing
		Assert.assertTrue(filter.filter(event));
	}
}
