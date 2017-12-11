package org.talend.esb.auxiliary.storage.examples;

import org.talend.esb.auxiliary.storage.common.AuxiliaryObjectFactory;

public class CallContextFactoryImpl<E> implements AuxiliaryObjectFactory<E> {

    @Override
    public String marshalObject(E ctx) {
        if(ctx instanceof CallContext){
            return ((CallContext) ctx).getCallbackId();
        } else {
            throw new IllegalArgumentException("Marshalled object should be an instance of "
                        + " org.talend.esb.auxiliary.storage.examples.CallContext  class");
        }
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

    @Override
    public String contentType() {
        return "application/octet-stream";
    }

}
