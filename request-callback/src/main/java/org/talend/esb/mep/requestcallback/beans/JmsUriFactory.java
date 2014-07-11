package org.talend.esb.mep.requestcallback.beans;

import java.util.Properties;

import javax.xml.namespace.QName;

import org.springframework.beans.factory.FactoryBean;

public class JmsUriFactory extends JmsUriConfigurator implements FactoryBean<Properties> {

	public static final String CALLBACK_SERVICE_NAME_PROP = "callback.service.name";
	public static final String CALLBACK_ENDPOINT_NAME_PROP = "callback.endpoint.name";
	public static final String CALLBACK_ENDPOINT_ADDRESS_PROP = "callback.endpoint.address";

	@Override
	public Properties getObject() throws Exception {
		return createProperties();
	}

	@Override
	public Class<?> getObjectType() {
		return Properties.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	private Properties createProperties() {
		final Properties result = new Properties();
		final QName serviceName = getServiceName();
		if (serviceName != null) {
			result.put(CALLBACK_SERVICE_NAME_PROP, serviceName);
		}
		final QName endpointName = getEndpointName();
		if (endpointName != null) {
			result.put(CALLBACK_ENDPOINT_NAME_PROP, endpointName);
		}
		final String jmsAddress = getJmsAddress();
		if (jmsAddress != null) {
			result.put(CALLBACK_ENDPOINT_ADDRESS_PROP, jmsAddress);
		}
		return result;
	}
}
