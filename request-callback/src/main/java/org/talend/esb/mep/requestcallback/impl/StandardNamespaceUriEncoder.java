package org.talend.esb.mep.requestcallback.impl;

import org.talend.esb.mep.requestcallback.impl.AbstractConfiguration.NamespaceUriEncoder;

public class StandardNamespaceUriEncoder implements NamespaceUriEncoder {

	@Override
	public String encodedNamespaceURI(String namespaceURI) {
		final int strlen = namespaceURI.length();
		final StringBuilder buf = new StringBuilder();
		boolean compact = false;
		boolean pendingAppend = false;
		char lastChar = '-';
		for (int i = 0; i < strlen; i++) {
			char currentChar = namespaceURI.charAt(i);
			switch (currentChar) {
			case ' ':
			// Replace any spaces in the config file name
				currentChar = '_';
				compact = false;
				break;
			// Delimiter and sub-delimiter characters according to RFC 3986
			// are replaced by '-' and compacted in the config file name
			case ':':
			case '/':
			case '?':
			case '#':
			case '[':
			case ']':
			case '@':
			case '!':
			case '$':
			case '&':
			case '\'':
			case '(':
			case ')':
			case '*':
			case '+':
			case ',':
			case ';':
			case '=':
				currentChar = '-';
				compact = true;
				break;
			default:
				compact = false;
				break;
			}
			if (compact) {
				if (!pendingAppend) {
					pendingAppend = lastChar != '-';
				}
			} else {
				if (pendingAppend) {
					pendingAppend = false;
					if (currentChar != '-') {
						buf.append('-');
					}
				}
				buf.append(currentChar);
			}
			lastChar = currentChar;
		}
		return buf.toString();
	}

}
