package org.sopera.monitoring.handler.impl;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.sopera.monitoring.event.Event;

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
