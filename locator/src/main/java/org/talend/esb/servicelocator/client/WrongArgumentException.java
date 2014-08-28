package org.talend.esb.servicelocator.client;

public class WrongArgumentException extends ServiceLocatorException {

    private static final long serialVersionUID = -6334940661790600528L;

    public WrongArgumentException() {
    }

    public WrongArgumentException(String msg) {
        super(msg);
    }

    public WrongArgumentException(Throwable cause) {
        super(cause);
    }

    public WrongArgumentException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
