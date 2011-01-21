package org.talend.esb.locator;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Representation of ZooKeeper node path. The path may be either relative or absolute. As path
 * segments for obvious cannot contain the path separator ("/") raw paths are always encoded.
 *
 */
public class NodePath {

	private static final Logger LOG = Logger.getLogger(NodePath.class.getPackage().getName());
	
	public static final char SEPARATOR = '/';
	
	private String path;

	public NodePath(String... pathSegments) {
		if (pathSegments == null || pathSegments.length == 0) {
			LOG.log(Level.SEVERE, "At least one path segment must be defined."); 
			throw new IllegalArgumentException("At least one path segment must be defined.");
		}
		StringBuffer rawPath = new StringBuffer();
		for (String pathSegment : pathSegments) {
			checkValidPathSegment(pathSegment);
			String encodedPathSegment = encode(pathSegment);
			rawPath.append(SEPARATOR).append(encodedPathSegment);
		}
		path = rawPath.toString();
	}

	public NodePath(NodePath parentPath, String childPathSegment) {
		checkValidPathSegment(childPathSegment);
		path = parentPath.path + SEPARATOR + encode(childPathSegment);
	}

	public NodePath child(String childPathSegment) {
		return new NodePath(this, childPathSegment);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || ! (obj instanceof NodePath)) {
			return false;
		}
		return path.equals(((NodePath)obj).path);
	}

	@Override
	public int hashCode() {
		return path.hashCode();
	}

	@Override
	public String toString() {
		return path;
	}
	
	private String encode(String raw) {
		String encoded = raw.replace("%", "%2A");
		encoded = encoded.replace("/", "%2F");
		return encoded;
	}
/*
	private String decode(String encoded) {
		String raw = encoded.replace("%2F", "/");
		raw = raw.replace("%2A", "%");
		return raw;
	}
*/
	private void checkValidPathSegment(String pathSegment) {
		if (pathSegment == null || pathSegment.isEmpty()) {
			throw new IllegalArgumentException("Path segments must not be null and not empty.");
		}
	}
}
