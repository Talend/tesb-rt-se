package org.talend.esb.mep.requestcallback.impl.callcontext;

import org.talend.esb.auxiliary.storage.client.rest.AuxiliaryStorageClientRest;

public class CallContextStore<E> extends AuxiliaryStorageClientRest<E> {
	
	final static String CONTEXT_STORE_SERVER_URL = "http://localhost:8040/services/AuxStorageService";	
	
	public CallContextStore(){
		super();
		setAuxiliaryObjectFactory(new CallContextFactoryImpl<E>());
		setServerURL(CONTEXT_STORE_SERVER_URL);
	}
}
