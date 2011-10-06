/*
 * #%L
 * Talend :: ESB :: Job :: Controller
 * %%
 * Copyright (C) 2011 Talend Inc.
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

import routines.system.api.TalendESBRoute;
import routines.system.api.TalendJob;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

public class JobLauncherListenerTest extends EasyMockSupport {

    public static final String NAME = "name";

    public static final String MANAGED_SERVICE_NAME = "org.osgi.service.cm.ManagedService";
    
    public static final String[] EMPTY_STRING_ARR = new String[0];

    @SuppressWarnings("serial")
    public static final Dictionary<?, ?> properties = new Hashtable<String, String>() {{
        put(Constants.SERVICE_PID, NAME);
    }};
    
    private BundleContext context;

    private ServiceRegistration sr;
    
    private ExecutorService execService;

    private JobLauncherImpl jobLauncher;

    @Before
    public void setup() {
        context = createMock(BundleContext.class);
        execService = createMock(ExecutorService.class);

        sr = createNiceMock(ServiceRegistration.class);
       
        jobLauncher = new JobLauncherImpl();
        jobLauncher.setBundleContext(context);
        jobLauncher.setExecutorService(execService);
    }

    @Test
    public void routeAddedManagedServiceRegistered() throws Exception {
        TalendESBRoute route = createNiceMock(TalendESBRoute.class);
  
        expect(context.registerService(
            eq(MANAGED_SERVICE_NAME), isA(RouteAdapter.class), eq(properties))).andReturn(sr);
        execService.execute(isA(RouteAdapter.class));
        replayAll();

        jobLauncher.routeAdded(route, NAME);
        verifyAll();
    }

    @Test
    public void managedServiceForRouteUnregisteredIfRegisteredBefore() throws Exception {
        TalendESBRoute route = createNiceMock(TalendESBRoute.class);
        sr.unregister();
        
        expect(context.registerService(
            eq(MANAGED_SERVICE_NAME), isA(RouteAdapter.class), eq(properties))).andReturn(sr);
        execService.execute(isA(RouteAdapter.class));
        replayAll();

        jobLauncher.routeAdded(route, NAME);
        jobLauncher.routeRemoved(route, NAME);
        verifyAll();
    }

    @Test
    public void jobAddedManagedServiceRegistered() throws Exception {
        TalendJob job = createNiceMock(TalendJob.class);

        ServiceRegistration sr = createNiceMock(ServiceRegistration.class);

        expect(context.registerService(
            eq(MANAGED_SERVICE_NAME), isA(SimpleJobTask.class), eq(properties))).andReturn(sr);
        execService.execute(isA(SimpleJobTask.class));
        replayAll();

        jobLauncher.jobAdded(job, NAME);
        verifyAll();
    }

    @Test
    public void managedServiceForJobUnregisteredIfRegisteredBefore() throws Exception {
        TalendJob job = createNiceMock(TalendJob.class);
        sr.unregister();

        expect(context.registerService(
                eq(MANAGED_SERVICE_NAME), isA(SimpleJobTask.class), eq(properties))).andReturn(sr);
        execService.execute(isA(SimpleJobTask.class));
        replayAll();

        jobLauncher.jobAdded(job, NAME);
        jobLauncher.jobRemoved(job, NAME);
        verifyAll();
    }
}
