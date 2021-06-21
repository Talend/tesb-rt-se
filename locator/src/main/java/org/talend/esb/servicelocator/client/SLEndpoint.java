/*
 * #%L
 * Service Locator Client for CXF
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
package org.talend.esb.servicelocator.client;

import javax.xml.namespace.QName;

/**
 * An <code>SLEndpoint</code> describes a service endpoint stored in the ServiceLocator. In addition the
 * properties and some additional meta data together with the status of the endpoint are provided.
 */
public interface SLEndpoint extends Endpoint {

    long getLastTimeStarted();


    long getLastTimeStopped();

    /**
     * Indicates whether the server is up and running.
     *
     * @return <code>true</code> iff the service locator deems the endpoint running.
     */
    boolean isLive();

    /**
     * Return the name of the service the endpoint belongs to.
     *
     * @return the service name
     */
    QName forService();

}
