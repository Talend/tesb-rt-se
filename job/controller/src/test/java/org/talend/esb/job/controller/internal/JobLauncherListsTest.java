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
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;

import routines.system.api.TalendESBRoute;

public class JobLauncherListsTest extends EasyMockSupport {

    public static final String ROUTE_NAME_1 = "routeName1";

    public static final String ROUTE_NAME_2 = "routeName2";

    public static final String JOB_NAME_1 = "jobName1";

    public static final String JOB_NAME_2 = "jobName2";

    public static final String SERVICE_NAME_1 = "serviceName1";

    public static final String SERVICE_NAME_2 = "serviceName2";

    public static final String MANAGED_SERVICE_NAME = "org.osgi.service.cm.ManagedService";

    @SuppressWarnings("serial")
    public static final Dictionary<?, ?> PROPERTIES = new Hashtable<String, String>() {
        {
            put(Constants.SERVICE_PID, ROUTE_NAME_1);
        }
    };

    private TalendESBRoute route1 = createMock(TalendESBRoute.class);

    private TalendESBRoute route2 = createMock(TalendESBRoute.class);

    private BundleContext context;

    private ServiceRegistration sr;

    private ExecutorService execService;

    private JobLauncherImpl jobLauncher;

    @Before
    public void setUp() {
        context = createNiceMock(BundleContext.class);
        execService = createNiceMock(ExecutorService.class);
        sr = createNiceMock(ServiceRegistration.class);

        expectManagedJobStarting();

        replayAll();

        jobLauncher = new JobLauncherImpl();
        jobLauncher.setBundleContext(context);
        jobLauncher.setExecutorService(execService);

        jobLauncher.routeAdded(route1, ROUTE_NAME_1);
        jobLauncher.routeAdded(route2, ROUTE_NAME_2);
    }

    private void expectManagedJobStarting() {
        expect(context.registerService((String) anyObject(),
               anyObject(),
               (Dictionary<String, ?>) anyObject())).andStubReturn(sr);
        execService.execute((Runnable)anyObject());
    }
}
