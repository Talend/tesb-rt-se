package org.talend.esb.auxiliary.storage.examples;

import org.talend.esb.auxiliary.storage.client.common.AuxiliaryStorageClient;
import org.talend.esb.auxiliary.storage.client.rest.AuxiliaryStorageClientRest;
import org.talend.esb.auxiliary.storage.common.AuxiliaryObjectFactory;

public  class WriteReadDeleteContext implements Example {

	private AuxiliaryStorageClientRest<CallContext> client;

	public void startUp(){
		runTest();
	}

	public AuxiliaryStorageClient<CallContext> getClient() {
		return client;
	}

	public void setClient(AuxiliaryStorageClientRest<CallContext> client) {
		this.client = client;
	}

	private CallContext createCallContext(){
		CallContext ctx = new CallContext();
		ctx.setCallbackId("callbackId");
		return ctx;
	}

	AuxiliaryObjectFactory<CallContext> factory;

	private AuxiliaryObjectFactory<CallContext> createCallContextFactory(){
		return new CallContextFactoryImpl<CallContext>();
	}

	private AuxiliaryStorageClientRest<CallContext> createCallContextClient(){
		return new AuxiliaryStorageClientRest<CallContext>();
	}

	@Override
	public void runTest() {

	factory = createCallContextFactory();
	client = createCallContextClient();
	client.setAuxiliaryObjectFactory(factory);
	client.setServerURL("http://localhost:8040/services/AuxStorageService");

	System.out.println("Test is run");

	CallContext ctxStored = createCallContext();


	System.out.println("Call Context is created with CID: " + ctxStored.getCallbackId() );

	String key = client.saveObject(ctxStored);

	System.out.println("Call Context is saved with key: " + key);

	CallContext ctxRestored = client.getStoredObject(key);

	System.out.println("Call Context is restored with CID: " + ctxRestored.getCallbackId());

	client.removeStoredObject(key);

	System.out.println("Call Context is removed");

	System.out.println("Test is finished");

	}

}
