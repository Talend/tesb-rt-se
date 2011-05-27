package org.talend.esb.servicelocator.client.internal;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.talend.esb.servicelocator.TestContent.CONTENT_ENDPOINT_1;
import static org.talend.esb.servicelocator.TestValues.*;

import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;
import org.talend.esb.servicelocator.client.SLEndpoint;

public class GetEndpointsTest extends AbstractServiceLocatorImplTest {

    @Test
    public void getEndpointsEndpointIsLive() throws Exception {
        pathExists(SERVICE_PATH_1);
        getChildren(SERVICE_PATH_1, ENDPOINT_NODE_1);
        pathExists(ENDPOINT_PATH_11 + "/" + STATUS_NODE);
        getContent(ENDPOINT_PATH_11, CONTENT_ENDPOINT_1);

        replayAll();
        
        ServiceLocatorImpl slc = createServiceLocatorAndConnect();

        List<SLEndpoint> endpoints = slc.getEndpoints(SERVICE_QNAME_1);

        SLEndpoint endpoint = endpoints.get(0);
        assertTrue(endpoint.isLive());
        verifyAll();
    }

    @Test
    public void getEndpointsEndpointIsNotLive() throws Exception {
        pathExists(SERVICE_PATH_1);
        getChildren(SERVICE_PATH_1, ENDPOINT_NODE_1); //, ENDPOINT_NODE_2);
        pathExistsNot(ENDPOINT_PATH_11 + "/" + STATUS_NODE);
        getContent(ENDPOINT_PATH_11, CONTENT_ENDPOINT_1);

        replayAll();
        
        ServiceLocatorImpl slc = createServiceLocatorAndConnect();

        List<SLEndpoint> endpoints = slc.getEndpoints(SERVICE_QNAME_1);

        SLEndpoint endpoint = endpoints.get(0);
        assertFalse(endpoint.isLive());
        verifyAll();
    }
    

    protected void getContent(String path, byte[] content) throws KeeperException,
            InterruptedException {
        expect(zkMock.getData(eq(path), eq(false), (Stat) anyObject())).andReturn(content);
    }


}
