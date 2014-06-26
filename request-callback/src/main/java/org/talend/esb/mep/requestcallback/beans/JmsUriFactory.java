package org.talend.esb.mep.requestcallback.beans;

import org.springframework.beans.factory.FactoryBean;

public class JmsUriFactory extends JmsUriConfigurator implements FactoryBean<String> {

	@Override
	public String getObject() throws Exception {
		return getJmsAddress();
	}

	@Override
	public Class<?> getObjectType() {
		return String.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}
}
