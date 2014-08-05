package org.talend.esb.mep.requestcallback.impl.callcontext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.talend.esb.auxiliary.storage.common.AuxiliaryObjectFactory;
import org.talend.esb.mep.requestcallback.feature.CallContext;

public class CallContextFactoryImpl<E> implements AuxiliaryObjectFactory<E> {
	
	@Override
	public String marshalObject(E ctx) {
		if(ctx instanceof Serializable){
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream( baos );
		        oos.writeObject(ctx);
		        oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

	        return new String( Base64Coder.encode( baos.toByteArray() ) );
		}
		
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public E unmarshallObject(String marshalledData) {
		
        byte [] data = Base64Coder.decode( marshalledData );
        ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object ctx  = ois.readObject();
			ois.close();
			return (E) ctx;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        
		return null ;
	}

	@Override
	public String createObjectKey(E ctxObj) {
		if(ctxObj instanceof CallContext){
			CallContext ctx = (CallContext)ctxObj;
			String key = ctx.getCallId();
			return prettifyCallContextKey(key);
		}
		
		return null;
	}
	
	private String prettifyCallContextKey(String key){
		String pkey = null;
		if(key!=null){
			return key.replace(':', '-');
		}
		return pkey;
	}

}
