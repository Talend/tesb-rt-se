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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import routines.system.api.TalendESBRoute;

public class RouteAdapter implements ManagedService, JobTask {

    private static final Logger LOG = Logger.getLogger(RouteAdapter.class.getName());

    private final TalendESBRoute route;

    private final String name;

    private final Configuration configuration = new Configuration();

    public RouteAdapter(TalendESBRoute route, String name) {
        this.route = route;
        this.name = name;
    }

    @Override
    public void updated(@SuppressWarnings("rawtypes") Dictionary properties) throws ConfigurationException {
        configuration.setProperties(properties);
    }

    public void stop() {
        LOG.info("Cancelling route " + name);
        ClassLoader oldContextCL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(route.getClass().getClassLoader());
            route.shutdown();
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Shutting down route " + name + " caused an exception.", e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldContextCL);
        }
    }

    @Override
    public void run() {
        LOG.info("Starting route " + name);

        String[] args = null;
        try {
            args = configuration.awaitArguments();
        } catch (InterruptedException e) {
            return;
        }

        ClassLoader oldContextCL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(route.getClass().getClassLoader());
            int ret = route.runJobInTOS(args);
            LOG.info("Route " + name + " finished, return code is " + ret);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            LOG.log(Level.SEVERE, "RuntimeException when invoking runJobInTOS()", ex);
        } finally {
            Thread.currentThread().setContextClassLoader(oldContextCL);
        }
    }

}
