package org.talend.esb.mep.requestcallback.impl.callcontext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.common.logging.LogUtils;
import org.talend.esb.auxiliary.storage.common.AuxiliaryObjectFactory;
import org.talend.esb.mep.requestcallback.feature.CallContext;

public class CallContextFactoryImpl<E> implements AuxiliaryObjectFactory<E> {

    private static final Logger LOGGER = LogUtils.getL7dLogger(CallContextFactoryImpl.class);

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
                if (LOGGER.isLoggable(Level.FINER)) {
                    LOGGER.log(Level.FINER, "Exception caught: ", e);
                }
            }

            return new String( Base64Coder.encode( baos.toByteArray() ) );

        } else {
            throw new IllegalArgumentException("Marshalled object should implement "
                            + " java.io.Serializable");
        }

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
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.log(Level.FINER, "Exception caught: ", e);
            }
        } catch (ClassNotFoundException e) {
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.log(Level.FINER, "Exception caught: ", e);
            }
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

    @Override
    public String contentType() {
        return "application/octet-stream";
    }

    private String prettifyCallContextKey(String key){
        return key == null ? null : key.replace(':', '-');
    }
}
