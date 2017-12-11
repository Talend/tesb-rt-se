package org.talend.esb.mep.requestcallback.impl.callcontext;

import org.talend.esb.auxiliary.storage.client.rest.AuxiliaryStorageClientRest;
import org.talend.esb.mep.requestcallback.feature.CallContext;

public class CallContextStore<E> extends AuxiliaryStorageClientRest<E> {

	private static final String CONTEXT_STORE_SERVER_URL_PROPERTY = "aux.store.server.url";	
	private static final String CONTEXT_STORE_SERVER_URL = "http://localhost:8040/services/AuxStorageService";	

	private final String contextStoreServerUrl = resolveServerURL();

	public CallContextStore(){
		super();
		setAuxiliaryObjectFactory(new CallContextFactoryImpl<E>());
		setServerURL(contextStoreServerUrl);
	}

	private static String resolveServerURL() {
		final String result = (String) CallContext.resolveConfiguration(null).get(CONTEXT_STORE_SERVER_URL_PROPERTY);
		return result == null ? CONTEXT_STORE_SERVER_URL : result;
	}
}
