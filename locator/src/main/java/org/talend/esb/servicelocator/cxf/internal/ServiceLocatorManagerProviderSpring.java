package org.talend.esb.servicelocator.cxf.internal;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.esb.servicelocator.cxf.ServiceLocatorManager;
import org.talend.esb.servicelocator.cxf.internal.ServiceLocatorManagerDelegate.ServiceLocatorManagerProvider;

class ServiceLocatorManagerProviderSpring implements ServiceLocatorManagerProvider {
    
    private ServiceLocatorManager locatorManager;

    public ServiceLocatorManagerProviderSpring() {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] {
            "/META-INF/tesb/locator/beansForDelegate.xml"
        });

        locatorManager = (ServiceLocatorManager) appContext.getBean("locatorManager");

    }

    public ServiceLocatorManager getServiceLocatorManager() {
        return locatorManager;
    }


}