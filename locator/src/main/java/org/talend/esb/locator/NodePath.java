package org.talend.esb.locator;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Representation of ZooKeeper node path. The path is always absolute. As path
 * segments obviously cannot contain the path separator ("/") raw paths are always encoded.
 */
public class NodePath {

	private static final Logger LOG = Logger.getLogger(NodePath.class.getPackage().getName());
	
	public static final char SEPARATOR = '/';
	
	private String path;

	/**
	 * Create an <code>NodePath</code> that consists of the given path segments.
	 * 
	 * @param pathSegments the sequence of path segments the NodePath consists of, must not be null or empty
	 */
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

	private NodePath(NodePath parentPath, String childPathSegment) {
		checkValidPathSegment(childPathSegment);
		path = parentPath.path + SEPARATOR + encode(childPathSegment);
	}

	/**
	 * Create a <code>NodePath</code>, which is a child of this one.
	 * 
	 * @param childNodeName name of the child node, must not be <code>null</code>
	 */
	public NodePath child(String childNodeName) {
		return new NodePath(this, childNodeName);
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

	/**
	 * Encoded <code>String</code> representation of this <code>NodePath</code>.
	 *
	 * @return the <code>String</code> representation
	 */
	@Override
	public String toString() {
		return path;
	}

	private void checkValidPathSegment(String pathSegment) {
		if (pathSegment == null || pathSegment.isEmpty()) {
			throw new IllegalArgumentException("Path segments must not be null and not empty.");
		}
	}

	/**
	 * Encode the given <code>String</code>. All occurrences of "/" are mapped to "%2A" and
	 * all occurrences of "%" to "%2F".
	 * 
	 * @param raw the <code>String</code> to encode, must not be <code>null</code>
	 * @return the encoded version
	 * 
	 * @see #decode(String)
	 */
	public static String encode(String raw) {
		String encoded = raw.replace("%", "%2A");
		encoded = encoded.replace("/", "%2F");
		return encoded;
	}

	/**
	 * Decode the given <code>String</code>. It is the inverse operation of {@link #encode(String)}.
	 * For all <code>String</code>s <code>s</code>, <code>decode(encode(s)).equals(s)</code> should
	 * be <code>true</code>.
	 * 
	 * @param encoded the <code>String</code> to decode, must not be <code>null</code>
	 * @return the raw version
	 * 
	 * @see #encode(String)
	 */
	public static String decode(String encoded) {
		String raw = encoded.replace("%2F", "/");
		raw = raw.replace("%2A", "%");
		return raw;
	}
}
