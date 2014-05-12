package org.talend.esb.callcontext.store.examples;

import org.talend.esb.callcontext.store.common.CallContextFactory;

public class CallContextFactoryImpl<E> implements CallContextFactory<E> {

	@Override
	public String marshalCallContext(E ctx) {
		if(ctx instanceof CallContext){
			return ((CallContext) ctx).getCallbackId();
		}
		
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E unmarshallCallContext(String marshalledData) {
		CallContext ctx =  new CallContext();
		ctx.setCallbackId(marshalledData);
		
		return (E)ctx ;
	}

	@Override
	public String createCallContextKey(E ctx) {
		if(ctx instanceof CallContext){
			return ((CallContext) ctx).getCallbackId();
		}
		return null;
	}

}
