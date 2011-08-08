package org.talend.esb.locator.proxy.service;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.talend.esb.servicelocator.client.ServiceLocator;
import org.talend.esb.servicelocator.client.ServiceLocatorException;

public class LocatorProxyServiceTest extends EasyMockSupport{
	
    private ServiceLocator sl = createMock(ServiceLocator.class);

	@Before
	public void setup() {

	}
    @Test
    public void initializeLocatorClient() throws InterruptedException, ServiceLocatorException {
    	LocatorProxyServiceImpl lps = new LocatorProxyServiceImpl();
    	lps.setConnectionTimeout(5000);
    	lps.setSessionTimeout(5000);
    	lps.setLocatorEndpoints("");
    	lps.setLocatorClient(sl);
    	lps.initLocator();
    	
    }
    @Test
    public void registeEndpoint() {
    	LocatorProxyServiceImpl lps = new LocatorProxyServiceImpl();
    	lps.setLocatorClient(sl);
    	
    }

}
