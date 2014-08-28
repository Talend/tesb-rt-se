package org.talend.esb.servicelocator.client;

public interface ExpiredEndpointCollector {
    
    void performCollection();
    
    void startScheduledCollection();
    
    void stopScheduledCollection();
}
