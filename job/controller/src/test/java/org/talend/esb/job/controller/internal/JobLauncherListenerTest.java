/*
 * #%L
 * Talend :: ESB :: Job :: Controller
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
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
package org.talend.esb.job.controller.internal;


import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import routines.system.api.ESBEndpointInfo;
import routines.system.api.ESBEndpointRegistry;
import routines.system.api.TalendESBJob;
import routines.system.api.TalendESBRoute;
import routines.system.api.TalendJob;

public class JobLauncherListenerTest extends EasyMockSupport {

    public static final String NAME = "name";

    public static final String MANAGED_SERVICE_NAME = "org.osgi.service.cm.ManagedService";

    public static final String[] EMPTY_STRING_ARR = new String[0];

    @SuppressWarnings("serial")
    public static final Dictionary<String, ?> PROPERTIES = new Hashtable<String, String>() {
        {
            put(Constants.SERVICE_PID, NAME);
        }
    };

    private BundleContext context;

    private ServiceRegistration sr;

    private ExecutorService execService;

    private JobLauncherImpl jobLauncher;

    @Before
    public void setUp() {
        context = createMock(BundleContext.class);
        execService = createMock(ExecutorService.class);

        sr = createNiceMock(ServiceRegistration.class);

        jobLauncher = new JobLauncherImpl();
        jobLauncher.setBundleContext(context);
        jobLauncher.setExecutorService(execService);
    }

    @Test
    public void routeAddedManagedServiceRegistered() throws Exception {
        TalendESBRoute route = createMock(TalendESBRoute.class);

        expectManagedJobStarting(RouteAdapter.class);
        replayAll();

        jobLauncher.routeAdded(route, NAME);
        verifyAll();
    }

    private void expectManagedJobStarting(Class<? extends JobTask> taskClass) {
        expect(context.registerService(
                eq(MANAGED_SERVICE_NAME), isA(taskClass), eq(PROPERTIES))).andReturn(sr);
        execService.execute(isA(taskClass));
    }

    @Test
    public void managedServiceForRouteUnregisteredIfRegisteredBefore() throws Exception {
        TalendESBRoute route = createNiceMock(TalendESBRoute.class);
        sr.unregister();

        expectManagedJobStarting(RouteAdapter.class);
        replayAll();

        jobLauncher.routeAdded(route, NAME);
        jobLauncher.routeRemoved(route, NAME);
        verifyAll();
    }

    @Test
    public void jobAddedManagedServiceRegistered() throws Exception {
        TalendJob job = createNiceMock(TalendJob.class);

        expectManagedJobStarting(SimpleJobTask.class);
        replayAll();

        jobLauncher.jobAdded(job, NAME);
        verifyAll();
    }

    @Test
    public void managedServiceForJobUnregisteredIfRegisteredBefore() throws Exception {
        TalendJob job = createNiceMock(TalendJob.class);
        sr.unregister();

        expectManagedJobStarting(SimpleJobTask.class);
        replayAll();

        jobLauncher.jobAdded(job, NAME);
        jobLauncher.jobRemoved(job, NAME);
        verifyAll();
    }

    @Test
    public void esbJobAddedEndpointRegistrySpecifiedForJob() {
        ESBEndpointInfo endpointInfo = createNiceMock(ESBEndpointInfo.class);
        TalendESBJob esbJob = createMock(TalendESBJob.class);
        esbJob.setEndpointRegistry((ESBEndpointRegistry) anyObject());
        expect(esbJob.getEndpoint()).andReturn(endpointInfo);

        replayAll();

        jobLauncher.esbJobAdded(esbJob, NAME);
        verifyAll();
    }

    @Test
    public void esbConsumerOnlyJobAddedJobStartedImmediatlyAsSimpleJob() {
        TalendESBJob esbJob = createMock(TalendESBJob.class);
        esbJob.setEndpointRegistry((ESBEndpointRegistry) anyObject());
        expect(esbJob.getEndpoint()).andReturn(null);

        expectManagedJobStarting(SimpleJobTask.class);
        replayAll();

        jobLauncher.esbJobAdded(esbJob, NAME);
        verifyAll();
    }

    @Test
    public void esbConsumerOnlyJobUnregisters() {
        TalendESBJob esbJob = createMock(TalendESBJob.class);

        sr.unregister();

        esbJob.setEndpointRegistry((ESBEndpointRegistry) anyObject());
        expect(esbJob.getEndpoint()).andReturn(null).times(2);

        expectManagedJobStarting(SimpleJobTask.class);
        replayAll();

        jobLauncher.esbJobAdded(esbJob, NAME);
        jobLauncher.esbJobRemoved(esbJob, NAME);
        verifyAll();
    }
}
