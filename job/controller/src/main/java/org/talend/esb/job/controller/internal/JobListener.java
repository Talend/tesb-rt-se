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

import routines.system.api.TalendESBJob;
import routines.system.api.TalendESBJobFactory;
import routines.system.api.TalendESBRoute;
import routines.system.api.TalendJob;

/**
 * The listener interface for receiving job lifecycle events.
 *
 */
public interface JobListener {

    /**
     * Invoked when an ESB Job factory is registered.
     *
     * @param esbJobFactory the job factory registered, must not be <code>null</code>
     * @param name the name of the job instances the factory is creating, must not be <code>null</code>
     */
    void esbJobFactoryAdded(TalendESBJobFactory esbJobFactory, String name);

    /**
     * Invoked when an ESB Job factory is unregistered.
     *
     * @param esbJobFactory the job factory unregistered, must not be <code>null</code>
     * @param name the name of the job instances the factory is creating, must not be <code>null</code>
     */
    void esbJobFactoryRemoved(TalendESBJobFactory esbJobFactory, String name);

    /**
     * Invoked when an ESB Job is registered.
     *
     * @param esbJob the job registered, must not be <code>null</code>
     * @param name the name of the job, must not be <code>null</code>
     */
    void esbJobAdded(TalendESBJob esbJob, String name);

    /**
     * Invoked when an ESB Job is unregistered.
     *
     * @param esbJob the job unregistered, must not be <code>null</code>
     * @param name the name of the job, must not be <code>null</code>
     */
    void esbJobRemoved(TalendESBJob esbJob, String name);

    /**
     * Invoked when an ESB route is registered.
     *
     * @param route the route registered, must not be <code>null</code>
     * @param name the name of the route, must not be <code>null</code>
     */
    void routeAdded(TalendESBRoute route, String name);

    /**
     * Invoked when an ESB route is unregistered.
     *
     * @param route the route unregistered, must not be <code>null</code>
     * @param name the name of the route, must not be <code>null</code>
     */
    void routeRemoved(TalendESBRoute route, String name);

    /**
     * Invoked when a simple job is registered, which is not an ESB job nor a route.
     *
     * @param job the job registered, must not be <code>null</code>
     * @param name the name of the job, must not be <code>null</code>
     */
    void jobAdded(TalendJob job, String name);

    /**
     * Invoked when a simple job is unregistered, which is not an ESB job nor a route.
     *
     * @param job the job unregistered, must not be <code>null</code>
     * @param name the name of the job, must not be <code>null</code>
     */
    void jobRemoved(TalendJob job, String name);

}
