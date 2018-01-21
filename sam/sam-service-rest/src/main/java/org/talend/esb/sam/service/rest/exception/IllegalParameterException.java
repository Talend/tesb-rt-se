package org.talend.esb.sam.service.rest.exception;

public class IllegalParameterException extends SamServiceException {

    /**
     *
     */
    private static final long serialVersionUID = -5679973253162838190L;

    public IllegalParameterException(String message) {
        super(message);
    }

    public IllegalParameterException(String message, Throwable e) {
        super(message, e);
    }

}
