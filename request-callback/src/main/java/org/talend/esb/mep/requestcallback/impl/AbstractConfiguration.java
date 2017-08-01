package org.talend.esb.mep.requestcallback.impl;

import java.util.Dictionary;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.cxf.common.logging.LogUtils;
import org.talend.esb.mep.requestcallback.feature.Configuration;

public abstract class AbstractConfiguration implements Configuration {

	private static final Logger LOGGER = LogUtils.getL7dLogger(AbstractConfiguration.class);

	public interface NamespaceUriEncoder {
		String encodedNamespaceURI(String namespaceURI);
	}

	public static final String CONFIG_ID_PREFIX = "org.talend.esb.mep.requestcallback";

	private static NamespaceUriEncoder namespaceUriEncoder =
			new StandardNamespaceUriEncoder();

	protected AbstractConfiguration() {
		super();
	}

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
			if (LOGGER.isLoggable(Level.FINER)) {
				LOGGER.log(Level.FINER, "Exception caught: ", e);
			}
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
			if (LOGGER.isLoggable(Level.FINER)) {
				LOGGER.log(Level.FINER, "Exception caught: ", e);
			}
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
	public PidMode getPidModeProperty(String key) {
		final Object raw = get(key);
		if (raw == null) {
			return null;
		}
		if (raw instanceof PidMode) {
			return (PidMode) raw;
		}
		final String s = raw.toString();
		if ("fullName".equalsIgnoreCase(s)) {
			return PidMode.FULL_NAME;
		}
		if ("localName".equalsIgnoreCase(s)) {
			return PidMode.LOCAL_NAME;
		}
		return null;
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
	public QName getConfigurationName() {
		return null;
	}

	@Override
	public String getConfigurationIdentifier() {
		return null;
	}

	@Override
	public String getAlternateConfigurationIdentifier() {
		return null;
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
			return;
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

	public static String asConfigIdentifier(QName serviceName) {
		if (serviceName == null) {
			return CONFIG_ID_PREFIX;
		}
		final StringBuilder buf = new StringBuilder(CONFIG_ID_PREFIX);
		String namespaceName = serviceName.getNamespaceURI();
		if (namespaceName != null && namespaceName.length() > 0) {
			buf.append(".").append(
					namespaceUriEncoder.encodedNamespaceURI(namespaceName));
		}
		String localName = serviceName.getLocalPart();
		if (localName != null && localName.length() > 0) {
			buf.append(".").append(localName);
		}
		return buf.toString();
	}

	public static String asConfigIdentifier(String serviceLocalName) {
		if (serviceLocalName == null || serviceLocalName.length() == 0) {
			return CONFIG_ID_PREFIX;
		}
		if (serviceLocalName.startsWith("{")) {
			return asConfigIdentifier(QName.valueOf(serviceLocalName));
		}
		return CONFIG_ID_PREFIX + "." + serviceLocalName;
	}

	public static NamespaceUriEncoder getNamespaceUriEncoder() {
		return namespaceUriEncoder;
	}

	public static void setNamespaceUriEncoder(NamespaceUriEncoder encoder) {
		namespaceUriEncoder = encoder;
	}
}
