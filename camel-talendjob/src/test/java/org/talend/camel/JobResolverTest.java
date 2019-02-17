package org.talend.camel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.talend.camel.internal.Activator;

import routines.system.api.TalendESBJob;
import routines.system.api.TalendESBJobFactory;

public class JobResolverTest {

    private static class DummyBundleContext implements BundleContext {

        private final Bundle bundle = new DummyBundle(ROUTE_BUNDLE_SYMBOLIC_NAME, this);

        private final Map<String, List<ServiceReference<TalendESBJobFactory>>> serviceReferences =
                new HashMap<String, List<ServiceReference<TalendESBJobFactory>>>();

        private final Map<ServiceListener, String> serviceListeners = new HashMap<ServiceListener, String>();
        private final Map<String, ServiceListener> serviceListenersReverse = new HashMap<String, ServiceListener>();

        public DummyBundleContext() {
            super();
        }

        @Override
        public String getProperty(String key) {
            // unused
            return null;
        }

        @Override
        public Bundle getBundle() {
            return bundle;
        }

        @Override
        public Bundle installBundle(String location, InputStream input) throws BundleException {
            // unused
            return null;
        }

        @Override
        public Bundle installBundle(String location) throws BundleException {
            // unused
            return null;
        }

        @Override
        public Bundle getBundle(long id) {
            // unused
            return null;
        }

        @Override
        public Bundle[] getBundles() {
            // unused
            return null;
        }

        @Override
        public void addServiceListener(ServiceListener listener, String filter) throws InvalidSyntaxException {
            if (filter == null) {
                throw new InvalidSyntaxException("Invalid", "null");
            }
            serviceListeners.put(listener, filter);
            serviceListenersReverse.put(filter, listener);
        }

        @Override
        public void addServiceListener(ServiceListener listener) {
            serviceListeners.put(listener, "");
            serviceListenersReverse.put("", listener);
        }

        @Override
        public void removeServiceListener(ServiceListener listener) {
            String filter = serviceListeners.remove(listener);
            if (filter != null) {
                serviceListenersReverse.remove(filter);
            }
        }

        @Override
        public void addBundleListener(BundleListener listener) {
            // unused
        }

        @Override
        public void removeBundleListener(BundleListener listener) {
            // unused
        }

        @Override
        public void addFrameworkListener(FrameworkListener listener) {
            // unused
        }

        @Override
        public void removeFrameworkListener(FrameworkListener listener) {
            // unused
        }

        @Override
        public ServiceRegistration<?> registerService(String[] clazzes, Object service,
                Dictionary<String, ?> properties) {
            // unused
            return null;
        }

        @Override
        public ServiceRegistration<?> registerService(String clazz, Object service, Dictionary<String, ?> properties) {
            // unused
            return null;
        }

        @Override
        public <S> ServiceRegistration<S> registerService(Class<S> clazz, S service, Dictionary<String, ?> properties) {
            // unused
            return null;
        }

        @Override
        public ServiceReference<?>[] getServiceReferences(String clazz, String filter) throws InvalidSyntaxException {
            if (!TalendESBJobFactory.class.getName().equals(clazz)) {
                return new ServiceReference[0];
            }
            final List<ServiceReference<TalendESBJobFactory>> result = serviceReferences.get(filter);
            if (result == null) {
                return new ServiceReference[0];
            }
            return result.toArray(new ServiceReference[result.size()]);
        }

        @Override
        public ServiceReference<?>[] getAllServiceReferences(String clazz, String filter)
                throws InvalidSyntaxException {
            return getServiceReferences(clazz, filter);
        }

        @Override
        public ServiceReference<?> getServiceReference(String clazz) {
            // unused
            return null;
        }

        @Override
        public <S> ServiceReference<S> getServiceReference(Class<S> clazz) {
            // unused
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <S> Collection<ServiceReference<S>> getServiceReferences(Class<S> clazz, String filter)
                throws InvalidSyntaxException {
            if (clazz == TalendESBJobFactory.class) {
                return (List<ServiceReference<S>>) (Object) serviceReferences.get(filter);
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <S> S getService(ServiceReference<S> reference) {
            if (reference instanceof DummyServiceReference) {
                return (S) new DummyTalendESBJobFactory((DummyServiceReference) reference);
            }
            return null;
        }

        @Override
        public boolean ungetService(ServiceReference<?> reference) {
            // unused
            return false;
        }

        @Override
        public File getDataFile(String filename) {
            // unused
            return null;
        }

        @Override
        public Filter createFilter(String filter) throws InvalidSyntaxException {
            // unused
            return null;
        }

        @Override
        public Bundle getBundle(String location) {
            // unused
            return null;
        }
    
        public void addServiceReference(String filter, DummyServiceReference serviceReference) {
            List<ServiceReference<TalendESBJobFactory>> sRefs = serviceReferences.get(filter);
            if (sRefs == null) {
                sRefs = new LinkedList<ServiceReference<TalendESBJobFactory>>();
                serviceReferences.put(filter, sRefs);
            }
            sRefs.add(serviceReference);
            ServiceListener listener = serviceListenersReverse.get(filter);
            if (listener != null) {
                listener.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, serviceReference));
            }
        }
    
        public void removeServiceReferences(String filter) {
            List<ServiceReference<TalendESBJobFactory>> sRefs = serviceReferences.remove(filter);
            if (sRefs != null) {
                ServiceListener listener = serviceListenersReverse.get(filter);
                if (listener != null) {
                    for (ServiceReference<?> sRef : sRefs) {
                        listener.serviceChanged(new ServiceEvent(ServiceEvent.UNREGISTERING, sRef));
                    }
                }
            }
        }

        @Override
        public <S> ServiceRegistration<S> registerService(Class<S> clazz, ServiceFactory<S> factory,
                Dictionary<String, ?> properties) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <S> ServiceObjects<S> getServiceObjects(ServiceReference<S> reference) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    private class DummyServiceReference implements ServiceReference<TalendESBJobFactory> {

        private final Bundle bundle;

        public DummyServiceReference(String bundleSymbolicName) {
            super();
            this.bundle = new DummyBundle(bundleSymbolicName, null);
        }

        @Override
        public Object getProperty(String key) {
            // unused
            return null;
        }

        @Override
        public String[] getPropertyKeys() {
            // unused
            return null;
        }

        @Override
        public Bundle getBundle() {
            return bundle;
        }

        @Override
        public Bundle[] getUsingBundles() {
            // unused
            return null;
        }

        @Override
        public boolean isAssignableTo(Bundle bundle, String className) {
            // unused
            return false;
        }

        @Override
        public int compareTo(Object reference) {
            // unused
            return 0;
        }
        
    }

    private static class DummyBundle implements Bundle {

        private final String symbolicName;
        private final BundleContext bundleContext;

        public DummyBundle(String symbolicName, BundleContext bundleContext) {
            super();
            this.symbolicName = symbolicName;
            this.bundleContext = bundleContext;
        }

        @Override
        public int compareTo(Bundle o) {
            // unused
            return 0;
        }

        @Override
        public int getState() {
            // unused
            return 0;
        }

        @Override
        public void start(int options) throws BundleException {
            // unused
        }

        @Override
        public void start() throws BundleException {
            // unused
        }

        @Override
        public void stop(int options) throws BundleException {
            // unused
        }

        @Override
        public void stop() throws BundleException {
            // unused
        }

        @Override
        public void update(InputStream input) throws BundleException {
            // unused
        }

        @Override
        public void update() throws BundleException {
            // unused
        }

        @Override
        public void uninstall() throws BundleException {
            // unused
        }

        @Override
        public Dictionary<String, String> getHeaders() {
            // unused
            return null;
        }

        @Override
        public long getBundleId() {
            // unused
            return 0;
        }

        @Override
        public String getLocation() {
            // unused
            return null;
        }

        @Override
        public ServiceReference<?>[] getRegisteredServices() {
            // unused
            return null;
        }

        @Override
        public ServiceReference<?>[] getServicesInUse() {
            // unused
            return null;
        }

        @Override
        public boolean hasPermission(Object permission) {
            // unused
            return false;
        }

        @Override
        public URL getResource(String name) {
            // unused
            return null;
        }

        @Override
        public Dictionary<String, String> getHeaders(String locale) {
            // unused
            return null;
        }

        @Override
        public String getSymbolicName() {
            return symbolicName;
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            // unused
            return null;
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            // unused
            return null;
        }

        @Override
        public Enumeration<String> getEntryPaths(String path) {
            // unused
            return null;
        }

        @Override
        public URL getEntry(String path) {
            // unused
            return null;
        }

        @Override
        public long getLastModified() {
            // unused
            return 0;
        }

        @Override
        public Enumeration<URL> findEntries(String path, String filePattern, boolean recurse) {
            // unused
            return null;
        }

        @Override
        public BundleContext getBundleContext() {
            return bundleContext;
        }

        @Override
        public Map<X509Certificate, List<X509Certificate>> getSignerCertificates(int signersType) {
            // unused
            return null;
        }

        @Override
        public Version getVersion() {
            // unused
            return null;
        }

        @Override
        public <A> A adapt(Class<A> type) {
            // unused
            return null;
        }

        @Override
        public File getDataFile(String filename) {
            // unused
            return null;
        }
        
    }

    private static class DummyTalendESBJobFactory implements TalendESBJobFactory {

        private DummyServiceReference serviceReference;

        public DummyTalendESBJobFactory(DummyServiceReference serviceReference) {
            super();
            this.serviceReference = serviceReference;
        }

        @Override
        public TalendESBJob newTalendESBJob() {
            // unused
            return null;
        }

        public DummyServiceReference getServiceReference() {
            return serviceReference;
        }
    }

    private static final String OWNER_NAME = "testRoute";
    private static final String FULL_JOB_NAME = "org.test.testJob";
    private static final String ROUTE_BUNDLE_SYMBOLIC_NAME = "test_project.testRoute";
    private static final String JOB_BUNDLE_SYMBOLIC_NAME = "test_project.testRoute_testJob";
    private static final String ALT_JOB_BUNDLE_SYMBOLIC_NAME = "alt_project.testRoute_testJob";
    private static final String OTHER_JOB_BUNDLE_SYMBOLIC_NAME = "test_project.otherRoute_testJob";
    private static final String OLD_FILTER = "(&(name=testJob)(type=job))";
    private static final String NEW_FILTER = "(&(name=org.test.testJob)(type=job))";
    private static final String COMBO_FILTER = "(|" + OLD_FILTER + NEW_FILTER + ")";

    public JobResolverTest() {
        super();
    }

    @Test
    public void testJobResolver() throws Exception {
        final Activator activator = new Activator();
        final DummyBundleContext bundleContext = new DummyBundleContext();
        activator.start(bundleContext);
        final JobResolver jobResolver = JobResolverHolder.getJobResolver();
        Assert.assertNotNull(jobResolver);
        DummyTalendESBJobFactory service = (DummyTalendESBJobFactory) jobResolver.getJobService(
                OWNER_NAME, FULL_JOB_NAME, false);
        Assert.assertNull(service);
        final DummyServiceReference sRefA = new DummyServiceReference(JOB_BUNDLE_SYMBOLIC_NAME);
        final DummyServiceReference sRefB = new DummyServiceReference(JOB_BUNDLE_SYMBOLIC_NAME);
        final DummyServiceReference sRefC = new DummyServiceReference(ALT_JOB_BUNDLE_SYMBOLIC_NAME);
        final DummyServiceReference sRefD = new DummyServiceReference(OTHER_JOB_BUNDLE_SYMBOLIC_NAME);
        bundleContext.addServiceReference(OLD_FILTER, sRefA);
        service = (DummyTalendESBJobFactory) jobResolver.getJobService(OWNER_NAME, FULL_JOB_NAME, false);
        Assert.assertNotNull(service);
        Assert.assertTrue(service.getServiceReference() == sRefA);
        bundleContext.addServiceReference(NEW_FILTER, sRefB);
        service = (DummyTalendESBJobFactory) jobResolver.getJobService(OWNER_NAME, FULL_JOB_NAME, false);
        Assert.assertNotNull(service);
        Assert.assertTrue(service.getServiceReference() == sRefA);
        bundleContext.removeServiceReferences(OLD_FILTER);
        service = (DummyTalendESBJobFactory) jobResolver.getJobService(OWNER_NAME, FULL_JOB_NAME, false);
        Assert.assertNotNull(service);
        Assert.assertTrue(service.getServiceReference() == sRefB);
        bundleContext.removeServiceReferences(NEW_FILTER);
        bundleContext.addServiceReference(NEW_FILTER, sRefC);
        service = (DummyTalendESBJobFactory) jobResolver.getJobService(OWNER_NAME, FULL_JOB_NAME, false);
        Assert.assertNotNull(service);
        Assert.assertTrue(service.getServiceReference() == sRefC);
        bundleContext.removeServiceReferences(NEW_FILTER);
        bundleContext.addServiceReference(NEW_FILTER, sRefD);
        service = (DummyTalendESBJobFactory) jobResolver.getJobService(OWNER_NAME, FULL_JOB_NAME, false);
        Assert.assertNull(service);
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                bundleContext.addServiceReference(COMBO_FILTER, sRefA);
            }
        };
        final Timer timer = new Timer();
        timer.schedule(timerTask, 5000L);
        service = (DummyTalendESBJobFactory) jobResolver.getJobService(OWNER_NAME, FULL_JOB_NAME, true);
        Assert.assertNotNull(service);
        Assert.assertTrue(service.getServiceReference() == sRefA);
    }
}
