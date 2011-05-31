package org.talend.esb.job.controller.internal;

import javax.xml.namespace.QName;

public class ESBProviderKey {

	private final QName serviceName;
	private final QName portName;

	public ESBProviderKey(
			final QName serviceName,
			final QName portName) {
		this.serviceName = serviceName;
		this.portName = portName;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ESBProviderKey) {
			ESBProviderKey anotherObj = (ESBProviderKey)obj;
			return serviceName.equals(anotherObj.serviceName) && portName.equals(anotherObj.portName);
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return serviceName.hashCode() ^ portName.hashCode();
	}
}
