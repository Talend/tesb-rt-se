package org.talend.esb.mep.requestcallback.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.ws.Dispatch;
import javax.xml.ws.Endpoint;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.jaxws.DispatchImpl;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.jms.JMSConfigFeature;
import org.apache.cxf.transport.jms.JMSConfiguration;
import org.apache.cxf.transport.jms.JNDIConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jndi.JndiTemplate;
import org.talend.esb.mep.requestcallback.feature.CallContext;
import org.talend.esb.mep.requestcallback.feature.Configuration;

public class JmsConfigurator implements InitializingBean {

	private String configurationPrefix;
	private Configuration configuration;
	private JMSConfiguration jmsConfiguration;
	private boolean jmsConfigured = false;

	public Endpoint configureEndpoint(Endpoint endpoint) {
		if (jmsConfiguration == null || !(endpoint instanceof EndpointImpl)) {
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

	public <T> Dispatch<T> configureDispatch(Dispatch<T> dispatch) {
		if (jmsConfiguration == null || !(dispatch instanceof DispatchImpl<?>)) {
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
		if (jmsConfiguration == null || serverFactory == null) {
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
		final Configuration cfg = configuration == null
				? CallContext.getConfiguration() : configuration;
		final String cacheLevelName = cfg.getProperty(prefixedKey("cacheLevelName"));
		if (cacheLevelName != null) {
			jmsConfiguration.setCacheLevelName(cacheLevelName);
		}
		final Integer cacheLevel = cfg.getIntegerProperty(prefixedKey("cacheLevel"));
		if (cacheLevel != null) {
			jmsConfiguration.setCacheLevel(cacheLevel);
		}
		final Long recoveryInterval = cfg.getLongProperty(prefixedKey("recoveryInterval"));
		if (recoveryInterval != null) {
			jmsConfiguration.setRecoveryInterval(recoveryInterval);
		}
		final Boolean autoResolveDestination = cfg.getBooleanProperty(prefixedKey("autoResolveDestination"));
		if (autoResolveDestination != null) {
			jmsConfiguration.setAutoResolveDestination(autoResolveDestination);
		}
		final Boolean usingEndpointInfo = cfg.getBooleanProperty(prefixedKey("usingEndpointInfo"));
		if (usingEndpointInfo != null) {
			jmsConfiguration.setUsingEndpointInfo(usingEndpointInfo);
		}
		final Boolean messageIdEnabled = cfg.getBooleanProperty(prefixedKey("messageIdEnabled"));
		if (messageIdEnabled != null) {
			jmsConfiguration.setMessageIdEnabled(messageIdEnabled);
		}
		final Boolean messageTimestampEnabled = cfg.getBooleanProperty(prefixedKey("messageTimestampEnabled"));
		if (messageTimestampEnabled != null) {
			jmsConfiguration.setMessageTimestampEnabled(messageTimestampEnabled);
		}
		final Boolean pubSubNoLocal = cfg.getBooleanProperty(prefixedKey("pubSubNoLocal"));
		if (pubSubNoLocal != null) {
			jmsConfiguration.setPubSubNoLocal(pubSubNoLocal);
		}
		final Long receiveTimeout = cfg.getLongProperty(prefixedKey("receiveTimeout"));
		if (receiveTimeout != null) {
			jmsConfiguration.setReceiveTimeout(receiveTimeout);
		}
		final Long clientReceiveTimeout = cfg.getLongProperty(prefixedKey("clientReceiveTimeout"));
		if (clientReceiveTimeout != null) {
			jmsConfiguration.setReceiveTimeout(clientReceiveTimeout);
		}
		final Long serverReceiveTimeout = cfg.getLongProperty(prefixedKey("serverReceiveTimeout"));
		if (serverReceiveTimeout != null) {
			jmsConfiguration.setServerReceiveTimeout(serverReceiveTimeout);
		}
		final Boolean explicitQosEnabled = cfg.getBooleanProperty(prefixedKey("explicitQosEnabled"));
		if (explicitQosEnabled != null) {
			jmsConfiguration.setExplicitQosEnabled(explicitQosEnabled);
		}
		final Integer deliveryMode = cfg.getIntegerProperty(prefixedKey("deliveryMode"));
		if (deliveryMode != null) {
			jmsConfiguration.setDeliveryMode(deliveryMode);
		}
		final Integer priority = cfg.getIntegerProperty(prefixedKey("priority"));
		if (priority != null) {
			jmsConfiguration.setPriority(priority);
		}
		final Long timeToLive = cfg.getLongProperty(prefixedKey("timeToLive"));
		if (timeToLive != null) {
			jmsConfiguration.setTimeToLive(timeToLive);
		}
		final String messageSelector = cfg.getProperty(prefixedKey("messageSelector"));
		if (messageSelector != null) {
			jmsConfiguration.setMessageSelector(messageSelector);
		}
		final String conduitSelectorPrefix = cfg.getProperty(prefixedKey("conduitSelectorPrefix"));
		if (conduitSelectorPrefix != null) {
			jmsConfiguration.setConduitSelectorPrefix(conduitSelectorPrefix);
		}
		final Boolean subscriptionDurable = cfg.getBooleanProperty(prefixedKey("subscriptionDurable"));
		if (subscriptionDurable != null) {
			jmsConfiguration.setSubscriptionDurable(subscriptionDurable);
		}
		final String durableSubscriptionName = cfg.getProperty(prefixedKey("durableSubscriptionName"));
		if (durableSubscriptionName != null) {
			jmsConfiguration.setDurableSubscriptionName(durableSubscriptionName);
		}
		final String targetDestination = cfg.getProperty(prefixedKey("targetDestination"));
		if (targetDestination != null) {
			jmsConfiguration.setTargetDestination(targetDestination);
		}
		final String replyDestination = cfg.getProperty(prefixedKey("replyDestination"));
		if (replyDestination != null) {
			jmsConfiguration.setReplyDestination(replyDestination);
		}
		final String replyToDestination = cfg.getProperty(prefixedKey("replyToDestination"));
		if (replyToDestination != null) {
			jmsConfiguration.setReplyToDestination(replyToDestination);
		}
		final String messageType = cfg.getProperty(prefixedKey("messageType"));
		if (messageType != null) {
			jmsConfiguration.setMessageType(messageType);
		}
		final Boolean pubSubDomain = cfg.getBooleanProperty(prefixedKey("pubSubDomain"));
		if (pubSubDomain != null) {
			jmsConfiguration.setPubSubDomain(pubSubDomain);
		}
		final Boolean replyPubSubDomain = cfg.getBooleanProperty(prefixedKey("replyPubSubDomain"));
		if (replyPubSubDomain != null) {
			jmsConfiguration.setReplyPubSubDomain(replyPubSubDomain);
		}
		final Boolean useJms11 = cfg.getBooleanProperty(prefixedKey("useJms11"));
		if (useJms11 != null) {
			jmsConfiguration.setUseJms11(useJms11);
		}
		final Boolean sessionTransacted = cfg.getBooleanProperty(prefixedKey("sessionTransacted"));
		if (sessionTransacted != null) {
			jmsConfiguration.setSessionTransacted(sessionTransacted);
		}
		final Integer concurrentConsumers = cfg.getIntegerProperty(prefixedKey("concurrentConsumers"));
		if (concurrentConsumers != null) {
			jmsConfiguration.setConcurrentConsumers(concurrentConsumers);
		}
		final Integer maxConcurrentConsumers = cfg.getIntegerProperty(prefixedKey("maxConcurrentConsumers"));
		if (maxConcurrentConsumers != null) {
			jmsConfiguration.setMaxConcurrentConsumers(maxConcurrentConsumers);
		}
		final Integer maxSuspendedContinuations = cfg.getIntegerProperty(prefixedKey("maxSuspendedContinuations"));
		if (maxSuspendedContinuations != null) {
			jmsConfiguration.setMaxSuspendedContinuations(maxSuspendedContinuations);
		}
		final Integer reconnectPercentOfMax = cfg.getIntegerProperty(prefixedKey("reconnectPercentOfMax"));
		if (reconnectPercentOfMax != null) {
			jmsConfiguration.setReconnectPercentOfMax(reconnectPercentOfMax);
		}
		final Boolean useConduitIdSelector = cfg.getBooleanProperty(prefixedKey("useConduitIdSelector"));
		if (useConduitIdSelector != null) {
			jmsConfiguration.setUseConduitIdSelector(useConduitIdSelector);
		}
		final Boolean reconnectOnException = cfg.getBooleanProperty(prefixedKey("reconnectOnException"));
		if (reconnectOnException != null) {
			jmsConfiguration.setReconnectOnException(reconnectOnException);
		}
		final Boolean acceptMessagesWhileStopping = cfg.getBooleanProperty(prefixedKey("acceptMessagesWhileStopping"));
		if (acceptMessagesWhileStopping != null) {
			jmsConfiguration.setAcceptMessagesWhileStopping(acceptMessagesWhileStopping);
		}
		final Boolean wrapInSingleConnectionFactory = cfg.getBooleanProperty(prefixedKey("wrapInSingleConnectionFactory"));
		if (wrapInSingleConnectionFactory != null) {
			jmsConfiguration.setWrapInSingleConnectionFactory(wrapInSingleConnectionFactory);
		}
		final String durableSubscriptionClientId = cfg.getProperty(prefixedKey("durableSubscriptionClientId"));
		if (durableSubscriptionClientId != null) {
			jmsConfiguration.setDurableSubscriptionClientId(durableSubscriptionClientId);
		}
		final String targetService = cfg.getProperty(prefixedKey("targetService"));
		if (targetService != null) {
			jmsConfiguration.setTargetService(targetService);
		}
		final String requestURI = cfg.getProperty(prefixedKey("requestURI"));
		if (requestURI != null) {
			jmsConfiguration.setRequestURI(requestURI);
		}
		final Boolean enforceSpec = cfg.getBooleanProperty(prefixedKey("enforceSpec"));
		if (enforceSpec != null) {
			jmsConfiguration.setEnforceSpec(enforceSpec);
		}
		final Boolean jmsProviderTibcoEms = cfg.getBooleanProperty(prefixedKey("jmsProviderTibcoEms"));
		if (jmsProviderTibcoEms != null) {
			jmsConfiguration.setJmsProviderTibcoEms(jmsProviderTibcoEms);
		}
		configureJndi(jmsConfiguration);
		return jmsConfiguration;
	}

	public String getConfigurationPrefix() {
		return configurationPrefix;
	}

	public void setConfigurationPrefix(String configurationPrefix) {
		this.configurationPrefix = configurationPrefix;
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
				? CallContext.getConfiguration() : configuration;
		JNDIConfiguration jndiCfg = jmsConfiguration.getJndiConfig();
		String jndiCfgPrefixRaw = prefixedKey("jndiConfig");
		String jndiCfgPrefix = cfg.getProperty(jndiCfgPrefixRaw);
		if (jndiCfgPrefix == null) {
			jndiCfgPrefix = jndiCfgPrefixRaw;
		}
		jndiCfgPrefix += ".";
		final String jndiConnectionFactoryName = cfg.getProperty(jndiCfgPrefix + "jndiConnectionFactoryName");
		if (jndiConnectionFactoryName != null) {
			if (jndiCfg == null) {
				jndiCfg = new JNDIConfiguration();
				jmsConfiguration.setJndiConfig(jndiCfg);
			}
			jndiCfg.setJndiConnectionFactoryName(jndiConnectionFactoryName);
		}
		final String connectionUserName = cfg.getProperty(jndiCfgPrefix + "connectionUserName");
		if (connectionUserName != null) {
			if (jndiCfg == null) {
				jndiCfg = new JNDIConfiguration();
				jmsConfiguration.setJndiConfig(jndiCfg);
			}
			jndiCfg.setConnectionUserName(connectionUserName);
		}
		final String connectionPassword = cfg.getProperty(jndiCfgPrefix + "connectionPassword");
		if (connectionPassword != null) {
			if (jndiCfg == null) {
				jndiCfg = new JNDIConfiguration();
				jmsConfiguration.setJndiConfig(jndiCfg);
			}
			jndiCfg.setConnectionPassword(connectionPassword);
		}
		String jndiEnvPrefixRaw = jndiCfgPrefix + "environment";
		String jndiEnvPrefix = cfg.getProperty(jndiEnvPrefixRaw);
		if (jndiEnvPrefix == null) {
			jndiEnvPrefix = jndiEnvPrefixRaw;
		}
		Properties env = jndiCfg == null ? null : jndiCfg.getEnvironment();
		final boolean hasNoEnv = env == null;
		if (hasNoEnv) {
			env = new Properties();
		}
		cfg.fillProperties(jndiEnvPrefix, env);
		if (hasNoEnv && !env.isEmpty()) {
			if (jndiCfg == null) {
				jndiCfg = new JNDIConfiguration();
				jmsConfiguration.setJndiConfig(jndiCfg);
			}
			jndiCfg.setEnvironment(env);
		}
		if (!env.isEmpty()) {
			JndiTemplate jt = jmsConfiguration.getJndiTemplate();
			if (jt != null) {
				Properties jtEnv = jt.getEnvironment();
				if (jtEnv != null && jtEnv != env) {
					jtEnv.putAll(env);
					env.putAll(jtEnv);
				}
			} else  {
				jt = new JndiTemplate();
				jmsConfiguration.setJndiTemplate(jt);
			}
			jt.setEnvironment(env);
		}
	}

	private String prefixedKey(String key) {
		return configurationPrefix == null ? key : configurationPrefix + "." + key;
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
