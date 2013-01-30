package org.talend.esb.sam.service.exception;

public class ResourceNotFoundException extends SamServiceException {

    /**
     *
     */
    private static final long serialVersionUID = -5067314390133284214L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable e) {
        super(message, e);
    }

}
