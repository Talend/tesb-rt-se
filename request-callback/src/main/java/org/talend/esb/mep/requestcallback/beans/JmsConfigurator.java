package org.talend.esb.mep.requestcallback.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Endpoint;

import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.jaxws.DispatchImpl;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.jms.JMSConfigFeature;
import org.apache.cxf.transport.jms.JMSConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.talend.esb.mep.requestcallback.feature.CallContext;
import org.talend.esb.mep.requestcallback.feature.Configuration;

public class JmsConfigurator implements InitializingBean {

	public static final String OVERRIDE_BY_URI_CONFIG = "override";
	private static final Logger LOGGER = LogUtils.getL7dLogger(JmsConfigurator.class);
	private QName serviceName;
	private String configurationPrefix;
	private String workPrefix;
	private Configuration configuration;
	private JMSConfiguration jmsConfiguration;
	private boolean jmsConfigured = false;

	public JmsConfigurator() {
		super();
	}

	public static JmsConfigurator create(Endpoint endpoint) {
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
		JmsConfigurator result = new JmsConfigurator();
		result.setConfigurationPrefix(portName);
		result.setJmsConfiguration(new JMSConfiguration());
		result.setServiceName(serviceName);
		return result;
	}

	public static JmsConfigurator create(JaxWsServerFactoryBean factory) {
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
		JmsConfigurator result = new JmsConfigurator();
		result.setConfigurationPrefix(portName);
		result.setJmsConfiguration(new JMSConfiguration());
		result.setServiceName(serviceName);
		return result;
	}

	public static JmsConfigurator create(Dispatch<?> dispatch) {
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
		QName endpointName;
		try {
			endpointName = cl.getEndpoint().getEndpointInfo().getName();
		} catch (Exception e) {
			if (LOGGER.isLoggable(Level.FINER)) {
				LOGGER.log(Level.FINER, "Exception caught: ", e);
			}
			endpointName = null;
		}
		final String portName = endpointName == null
				? null : endpointName.getLocalPart();
		JmsConfigurator result = new JmsConfigurator();
		result.setConfigurationPrefix(portName);
		result.setJmsConfiguration(new JMSConfiguration());
		result.setServiceName(serviceName);
		return result;
	}

	public Endpoint configureEndpoint(Endpoint endpoint) {
		if (jmsConfiguration == null || !(endpoint instanceof EndpointImpl) ||
				(serviceName == null && configuration == null)) {
			return null;
		}
		if (!jmsConfigured) {
			setupJmsConfiguration();
		}
		final EndpointImpl ei = (EndpointImpl) endpoint;
		final JMSConfigFeature feature = new JMSConfigFeature();
		feature.setJmsConfig(jmsConfiguration);
		List<Feature> features = ei.getFeatures();
		if (features == null) {
			features = new ArrayList<Feature>();
		}
		features.add(feature);
		ei.setFeatures(features);
		return endpoint;
	}

	public Endpoint configureAndPublishEndpoint(Endpoint endpoint, String jmsAddress) {
		String address = jmsAddress;
		if (OVERRIDE_BY_URI_CONFIG.equals(jmsAddress)) {
			if (configuration == null) {
				if (serviceName == null) {
					return null;
				}
				configuration = CallContext.resolveConfiguration(serviceName);
			}
			String overrideAddress = getProperty("requestURI2010");
			if (overrideAddress != null) {
				endpoint.publish(overrideAddress);
				return endpoint;
			}
			address = "jms://";
		}
		Endpoint result = configureEndpoint(endpoint);
		if (result != null) {
			result.publish(address);
		}
		return result;
	}

	public <T> Dispatch<T> configureDispatch(Dispatch<T> dispatch) {
		if (jmsConfiguration == null || !(dispatch instanceof DispatchImpl<?>) ||
				(serviceName == null && configuration == null)) {
			return null;
		}
		if (!jmsConfigured) {
			setupJmsConfiguration();
		}
		final DispatchImpl<?> di = (DispatchImpl<?>) dispatch;
		final Client cl = di.getClient();
		final JMSConfigFeature feature = new JMSConfigFeature();
		feature.setJmsConfig(jmsConfiguration);
		feature.initialize(cl, cl.getBus());
		return dispatch;
	}

	public <T> Dispatch<T> configureDispatch(Dispatch<T> dispatch, String addressing) {
		if (configuration == null) {
			if (serviceName == null) {
				return null;
			}
			configuration = CallContext.resolveConfiguration(serviceName);
		}
		String overrideAddress = getProperty("requestURI2010");
		if (overrideAddress != null &&
				(overrideAddress.equals(addressing) ||
						OVERRIDE_BY_URI_CONFIG.equals(addressing))) {
			return dispatch;
		}
		if (jmsConfiguration == null || !(dispatch instanceof DispatchImpl<?>) ||
				(serviceName == null && configuration == null)) {
			return null;
		}
		if (!jmsConfigured) {
			setupJmsConfiguration();
		}
		final DispatchImpl<?> di = (DispatchImpl<?>) dispatch;
		final Client cl = di.getClient();
		final JMSConfigFeature feature = new JMSConfigFeature();
		feature.setJmsConfig(jmsConfiguration);
		feature.initialize(cl, cl.getBus());
		return dispatch;
	}

	public JaxWsServerFactoryBean configureServerFactory(JaxWsServerFactoryBean serverFactory) {
		if (jmsConfiguration == null || serverFactory == null ||
				(serviceName == null && configuration == null)) {
			return null;
		}
		if (!jmsConfigured) {
			setupJmsConfiguration();
		}
		final JMSConfigFeature feature = new JMSConfigFeature();
		feature.setJmsConfig(jmsConfiguration);
		List<Feature> features = serverFactory.getFeatures();
		if (features == null) {
			features = new ArrayList<Feature>();
		}
		features.add(feature);
		serverFactory.setFeatures(features);
		return serverFactory;
	}

	public JMSConfiguration setupJmsConfiguration() {
		if (jmsConfiguration == null) {
			return null;
		}
		jmsConfigured = true;
		if (configuration == null) {
			configuration = CallContext.resolveConfiguration(serviceName);
		}
		final String cacheLevelName = getProperty("cacheLevelName");
		if (cacheLevelName != null) {
			LOGGER.warning("Obsolete JMS configuration property: \"cacheLevelName\"");
		}
		final Integer cacheLevel = getIntegerProperty("cacheLevel");
		if (cacheLevel != null) {
			LOGGER.warning("Obsolete JMS configuration property: \"cacheLevel\"");
		}
		final Long recoveryInterval = getLongProperty("recoveryInterval");
		if (recoveryInterval != null) {
			LOGGER.warning("Obsolete JMS configuration property: \"recoveryInterval\"");
		}
		final Boolean autoResolveDestination = getBooleanProperty("autoResolveDestination");
		if (autoResolveDestination != null) {
			LOGGER.warning("Obsolete JMS configuration property: \"autoResolveDestination\"");
		}
		final Boolean usingEndpointInfo = getBooleanProperty("usingEndpointInfo");
		if (usingEndpointInfo != null) {
			LOGGER.warning("Obsolete JMS configuration property: \"usingEndpointInfo\"");
		}
		final Boolean messageIdEnabled = getBooleanProperty("messageIdEnabled");
		if (messageIdEnabled != null) {
			LOGGER.warning("Obsolete JMS configuration property: \"messageIdEnabled\"");
		}
		final Boolean messageTimestampEnabled = getBooleanProperty("messageTimestampEnabled");
		if (messageTimestampEnabled != null) {
			LOGGER.warning("Obsolete JMS configuration property: \"messageTimestampEnabled\"");
		}
		final Boolean pubSubNoLocal = getBooleanProperty("pubSubNoLocal");
		if (pubSubNoLocal != null) {
			jmsConfiguration.setPubSubNoLocal(pubSubNoLocal);
		}
		final Long receiveTimeout = getLongProperty("receiveTimeout");
		if (receiveTimeout != null) {
			jmsConfiguration.setReceiveTimeout(receiveTimeout);
		}
		final Long clientReceiveTimeout = getLongProperty("clientReceiveTimeout");
		if (clientReceiveTimeout != null) {
			jmsConfiguration.setReceiveTimeout(clientReceiveTimeout);
		}
		final Long serverReceiveTimeout = getLongProperty("serverReceiveTimeout");
		if (serverReceiveTimeout != null) {
			jmsConfiguration.setServerReceiveTimeout(serverReceiveTimeout);
		}
		final Boolean explicitQosEnabled = getBooleanProperty("explicitQosEnabled");
		if (explicitQosEnabled != null) {
			jmsConfiguration.setExplicitQosEnabled(explicitQosEnabled);
		}
		final Integer deliveryMode = getIntegerProperty("deliveryMode");
		if (deliveryMode != null) {
			jmsConfiguration.setDeliveryMode(deliveryMode);
		}
		final Integer priority = getIntegerProperty("priority");
		if (priority != null) {
			jmsConfiguration.setPriority(priority);
		}
		final Long timeToLive = getLongProperty("timeToLive");
		if (timeToLive != null) {
			jmsConfiguration.setTimeToLive(timeToLive);
		}
		final String messageSelector = getProperty("messageSelector");
		if (messageSelector != null) {
			jmsConfiguration.setMessageSelector(messageSelector);
		}
		final String conduitSelectorPrefix = getProperty("conduitSelectorPrefix");
		if (conduitSelectorPrefix != null) {
			jmsConfiguration.setConduitSelectorPrefix(conduitSelectorPrefix);
		}
		final Boolean subscriptionDurable = getBooleanProperty("subscriptionDurable");
		if (subscriptionDurable != null) {
			jmsConfiguration.setSubscriptionDurable(subscriptionDurable);
		}
		final String durableSubscriptionName = getProperty("durableSubscriptionName");
		if (durableSubscriptionName != null) {
			jmsConfiguration.setDurableSubscriptionName(durableSubscriptionName);
		}
		final String targetDestination = getProperty("targetDestination");
		if (targetDestination != null) {
			jmsConfiguration.setTargetDestination(targetDestination);
		}
		final String replyDestination = getProperty("replyDestination");
		if (replyDestination != null) {
			jmsConfiguration.setReplyDestination(replyDestination);
		}
		final String replyToDestination = getProperty("replyToDestination");
		if (replyToDestination != null) {
			jmsConfiguration.setReplyToDestination(replyToDestination);
		}
		final String messageType = getProperty("messageType");
		if (messageType != null) {
			jmsConfiguration.setMessageType(messageType);
		}
		final Boolean pubSubDomain = getBooleanProperty("pubSubDomain");
		if (pubSubDomain != null) {
			jmsConfiguration.setPubSubDomain(pubSubDomain);
		}
		final Boolean replyPubSubDomain = getBooleanProperty("replyPubSubDomain");
		if (replyPubSubDomain != null) {
			jmsConfiguration.setReplyPubSubDomain(replyPubSubDomain);
		}
		final Boolean useJms11 = getBooleanProperty("useJms11");
		if (useJms11 != null) {
			LOGGER.warning("Obsolete JMS configuration property: \"useJms11\"");
		}
		final Boolean sessionTransacted = getBooleanProperty("sessionTransacted");
		if (sessionTransacted != null) {
			jmsConfiguration.setSessionTransacted(sessionTransacted);
		}
		final Integer concurrentConsumers = getIntegerProperty("concurrentConsumers");
		if (concurrentConsumers != null) {
			jmsConfiguration.setConcurrentConsumers(concurrentConsumers);
		}
		final Integer maxConcurrentConsumers = getIntegerProperty("maxConcurrentConsumers");
		if (maxConcurrentConsumers != null) {
			LOGGER.warning("Obsolete JMS configuration property: \"maxConcurrentConsumers\"");
		}
		final Integer maxSuspendedContinuations = getIntegerProperty("maxSuspendedContinuations");
		if (maxSuspendedContinuations != null) {
			jmsConfiguration.setMaxSuspendedContinuations(maxSuspendedContinuations);
		}
		final Integer reconnectPercentOfMax = getIntegerProperty("reconnectPercentOfMax");
		if (reconnectPercentOfMax != null) {
			jmsConfiguration.setReconnectPercentOfMax(reconnectPercentOfMax);
		}
		final Boolean useConduitIdSelector = getBooleanProperty("useConduitIdSelector");
		if (useConduitIdSelector != null) {
			jmsConfiguration.setUseConduitIdSelector(useConduitIdSelector);
		}
		final Boolean reconnectOnException = getBooleanProperty("reconnectOnException");
		if (reconnectOnException != null) {
			LOGGER.warning("Obsolete JMS configuration property: \"reconnectOnException\"");
		}
		final Boolean acceptMessagesWhileStopping = getBooleanProperty("acceptMessagesWhileStopping");
		if (acceptMessagesWhileStopping != null) {
			LOGGER.warning("Obsolete JMS configuration property: \"acceptMessagesWhileStopping\"");
		}
		final Boolean wrapInSingleConnectionFactory = getBooleanProperty("wrapInSingleConnectionFactory");
		if (wrapInSingleConnectionFactory != null) {
			LOGGER.warning("Obsolete JMS configuration property: \"wrapInSingleConnectionFactory\"");
		}
		final String durableSubscriptionClientId = getProperty("durableSubscriptionClientId");
		if (durableSubscriptionClientId != null) {
			jmsConfiguration.setDurableSubscriptionClientId(durableSubscriptionClientId);
		}
		final String targetService = getProperty("targetService");
		if (targetService != null) {
			jmsConfiguration.setTargetService(targetService);
		}
		final String requestURI = getProperty("requestURI");
		if (requestURI != null) {
			jmsConfiguration.setRequestURI(requestURI);
		}
		final Boolean enforceSpec = getBooleanProperty("enforceSpec");
		if (enforceSpec != null) {
			LOGGER.warning("Obsolete JMS configuration property: \"enforceSpec\"");
		}
		final Boolean jmsProviderTibcoEms = getBooleanProperty("jmsProviderTibcoEms");
		if (jmsProviderTibcoEms != null) {
			jmsConfiguration.setJmsProviderTibcoEms(jmsProviderTibcoEms);
		}
		configureJndi(jmsConfiguration);
		final String connectionFactoryName = getProperty("connectionFactoryName");
		if (connectionFactoryName != null) {
			jmsConfiguration.setConnectionFactoryName(connectionFactoryName);
		}
		final String userName = getProperty("userName");
		if (userName != null) {
			jmsConfiguration.setUserName(userName);
		}
		final String password = getProperty("password");
		if (password != null) {
			jmsConfiguration.setPassword(password);
		}
		return jmsConfiguration;
	}

	public QName getServiceName() {
		return serviceName;
	}

	public void setServiceName(QName serviceName) {
		this.serviceName = serviceName;
	}

	public String getConfigurationPrefix() {
		return configurationPrefix;
	}

	public void setConfigurationPrefix(String configurationPrefix) {
		this.configurationPrefix = configurationPrefix;
		this.workPrefix = configurationPrefix == null
				? null : configurationPrefix + ".";
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public JMSConfiguration getJmsConfiguration() {
		return jmsConfiguration;
	}

	public void setJmsConfiguration(JMSConfiguration jmsConfiguration) {
		this.jmsConfiguration = jmsConfiguration;
	}

	private void configureJndi(JMSConfiguration jmsConfiguration) {
		final Configuration cfg = configuration == null
				? CallContext.resolveConfiguration(serviceName) : configuration;
		final String jndiConnectionFactoryName = getJndiProperty("jndiConnectionFactoryName");
		if (jndiConnectionFactoryName != null) {
			jmsConfiguration.setConnectionFactoryName(jndiConnectionFactoryName);
		}
		final String connectionUserName = getJndiProperty("connectionUserName");
		if (connectionUserName != null) {
			jmsConfiguration.setUserName(connectionUserName);
		}
		final String connectionPassword = getJndiProperty("connectionPassword");
		if (connectionPassword != null) {
			jmsConfiguration.setPassword(connectionPassword);
		}
		Properties env = jmsConfiguration.getJndiEnvironment();
		final boolean hasNoEnv = env == null;
		if (hasNoEnv) {
			env = new Properties();
		}
		cfg.fillProperties("jndiConfig.environment", env);
		if (workPrefix != null) {
			cfg.fillProperties(workPrefix + "jndiConfig.environment", env);
		}
		if (hasNoEnv && !env.isEmpty()) {
			jmsConfiguration.setJndiEnvironment(env);
		}
	}

	private String getProperty(String key) {
		String result = null;
		if (workPrefix != null) {
			result = configuration.getProperty(workPrefix + key);
		}
		return result == null ? configuration.getProperty(key) : result;
	}

	private String getJndiProperty(String key) {
		String result = null;
		if (workPrefix != null) {
			result = configuration.getProperty(workPrefix + "jndiConfig." + key);
		}
		return result == null ? configuration.getProperty("jndiConfig." + key) : result;
	}

	private Boolean getBooleanProperty(String key) {
		Boolean result = null;
		if (workPrefix != null) {
			result = configuration.getBooleanProperty(workPrefix + key);
		}
		return result == null ? configuration.getBooleanProperty(key) : result;
	}

	private Integer getIntegerProperty(String key) {
		Integer result = null;
		if (workPrefix != null) {
			result = configuration.getIntegerProperty(workPrefix + key);
		}
		return result == null ? configuration.getIntegerProperty(key) : result;
	}

	private Long getLongProperty(String key) {
		Long result = null;
		if (workPrefix != null) {
			result = configuration.getLongProperty(workPrefix + key);
		}
		return result == null ? configuration.getLongProperty(key) : result;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (jmsConfiguration == null) {
			throw new IllegalStateException("Missing JMS Configuration. ");
		}
		setupJmsConfiguration();
		jmsConfiguration.afterPropertiesSet();
	}
}
