package org.talend.esb.mep.requestcallback.impl.wsdl;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.apache.cxf.wsdl.service.factory.DefaultServiceConfiguration;
import org.talend.esb.mep.requestcallback.feature.CallbackInfo;

public class CallbackDefaultServiceConfiguration extends DefaultServiceConfiguration {

	private CallbackInfo callbackInfo;

	public CallbackDefaultServiceConfiguration(CallbackInfo callbackInfo) {
		super();
		this.callbackInfo = callbackInfo;
	}

	@Override
	public QName getInterfaceName() {
		QName result = callbackInfo.getCallbackPortTypeName();
		return result == null ? super.getInterfaceName() : result;
	}

    @Override
    public Boolean hasOutMessage(Method m) {
        return Boolean.FALSE;
    }

}
