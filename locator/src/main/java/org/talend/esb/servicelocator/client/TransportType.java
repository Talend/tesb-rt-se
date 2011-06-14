package org.talend.esb.servicelocator.client;


public enum TransportType {
    HTTP,
    
    HTTPS,
    
    JMS,
    
    OTHER;
    
    public String getValue() {
        return name();
    }

    public static TransportType fromValue(String v) {
        return valueOf(v);
    }

}