package org.talend.esb.servicelocator.client.internal;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.talend.esb.servicelocator.TestContent.*;
import static org.talend.esb.servicelocator.TestValues.*;

public class SLEndpointImplTest {


    private SLEndpointImpl slEndpoint;

    @Before
    public void setUp() throws Exception {
        slEndpoint = new SLEndpointImpl(SERVICE_QNAME_1, CONTENT_ENDPOINT_1, false);
    }
    
    @Test
    public void forService() {
        assertEquals(SERVICE_QNAME_1, slEndpoint.forService());
    }

    @Test
    public void getAddress() {
        assertEquals(ENDPOINT_1, slEndpoint.getAddress());
    }

    @Test
    public void getLasttimeStarted() {
        assertEquals(LAST_TIME_STARTED, slEndpoint.getLastTimeStarted());
    }
    
}
