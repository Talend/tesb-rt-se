/*
 * #%L
 * Service Activity Monitoring :: Common
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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
package org.talend.esb.sam.common.handler.impl;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.handler.impl.ContentLengthHandler;

/**
 * Test content length cutting
 *
 * @author cschmuelling
 *
 */
public class ContentLengthHandlerTest extends TestCase {

	private Event event;
	private ContentLengthHandler contentLengthHandler;

	public ContentLengthHandlerTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(ContentLengthHandlerTest.class);
	}

	/**
	 * Test, nothing will happen if content is shorter then maximum length
	 */
	public void testLengthNotCutting() {
		String testString = "<n>0123</n>";
		event.setContent(testString);
		contentLengthHandler.handleEvent(event);
		assertEquals(event.getContent(), testString);
	}

	/**
	 * Content is longer then maximum length and will be cut
	 */
	public void testLengthCutting() {
		String testString = "<normal>0123456789012345678901234567890123456789</normal>";
		event.setContent(testString);
		contentLengthHandler.handleEvent(event);
		assertEquals(event.getContent(), ContentLengthHandler.CUT_START_TAG
				+ testString.substring(0, 17)
				+ ContentLengthHandler.CUT_END_TAG);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		contentLengthHandler = new ContentLengthHandler();
		contentLengthHandler.setLength(40);

		event = new Event();
	}
}
