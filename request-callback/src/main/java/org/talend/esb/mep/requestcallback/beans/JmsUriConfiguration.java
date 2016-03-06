package org.talend.esb.mep.requestcallback.beans;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class JmsUriConfiguration {

	public enum UriEncoding {
		NONE, PARTIAL, FULL
	}

	private String variant = null;
	private String destinationName = null;
	private final NavigableMap<String, String> parameters = new TreeMap<String, String>();
	private UriEncoding uriEncode = UriEncoding.PARTIAL;
	private boolean validate = true;

	public JmsUriConfiguration() {
		super();
	}

	public String getVariant() {
		return variant;
	}

	public void setVariant(String variant) {
		if (variant == null) {
			this.variant = null;
			return;
		}
		if ("jndi".equalsIgnoreCase(variant)) {
			this.variant = "jndi";
			return;
		}
		if ("jndi-topic".equalsIgnoreCase(variant)) {
			this.variant = "jndi-topic";
			return;
		}
		if ("queue".equalsIgnoreCase(variant)) {
			this.variant = "queue";
			return;
		}
		if ("topic".equalsIgnoreCase(variant)) {
			this.variant = "topic";
			return;
		}
		if (validate) {
			throw new IllegalArgumentException("Unknown JMS variant " + variant);
		}
		this.variant = variant;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	@Deprecated
	public boolean isEncode() {
		return uriEncode != UriEncoding.NONE;
	}

	@Deprecated
	public void setEncode(boolean encode) {
		this.uriEncode = encode ? UriEncoding.PARTIAL : UriEncoding.NONE;
	}

	public UriEncoding getUriEncode() {
		return uriEncode;
	}

	public void setUriEncode(UriEncoding uriEncode) {
		this.uriEncode = uriEncode;
	}

	public void setUriEncode(String uriEncodeValue) {
		this.uriEncode = toUriEncoding(uriEncodeValue);
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public String getParameter(String name) {
		return parameters.get(name);
	}

	public String putParameter(String name, String value) {
		return parameters.put(name, value);
	}

	public NavigableMap<String, String> getParameters() {
		return parameters;
	}

	public NavigableMap<String, String> getJndiEnvironmentParameters() {
		return parameters.subMap("jndi-", false, "jndi.", false);
	}

	public void applyJmsUri(String jmsUri) {
		if (jmsUri == null || jmsUri.length() == 0 || "jms://".equalsIgnoreCase(jmsUri)) {
			return;
		}
		int ndx = extractPrefix(jmsUri);
		ndx = extractVariant(jmsUri, ndx);
		ndx = extractDestinationName(jmsUri, ndx);
		ndx = extractParameters(jmsUri, ndx);
		if (validate && ndx >= 0) {
			throw new IllegalArgumentException("Fragment not supported in JMS URI ");
		}
	}

	public void clear() {
		variant = null;
		destinationName = null;
		parameters.clear();
	}

	public String toString() {
		if (variant == null || variant.isEmpty()
				|| destinationName == null || destinationName.isEmpty()) {
			return "jms://";
		}
		final StringBuilder buf = new StringBuilder("jms:");
		buf.append(variant).append(':').append(encode(destinationName));
		if (!parameters.isEmpty()) {
			buf.append('?');
			boolean addDelimiter = false;
			for (Entry<String, String> param : parameters.entrySet()) {
				if (addDelimiter) {
					buf.append('&');
				} else {
					addDelimiter = true;
				}
				buf.append(encode(param.getKey()));
				final String value = param.getValue();
				if (value != null && value.length() > 0) {
					buf.append('=').append(encode(value));
				}
			}
		}
		return buf.toString();
	}

	public static UriEncoding toUriEncoding(String uriEncodeValue) {
		if (uriEncodeValue == null) {
			throw new IllegalArgumentException("Invalid URI encoding option value: null");
		}
		if ("none".equalsIgnoreCase(uriEncodeValue) ||
				"false".equalsIgnoreCase(uriEncodeValue) ||
				"no".equalsIgnoreCase(uriEncodeValue) ||
				"n".equalsIgnoreCase(uriEncodeValue) ||
				"off".equalsIgnoreCase(uriEncodeValue)) {
			return UriEncoding.NONE;
		}
		if ("partial".equalsIgnoreCase(uriEncodeValue) ||
				"true".equalsIgnoreCase(uriEncodeValue) ||
				"yes".equalsIgnoreCase(uriEncodeValue) ||
				"y".equalsIgnoreCase(uriEncodeValue) ||
				"on".equalsIgnoreCase(uriEncodeValue)) {
			return UriEncoding.PARTIAL;
		}
		if ("full".equalsIgnoreCase(uriEncodeValue)) {
			return UriEncoding.FULL;
		}
		throw new IllegalArgumentException(
				"Invalid URI encoding option value: " + uriEncodeValue + ". ");
	}

	private int extractPrefix(String jmsUri) {
		final int strLen = jmsUri.length();
		if (strLen < 4 || !"jms:".equalsIgnoreCase(jmsUri.substring(0, 4))) {
			throw new IllegalArgumentException("Not a JMS URI: " + jmsUri);
		}
		return strLen == 4 ? -1 : 4;
	}

	private int extractVariant(String jmsUri, int startIndex) {
		if (startIndex < 0) {
			return -1;
		}
		final int endIndex = jmsUri.indexOf(':', startIndex);
		final String v = endIndex < 0
				? jmsUri.substring(startIndex)
						: jmsUri.substring(startIndex, endIndex);
		if (v.length() > 0) {
			setVariant(v);
		}
		return currentEndIndex(endIndex, jmsUri.length());
	}

	private int extractDestinationName(String jmsUri, int startIndex) {
		if (startIndex < 0) {
			return -1;
		}
		final int strLen = jmsUri.length();
		int bracketLevel = 0;
		for (int endIndex = startIndex; endIndex < strLen; endIndex++) {
			switch (jmsUri.charAt(endIndex)) {
			case '?':
				if (bracketLevel <= 0) {
					final String dName = decode(jmsUri.substring(startIndex, endIndex));
					if (dName.length() > 0) {
						destinationName = dName;
					}
					return currentEndIndex(endIndex, strLen);
				}
				break;
			case '(':
				++bracketLevel;
				break;
			case ')':
				--bracketLevel;
				break;
			default:
				break;
			}
		}
		final String dName = decode(jmsUri.substring(startIndex));
		if (dName.length() > 0) {
			destinationName = dName;
		}
		return -1;
	}

	private int extractParameters(String jmsUri, int startIndex) {
		if (startIndex < 0) {
			return -1;
		}
		final int endIndex = jmsUri.indexOf('#', startIndex);
		final String queryString = endIndex < 0
				? jmsUri.substring(startIndex)
						: jmsUri.substring(startIndex, endIndex);
		addParametersFromQueryString(queryString);
		return currentEndIndex(endIndex, jmsUri.length());
	}

	private void addParametersFromQueryString(String queryString) {
		if (queryString == null || queryString.length() == 0) {
			return;
		}
		final StringTokenizer params = new StringTokenizer(queryString, "&", false);
		while (params.hasMoreTokens()) {
			final String rawEntry = params.nextToken();
			final int ndx = rawEntry.indexOf('=');
			if (ndx < 0) {
				final String key = decode(rawEntry);
				parameters.put(key, "");
			} else {
				final String key = decode(rawEntry.substring(0, ndx));
				final String value = decode(rawEntry.substring(ndx + 1));
				parameters.put(key, value);
			}
		}
	}

	private String decode(String enc) {
		if (uriEncode == UriEncoding.NONE) {
			return enc;
		}
		try {
			return URLDecoder.decode(enc, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unexpected Exception: ", e);
		}
	}

	private String encode(String enc) {
		if (uriEncode == UriEncoding.NONE) {
			return enc;
		}
		try {
			String result = URLEncoder.encode(enc, "UTF-8");
			return uriEncode == UriEncoding.FULL ? result : result.replace("%2F", "/").replace("%3A", ":").
					replace("%28", "(").replace("%29", ")").replace("%2C", ",").replace("%3D", "=");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unexpected Exception: ", e);
		}
	}

	private static int currentEndIndex(int markIndex, int strlen) {
		if (markIndex < 0) {
			return -1;
		}
		int rawResult = markIndex + 1;
		return rawResult < strlen ? rawResult : -1;
	}
}
