/*
 * #%L
 * Service Activity Monitoring :: Agent
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
package org.talend.esb.sam.agent.lifecycle;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerLifeCycleListener;
import org.talend.esb.sam.common.event.EventTypeEnum;

/**
 * This ServerLifeCycleListener impl used to implement the feature of 
 * support web service start/stop event
 */
public class ServiceListenerImpl extends AbstractListenerImpl implements ServerLifeCycleListener {

    @Override
    public void startServer(Server server) {
        processStart(server.getEndpoint(), EventTypeEnum.SERVICE_START);
    }

    @Override
    public void stopServer(Server server) {
        processStop(server.getEndpoint(), EventTypeEnum.SERVICE_STOP);
    }

}
