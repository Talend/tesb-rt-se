package org.talend.esb.mep.requestcallback.beans;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Endpoint;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.DispatchImpl;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.model.EndpointInfo;
import org.springframework.beans.factory.InitializingBean;
import org.talend.esb.mep.requestcallback.feature.CallContext;
import org.talend.esb.mep.requestcallback.feature.Configuration;

public class JmsUriConfigurator implements InitializingBean {

	private static final Logger LOGGER = LogUtils.getL7dLogger(JmsUriConfigurator.class);
	private QName serviceName;
	private QName endpointName;
	private String configurationPrefix;
	private String workPrefix;
	private Configuration configuration;
	private String presetJmsAddress;
	private String variant;
	private String defaultVariant;
	private String destinationName;
	private String defaultDestinationName;
	private Map<?, ?> parameters;
	private Map<?, ?> defaultParameters;
	private String jmsAddress;
	private JmsUriConfiguration.UriEncoding encodeURI =
			JmsUriConfiguration.UriEncoding.PARTIAL;

	public JmsUriConfigurator() {
		super();
	}

	public static JmsUriConfigurator create(Endpoint endpoint) {
		if (!(endpoint instanceof EndpointImpl)) {
			return null;
		}
		final EndpointImpl ep = (EndpointImpl) endpoint;
		final QName serviceName = ep.getServiceName();
		if (serviceName == null) {
			return null;
		}
		final QName endpointName = ep.getEndpointName();
		final String portName = endpointName == null
				? null : endpointName.getLocalPart();
		JmsUriConfigurator result = new JmsUriConfigurator();
		result.setConfigurationPrefix(portName);
		result.setServiceName(serviceName);
		return result;
	}

	public static JmsUriConfigurator create(JaxWsServerFactoryBean factory) {
		if (factory == null) {
			return null;
		}
		final QName serviceName = factory.getServiceName();
		if (serviceName == null) {
			return null;
		}
		final QName endpointName = factory.getEndpointName();
		final String portName = endpointName == null
				? null : endpointName.getLocalPart();
		JmsUriConfigurator result = new JmsUriConfigurator();
		result.setConfigurationPrefix(portName);
		result.setServiceName(serviceName);
		return result;
	}

	public static JmsUriConfigurator create(Dispatch<?> dispatch) {
		if (!(dispatch instanceof DispatchImpl<?>)) {
			return null;
		}
		DispatchImpl<?> dsp = (DispatchImpl<?>) dispatch;
		Client cl = dsp.getClient();
		final QName serviceName;
		try {
			serviceName = cl.getEndpoint().getService().getName();
		} catch (Exception e) {
			if (LOGGER.isLoggable(Level.FINER)) {
				LOGGER.log(Level.FINER, "Exception caught: ", e);
			}
			return null;
		}
		if (serviceName == null) {
			return null;
		}
		EndpointInfo endpointInfo;
		QName endpointName;
		String endpointAddress;
		try {
			endpointInfo = cl.getEndpoint().getEndpointInfo();
			endpointName = endpointInfo.getName();
			endpointAddress = endpointInfo.getAddress();
		} catch (Exception e) {
			if (LOGGER.isLoggable(Level.FINER)) {
				LOGGER.log(Level.FINER, "Exception caught: ", e);
			}
			endpointInfo = null;
			endpointName = null;
			endpointAddress = null;
		}
		final String portName = endpointName == null
				? null : endpointName.getLocalPart();
		JmsUriConfigurator result = new JmsUriConfigurator();
		result.setConfigurationPrefix(portName);
		result.setServiceName(serviceName);
		if (endpointAddress != null && endpointAddress.startsWith("jms:")) {
			result.setPresetJmsAddress(endpointAddress);
		}
		return result;
	}

	public <T> Dispatch<T> configureDispatch(Dispatch<T> dispatch) {
		if (!(dispatch instanceof DispatchImpl<?>) ||
				(serviceName == null && configuration == null)) {
			return null;
		}
		final DispatchImpl<?> di = (DispatchImpl<?>) dispatch;
		final Client cl = di.getClient();
		try {
			String jmsAddr = getJmsAddress();
			cl.getRequestContext().put(Message.ENDPOINT_ADDRESS, jmsAddr);
			cl.getEndpoint().getEndpointInfo().setAddress(jmsAddr);
		} catch (Exception e) {
			if (LOGGER.isLoggable(Level.FINER)) {
				LOGGER.log(Level.FINER, "Exception caught: ", e);
			}
			return null;
		}
		return dispatch;
	}

	public String getPresetJmsAddress() {
		return presetJmsAddress;
	}

	public void setPresetJmsAddress(String presetJmsAddress) {
		this.presetJmsAddress = presetJmsAddress;
	}

	public String getVariant() {
		return variant;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}

	public String getDefaultVariant() {
		return defaultVariant;
	}

	public void setDefaultVariant(String defaultVariant) {
		this.defaultVariant = defaultVariant;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public String getDefaultDestinationName() {
		return defaultDestinationName;
	}

	public void setDefaultDestinationName(String defaultDestinationName) {
		this.defaultDestinationName = defaultDestinationName;
	}

	public Map<?, ?> getParameters() {
		return parameters;
	}

	public void setParameters(Map<?, ?> parameters) {
		this.parameters = parameters;
	}

	public Map<?, ?> getDefaultParameters() {
		return defaultParameters;
	}

	public void setDefaultParameters(Map<?, ?> defaultParameters) {
		this.defaultParameters = defaultParameters;
	}

	public String getJmsAddress() {
		if (jmsAddress == null) {
			jmsAddress = createJmsAddress();
		}
		return jmsAddress;
	}

	public String resetJmsAddress() {
		String result = jmsAddress;
		jmsAddress = null;
		return result;
	}

	public JmsUriConfiguration createJmsUriConfiguration() {
		if (configuration == null) {
			configuration = CallContext.resolveConfiguration(serviceName);
		}
		final JmsUriConfiguration jmsConfig = new JmsUriConfiguration();
		String prop = getProperty("encodeURI");
		if (nonzero(prop)) {
			try {
				setEncodeURI(prop);
			} catch (IllegalArgumentException e) {
				// ignore - incorrect value leaves default
			}
		}
		jmsConfig.setUriEncode(encodeURI);
		if (nonzero(defaultVariant)) {
			jmsConfig.setVariant(defaultVariant);
		}
		if (nonzero(defaultDestinationName)) {
			jmsConfig.setDestinationName(defaultDestinationName);
		}
		copyParams(defaultParameters, jmsConfig);
		prop = getProperty("jmsAddress");
		if (nonzero(prop)) {
			jmsConfig.applyJmsUri(prop);
		}
		prop = getProperty("variant");
		if (nonzero(prop)) {
			jmsConfig.setVariant(prop);
		}
		prop = getProperty("destinationName");
		if (nonzero(prop)) {
			jmsConfig.setDestinationName(prop);
		}
		addConfigParamsTo(jmsConfig);
		if (nonzero(presetJmsAddress)) {
			jmsConfig.applyJmsUri(presetJmsAddress);
		}
		if (nonzero(variant)) {
			jmsConfig.setVariant(variant);
		}
		if (nonzero(destinationName)) {
			jmsConfig.setDestinationName(destinationName);
		}
		copyParams(parameters, jmsConfig);
		return jmsConfig;
	}

	public String createJmsAddress() {
		return createJmsUriConfiguration().toString();
	}

	public QName getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = validQName(serviceName);
	}

	public void setServiceName(QName serviceName) {
		this.serviceName = validQName(serviceName);
	}

	public QName getEndpointName() {
		return endpointName;
	}

	public void setEndpointName(String endpointName) {
		this.endpointName = validQName(endpointName);
		setConfigurationPrefix();
	}

	public void setEndpointName(QName endpointName) {
		this.endpointName = validQName(endpointName);
		setConfigurationPrefix();
	}

	public String getConfigurationPrefix() {
		return configurationPrefix;
	}

	public void setConfigurationPrefix(String configurationPrefix) {
		this.configurationPrefix = configurationPrefix;
		this.workPrefix = nonzero(configurationPrefix)
				? configurationPrefix + "." : null;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public boolean isEncodeURI() {
		return encodeURI != JmsUriConfiguration.UriEncoding.NONE;
	}

	public void setEncodeURI(boolean encodeURIValue) {
		this.encodeURI = encodeURIValue
				? JmsUriConfiguration.UriEncoding.PARTIAL
				: JmsUriConfiguration.UriEncoding.NONE;
	}

	public void setEncodeURI(String encodeURIValue) {
		encodeURI = JmsUriConfiguration.toUriEncoding(encodeURIValue);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		jmsAddress = createJmsAddress();
	}

	private void setConfigurationPrefix() {
		setConfigurationPrefix(
				endpointName == null ? null : endpointName.getLocalPart());
	}

	private String getProperty(String key) {
		String result = null;
		if (workPrefix != null) {
			result = configuration.getProperty(workPrefix + key);
		}
		return result == null ? configuration.getProperty(key) : result;
	}

	private void addConfigParamsTo(JmsUriConfiguration target) {
		if (nonzero(configuration)) {
			final Map<String, String> params = target.getParameters();
			final List<String> excludes = new LinkedList<String>();
			excludes.add("variant");
			excludes.add("destinationName");
			excludes.add("jmsAddress");
			excludes.add("nonJmsAddress");
			excludes.add("encodeURI");
			if (workPrefix != null) {
				excludes.add(workPrefix + "variant");
				excludes.add(workPrefix + "destinationName");
				excludes.add(workPrefix + "jmsAddress");
				excludes.add(workPrefix + "nonJmsAddress");
				excludes.add(workPrefix + "encodeURI");
			}
			for (Entry<String, Object> e : configuration.entrySet()) {
				final Object value = e.getValue();
				if (value == null) {
					continue;
				}
				final String key = validKey(e.getKey(), excludes);
				if (key != null) {
					params.put((String) key, value.toString());
				}
			}
		}
	}

	private String validKey(String sample, List<String> excludes) {
		if (sample == null || sample.length() == 0) {
			return null;
		}
		for (String exclude : excludes) {
			if (sample.equals(exclude)) {
				return null;
			}
		}
		final int ndx = sample.indexOf('.');
		if (ndx < 0) {
			return sample;
		}
		if (workPrefix != null && sample.startsWith(workPrefix)) {
			final String s = sample.substring(workPrefix.length());
			final int n = s.indexOf('.');
			if (n >= 0) {
				return null;
			}
			excludes.add(s);
			return s;
		}
		return null;
	}

	private static void copyParams(Map<?, ?> source, JmsUriConfiguration target) {
		if (nonzero(source)) {
			final Map<String, String> params = target.getParameters();
			for (Entry<?, ?> e : source.entrySet()) {
				final Object key = e.getKey();
				final Object value = e.getValue();
				if ((key instanceof String) && (value != null)) {
					params.put((String) key, value.toString());
				}
			}
		}
	}

	private static boolean nonzero(String string) {
		return string != null && string.length() > 0;
	}

	private static boolean nonzero(Map<?, ?> map) {
		return map != null && !map.isEmpty();
	}

	private static QName validQName(String value) {
		if (value == null) {
			return null;
		}
		if (value.startsWith("{")) {
			final int ndx = value.indexOf('}', 1);
			if (ndx < 0) {
				return null;
			}
			return new QName(
					value.substring(1, ndx),
					value.substring(ndx + 1));
		}
		final int cndx = value.indexOf(':');
		if (cndx < 0) {
			return new QName(value);
		}
		return new QName(value.substring(cndx + 1));
	}

	private static QName validQName(QName qname) {
		if (qname == null) {
			return null;
		}
		if (nonzero(qname.getNamespaceURI())) {
			return qname;
		}
		return validQName(qname.getLocalPart());
	}
}
