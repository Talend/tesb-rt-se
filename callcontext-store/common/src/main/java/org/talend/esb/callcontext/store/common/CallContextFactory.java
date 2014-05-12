package org.talend.esb.callcontext.store.common;

public interface  CallContextFactory<E> {
	
	public String marshalCallContext(E ctx);
	
	public E unmarshallCallContext(String marshalledData);
	
	public String createCallContextKey(E ctx);
}
