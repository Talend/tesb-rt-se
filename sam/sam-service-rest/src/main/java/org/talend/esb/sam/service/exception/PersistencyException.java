package org.talend.esb.sam.service.exception;

public class PersistencyException extends SamServiceException {

    /**
     *
     */
    private static final long serialVersionUID = 7871176774544240702L;

    public PersistencyException(String message) {
        super(message);
    }

    public PersistencyException(String message, Throwable e) {
        super(message, e);
    }

}
