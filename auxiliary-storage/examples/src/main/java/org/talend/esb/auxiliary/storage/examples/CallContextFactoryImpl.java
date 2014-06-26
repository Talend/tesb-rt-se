package org.talend.esb.auxiliary.storage.examples;

import org.talend.esb.auxiliary.storage.common.AuxiliaryObjectFactory;

public class CallContextFactoryImpl<E> implements AuxiliaryObjectFactory<E> {

	@Override
	public String marshalObject(E ctx) {
		if(ctx instanceof CallContext){
			return ((CallContext) ctx).getCallbackId();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E unmarshallObject(String marshalledData) {
		CallContext ctx =  new CallContext();
		ctx.setCallbackId(marshalledData);

		return (E)ctx ;
	}

	@Override
	public String createObjectKey(E ctx) {
		if(ctx instanceof CallContext){
			return ((CallContext) ctx).getCallbackId();
		}
		return null;
	}

}
