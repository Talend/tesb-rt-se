package org.talend.esb.callcontext.store.examples;

import org.talend.esb.callcontext.store.client.common.CallContextStoreClient;
import org.talend.esb.callcontext.store.client.rest.CallContextStoreClientRest;
import org.talend.esb.callcontext.store.common.CallContextFactory;

public  class WriteReadDeleteContext implements Example {
	
	private CallContextStoreClientRest<CallContext> client;
	
	public void startUp(){
		runTest();
	}
	
	public CallContextStoreClient<CallContext> getClient() {
		return client;
	}

	public void setClient(CallContextStoreClientRest<CallContext> client) {
		this.client = client;
	}
	
	private CallContext createCallContext(){
		CallContext ctx = new CallContext();
		ctx.setCallbackId("callbackId");
		return ctx;
	}

	CallContextFactory<CallContext> factory;
	
	private CallContextFactory<CallContext> createCallContextFactory(){
		return new CallContextFactoryImpl<CallContext>();
	}	
	
	private CallContextStoreClientRest<CallContext> createCallContextClient(){
		return new CallContextStoreClientRest<CallContext>();
	}	
	
	@Override
	public void runTest() {
		
	factory = createCallContextFactory();
	client = createCallContextClient();
	client.setCallContextFactory(factory);
	client.setServerURL("htttp://localhost:8040/services/CallbackService");
		
	System.out.println("Test is run");
	
	CallContext ctxStored = createCallContext();
	
	
	System.out.println("Call Context is created with CID: " + ctxStored.getCallbackId() );
	
	String key = client.saveCallContext(ctxStored);
	
	System.out.println("Call Context is saved with key: " + key);
	
	CallContext ctxRestored = client.getCallContext(key);
	
	System.out.println("Call Context is restored with CID: " + ctxRestored.getCallbackId());
	
	client.removeCallContext(key);
	
	System.out.println("Call Context is removed");
	
	System.out.println("Test is finished");
		
	}
	
}
