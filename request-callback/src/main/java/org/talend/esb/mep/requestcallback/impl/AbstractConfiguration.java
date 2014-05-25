package org.talend.esb.mep.requestcallback.impl;

import java.util.Dictionary;
import java.util.Map;

import org.talend.esb.mep.requestcallback.feature.Configuration;

public abstract class AbstractConfiguration implements Configuration {

	@Override
	public String getProperty(String key) {
		final Object raw = get(key);
		return raw == null ? null : raw.toString();
	}

	@Override
	public Integer getIntegerProperty(String key) {
		final Object raw = get(key);
		if (raw == null) {
			return null;
		}
		if (raw instanceof Integer) {
			return (Integer) raw;
		}
		if (raw instanceof Number) {
			return new Integer(((Number) raw).intValue());
		}
		try {
			return Integer.valueOf(raw.toString());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	public Long getLongProperty(String key) {
		final Object raw = get(key);
		if (raw == null) {
			return null;
		}
		if (raw instanceof Long) {
			return (Long) raw;
		}
		if (raw instanceof Number) {
			return new Long(((Number) raw).longValue());
		}
		try {
			return Long.valueOf(raw.toString());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	public Boolean getBooleanProperty(String key) {
		final Object raw = get(key);
		if (raw == null) {
			return null;
		}
		if (raw instanceof Boolean) {
			return (Boolean) raw;
		}
		return Boolean.valueOf(raw.toString());
	}

	@Override
	public String getExpandedProperty(String key) {
		return expandedValue(get(key), this);
	}

	@Override
	public void fillProperties(String prefix,
			Map<? super String, Object> properties) {

		transferProperties(prefix, this, properties);
	}

	@Override
	public void fillExpandedProperties(String prefix,
			Map<? super String, Object> properties) {

		transferExpandedProperties(prefix, this, properties, this);
	}

	@Override
	public void updateDynamicConfiguration(
			Map<?, ?> updateMap, boolean replaceCurrent) {
		// empty default implementation
	}

	@Override
	public void updateDynamicConfiguration(
			Dictionary<?, ?> updateDict, boolean replaceCurrent) {
		// empty default implementation
	}

	@Override
	public void refreshStaticConfiguration() {
		// empty default implementation
	}

	@Override
	public ChangeListener getChangeListener() {
		return null;
	}

	@Override
	public void setChangeListener(ChangeListener changeListener) {
		// empty default implementation
	}

	public static String expandedValue(
			final Object rawValue, final Map<?, ?> replacements) {
		if (rawValue == null) {
			return null;
		}
		final String input = rawValue.toString();
		int varStart = input.indexOf("${");
		if (varStart < 0) {
			return input;
		}
		final int strlen = input.length();
		StringBuilder buf = new StringBuilder(input.substring(0, varStart));
		varStart += 2;
		while (varStart < strlen) {
			int varEnd = input.indexOf("}", varStart);
			if (varEnd < 0) {
				varEnd = strlen;
			}
			String varKey = input.substring(varStart, varEnd);
			Object varValue = varKey.length() > 0 ? replacements.get(varKey) : null;
			if (varValue != null) {
				buf.append(varValue.toString());
			}
			varEnd += 1;
			varStart = varEnd;
			if (varEnd < strlen) {
				varStart = input.indexOf("${", varEnd);
				if (varStart < 0) {
					varStart = strlen;
				}
				buf.append(input.substring(varEnd, varStart));
			}
			varStart += 2;
		}
		return buf.toString();
	}

	public static void transferProperties(String prefix,
			Map<String, Object> source, Map<? super String, Object> target) {

		final String fullPrefix = prefix == null ? null : prefix + ".";
		if (fullPrefix == null) {
			target.putAll(source);
		}
		for (Entry<String, Object> e : source.entrySet()) {
			String key = e.getKey();
			 if (key.startsWith(fullPrefix)) {
				target.put(key.substring(fullPrefix.length()), e.getValue());
			}
		}
	}

	public static void transferExpandedProperties(String prefix,
			Map<String, Object> source, Map<? super String, Object> target,
			Map<?, ?> replacements) {

		final String fullPrefix = prefix == null ? null : prefix + ".";
		for (Entry<String, Object> e : source.entrySet()) {
			String key = e.getKey();
			if (fullPrefix ==  null) {
				target.put(key, expandedValue(e.getValue(), replacements));
			} else if (key.startsWith(fullPrefix)) {
				target.put(key.substring(fullPrefix.length()),
						expandedValue(e.getValue(), replacements));
			}
		}
	}
}
