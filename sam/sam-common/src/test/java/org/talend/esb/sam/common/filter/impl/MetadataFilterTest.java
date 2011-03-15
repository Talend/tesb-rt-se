/*
 * #%L
 * Service Activity Monitoring :: Common
 * %%
 * Copyright (C) 2011 Talend Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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

		// Filter and event match. Hostname is null for testing
		Assert.assertTrue(filter.filter(event));
	}
}
