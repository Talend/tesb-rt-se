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

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;

import routines.system.api.TalendESBRoute;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class JobLauncherListenerTest {

    public static final String ROUTE_NAME_1 = "routeName1";

    public static final String ROUTE_NAME_2 = "routeName2";

    public static final String MANAGED_SERVICE_NAME = "org.osgi.service.cm.ManagedService";
    
    public static final String[] EMPTY_STRING_ARR = new String[0];

    @SuppressWarnings("serial")
    public static final Dictionary<?, ?> properties = new Hashtable<String, String>() {{
        put(Constants.SERVICE_PID, ROUTE_NAME_1);
    }};
    
    private BundleContext context;

    private TalendESBRoute route;

    private JobLauncherImpl jobLauncher;

    @Before
    public void setup() {
        context = createMock(BundleContext.class);
        route = createNiceMock(TalendESBRoute.class);
        replay(route);
        
        jobLauncher = new JobLauncherImpl();
        jobLauncher.setBundleContext(context);
    }

    @Test
    public void routeAddedManagedServiceRegistered() throws Exception {
        ServiceRegistration sr = createNiceMock(ServiceRegistration.class);
        
        expect(context.registerService(
            eq(MANAGED_SERVICE_NAME), isA(RouteAdapter.class), eq(properties))).andReturn(sr);
        replay(context, sr);

        jobLauncher.routeAdded(route, ROUTE_NAME_1);
        verify(context);
    }

    @Test
    public void managedServiceUnregisteredIfRegisteredBefore() throws Exception {
        ServiceRegistration sr = createNiceMock(ServiceRegistration.class);
        sr.unregister();
        
        expect(context.registerService(
            eq(MANAGED_SERVICE_NAME), isA(RouteAdapter.class), eq(properties))).andReturn(sr);
        replay(context, sr);

        jobLauncher.routeAdded(route, ROUTE_NAME_1);
        jobLauncher.routeRemoved(route, ROUTE_NAME_1);
        verify(context, sr);
    }
}
