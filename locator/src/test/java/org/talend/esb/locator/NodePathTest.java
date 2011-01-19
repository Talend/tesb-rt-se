package org.talend.esb.locator;

import static org.hamcrest.CoreMatchers.equalTo;
import static  org.junit.Assert.assertThat;
import static  org.junit.Assert.fail;

import org.junit.Test;

public class NodePathTest {

	public static final String VALID_PATH = "/root/child";

	public static final String PATH_SEGMENT_1 = "root";

	public static final String PATH_SEGMENT_2 = "child";

	public static final String PATH_SEGMENT_RAW_1 = "part1/part2";

	public static final String PATH_SEGMENT_RAW_2 = "part3%part4";

	public static final String EMPTY_PATH_SEGMENT = "";

	public static final String VALID_PATH_ENCODED = "/part1%2Fpart2/part3%2Apart4";
	
	public static final NodePath PARENT_PATH = new NodePath(PATH_SEGMENT_RAW_1);

	
	/*
	@Test
	public void createNodePathByValidRawString() {
		NodePath path = new NodePath(VALID_RAW_PATH);
		assertThat(path.toString(), equalTo(VALID_RAW_PATH));
	}
*/

	@Test
	public void createNodePathByPathSegments() {
		NodePath path = new NodePath(PATH_SEGMENT_1, PATH_SEGMENT_2);
		assertThat(path.toString(), equalTo(VALID_PATH));
	}

	@Test
	public void createNodePathByPathSegmentsWithReserverCharacters() {
		NodePath path = new NodePath(PATH_SEGMENT_RAW_1, PATH_SEGMENT_RAW_2);
		assertThat(path.toString(), equalTo(VALID_PATH_ENCODED));
	}

	@Test
	public void createNodePathWithEmptySegmentListFails() {
		try {
			new NodePath();
			fail("An IllegalArgumentException should have been thrown");
		} catch(IllegalArgumentException e) {
		}
	}

	@Test
	public void createNodePathWithNullSegmentListFails() {
		try {
			String[] segments = null;
			new NodePath(segments);
			fail("An IllegalArgumentException should have been thrown");
		} catch(IllegalArgumentException e) {
		}
	}

	@Test
	public void createNodePathWithAtLeastOneEmptyPathSegmentFails() {
		try {
			new NodePath(PATH_SEGMENT_1, EMPTY_PATH_SEGMENT);
			fail("An IllegalArgumentException should have been thrown");
		} catch(IllegalArgumentException e) {
		}
	}

	@Test
	public void createChildNodePath() {
		NodePath childPath = PARENT_PATH.child(PATH_SEGMENT_RAW_2);
		
		assertThat(childPath.toString(), equalTo(VALID_PATH_ENCODED));
	}

	@Test
	public void createWithEmptyChildNodePathFails() {
		try {
			PARENT_PATH.child(EMPTY_PATH_SEGMENT);
			fail("An IllegalArgumentException should have been thrown");
		} catch(IllegalArgumentException e) {
		}
	}

	@Test
	public void createWithUndefinedChildNodePathFails() {
		try {
			PARENT_PATH.child(null);
			fail("An IllegalArgumentException should have been thrown");
		} catch(IllegalArgumentException e) {
		}
	}
}
