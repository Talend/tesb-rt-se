package org.talend.esb.servicelocator.client;

public class EndpointNotFoundException extends ServiceLocatorException {

    private static final long serialVersionUID = 8489404992424291899L;

    public EndpointNotFoundException() {
    }

    public EndpointNotFoundException(String msg) {
        super(msg);
    }

    public EndpointNotFoundException(Throwable cause) {
        super(cause);
    }

    public EndpointNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
