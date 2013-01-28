package org.talend.esb.servicelocator.cxf.internal;

import org.apache.cxf.Bus;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.talend.esb.servicelocator.cxf.ServiceLocatorManager;
import org.talend.esb.servicelocator.cxf.internal.ServiceLocatorManagerDelegate.ServiceLocatorManagerProvider;

class ServiceLocatorManagerProviderOSGi implements ServiceLocatorManagerProvider {

    private BundleContext context;
    
    private ServiceLocatorManager locatorManager;

    public ServiceLocatorManagerProviderOSGi(Bus cxfBus) {
        context = cxfBus.getExtension(BundleContext.class);
        ServiceLocatorManagerTracker tracker = new ServiceLocatorManagerTracker(context);
        tracker.open();
        locatorManager = (ServiceLocatorManager) tracker.getService();
    }

    public ServiceLocatorManager getServiceLocatorManager() {
        return locatorManager;
    }

    private class ServiceLocatorManagerTracker extends ServiceTracker {
        
        public ServiceLocatorManagerTracker(BundleContext bundleContext) {
            super(bundleContext, ServiceLocatorManager.class.getName(), null); 
        }

        @Override
        public Object addingService(ServiceReference reference) {
            Object service = context.getService(reference);
            synchronized (ServiceLocatorManagerProviderOSGi.this) {
                if (locatorManager == null) {
                    locatorManager = (ServiceLocatorManager) service;
                }
            }
            return service;
        }

        @Override
        public void modifiedService(ServiceReference reference, Object service) {
        }

        @Override
        public void removedService(ServiceReference reference, Object service) {
            synchronized (ServiceLocatorManagerProviderOSGi.this) {
                if (service != locatorManager) {
                    return;
                }
                locatorManager = (ServiceLocatorManager) getService();
            }
        }
    }
}