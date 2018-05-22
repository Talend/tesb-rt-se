/*
 * #%L
 * Talend ESB :: Camel Talend Job Component
 * %%
 * Copyright (C) 2011 - 2014 Talend Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.talend.camel.internal;

import java.util.Collection;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.talend.camel.JobResolver;
import org.talend.camel.JobResolverHolder;

import routines.system.api.TalendESBJobFactory;

public class Activator implements BundleActivator, JobResolver {

    private static class ServiceReferenceRetriever {
    
        private final String oldFilter;
        private final String newFilter;
        private final String jobOwner;
        private final String simpleName;
        private final ServiceListener listener;
        private ServiceReference<TalendESBJobFactory> serviceReference = null;

        public ServiceReferenceRetriever(final String oldFilter, final String newFilter,
                final String jobOwner, final String simpleName) {
            super();
            this.oldFilter = oldFilter;
            this.newFilter = newFilter;
            this.jobOwner = jobOwner;
            this.simpleName = simpleName;
            if (context == null) {
                throw new IllegalStateException("Bundle not initialized. ");
            }
            listener = new ServiceListener() {
                @SuppressWarnings("unchecked")
                @Override
                public void serviceChanged(final ServiceEvent event) {
                    if (event.getType() == ServiceEvent.REGISTERED) {
                        setServiceReference(validServiceReference(
                                (ServiceReference<TalendESBJobFactory>) event.getServiceReference(),
                                jobOwner, simpleName));
                    }
                }
            };
        }

        public void initialize() throws InvalidSyntaxException {
            boolean success = false;
            try {
                final String filter = "(|" + oldFilter + newFilter + ")";
                context.addServiceListener(listener, filter);
                Collection<ServiceReference<TalendESBJobFactory>> serviceReferences =
                        getCurrentServiceReferences(oldFilter, newFilter);
                setServiceReference(validServiceReference(serviceReferences, jobOwner, simpleName));
                success = true;
            } finally {
                if (!success) {
                    context.removeServiceListener(listener);
                }
            }
        }

        public synchronized ServiceReference<TalendESBJobFactory> getServiceReference(final long timeout) {
            try {
                if (serviceReference != null) {
                    return serviceReference;
                }
                final long endTime = System.currentTimeMillis() + timeout;
                while (serviceReference == null) {
                    final long waitTime = endTime - System.currentTimeMillis();
                    if (waitTime <= 0) {
                        break;
                    }
                    try {
                        wait(waitTime);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                return serviceReference;
            } finally {
                context.removeServiceListener(listener);
            }
        }

        private synchronized void setServiceReference(final ServiceReference<TalendESBJobFactory> serviceReference) {
            if (this.serviceReference != null || serviceReference == null) {
                return;
            }
            this.serviceReference = serviceReference;
            notifyAll();
        }
    }

    private static final long SERVICE_REFERENCE_TIMEOUT = 10000L;
    private static BundleContext context;

    @Override
    public void start(BundleContext ctx) throws Exception {
        Activator.context = ctx;
        JobResolverHolder.setJobResolver(this);
    }

    @Override
    public void stop(BundleContext ctx) throws Exception {
        Activator.context = null;
    }

    public TalendESBJobFactory getJobService(final String jobOwner, final String fullJobName,
            final boolean isMandatory) throws Exception {
        final String simpleName = fullJobName.substring(fullJobName.lastIndexOf('.') + 1);
        final String oldFilter = "(&(name=" + simpleName + ")(type=job))";
        final String newFilter = "(&(name=" + fullJobName + ")(type=job))";
        Collection<ServiceReference<TalendESBJobFactory>> serviceReferences =
                getCurrentServiceReferences(oldFilter, newFilter);
        ServiceReference<TalendESBJobFactory> serviceReference =
                validServiceReference(serviceReferences, jobOwner, simpleName);
        if (serviceReference == null && isMandatory) {
            ServiceReferenceRetriever retriever = new ServiceReferenceRetriever(
                    oldFilter, newFilter, jobOwner, simpleName);
            retriever.initialize();
            serviceReference = retriever.getServiceReference(SERVICE_REFERENCE_TIMEOUT);
        }
        return serviceReference == null ? null : context.getService(serviceReference);
    }

    private static Collection<ServiceReference<TalendESBJobFactory>> getCurrentServiceReferences(
            final String oldFilter, final String newFilter) throws InvalidSyntaxException {
        if (context == null) {
            return null;
        }
        /*
         * read old version style first
         * see https://jira.talendforge.org/browse/TESB-12909
         */
        Collection<ServiceReference<TalendESBJobFactory>> serviceReferences =
                context.getServiceReferences(TalendESBJobFactory.class, oldFilter);

        //if no old version style, then read fashion style
        if (null == serviceReferences || serviceReferences.isEmpty()) {
            serviceReferences =
                context.getServiceReferences(TalendESBJobFactory.class, newFilter);
        }
        return serviceReferences;
    }

    private static ServiceReference<TalendESBJobFactory> validServiceReference(
            final Collection<ServiceReference<TalendESBJobFactory>> serviceReferences,
            final String jobOwner, final String simpleName) {
        if (serviceReferences == null || serviceReferences.isEmpty()) {
            return null;
        }
        if (jobOwner == null) {
            return serviceReferences.iterator().next();
        }
        final String jobBundleName = jobOwner + "_" + simpleName;
        for (ServiceReference<TalendESBJobFactory> sref : serviceReferences) {
            final String symName = sref.getBundle().getSymbolicName();
            final String shortName = symName.substring(symName.lastIndexOf('.') + 1);
            if (jobBundleName.equals(shortName)) {
                return sref;
            }
        }
        return null;
    }

    private static ServiceReference<TalendESBJobFactory> validServiceReference(
            final ServiceReference<TalendESBJobFactory> serviceReference,
            final String jobOwner, final String simpleName) {
        if (serviceReference == null) {
            return null;
        }
        if (jobOwner == null) {
            return serviceReference;
        }
        final String jobBundleName = jobOwner + "_" + simpleName;
        final String symName = serviceReference.getBundle().getSymbolicName();
        final String shortName = symName.substring(symName.lastIndexOf('.') + 1);
        if (jobBundleName.equals(shortName)) {
            return serviceReference;
        }
        return null;
    }
}
