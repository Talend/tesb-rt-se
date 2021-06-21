/*
 * #%L
 * Service Activity Monitoring :: Agent
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
package org.talend.esb.sam.agent.lifecycle;

//import javax.inject.Named;
//import javax.inject.Singleton;
import javax.xml.namespace.QName;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.ClientLifeCycleListener;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.service.model.ServiceInfo;
import org.talend.esb.sam.common.event.EventTypeEnum;

/**
 * This ClientLifeCycleListener impl used to implement the feature of
 * support web service start/stop event.
 */
//@Named
//@Singleton
public class ClientListenerImpl extends AbstractListenerImpl implements ClientLifeCycleListener {

    private static final QName AGENT_PORT_TYPE =
            new QName("http://www.talend.org/esb/sam/MonitoringService/v1", "MonitoringService");

    /* (non-Javadoc)
     * @see org.apache.cxf.endpoint.ClientLifeCycleListener#clientCreated(org.apache.cxf.endpoint.Client)
     */
    @Override
    public void clientCreated(Client client) {
        if (AGENT_PORT_TYPE.equals(getEndpointName(client.getEndpoint()))) {
            return;
        }
        processStart(client.getEndpoint(), EventTypeEnum.CLIENT_CREATE);
    }

    /* (non-Javadoc)
     * @see org.apache.cxf.endpoint.ClientLifeCycleListener#clientDestroyed(org.apache.cxf.endpoint.Client)
     */
    @Override
    public void clientDestroyed(Client client) {
        if (AGENT_PORT_TYPE.equals(getEndpointName(client.getEndpoint()))) {
            return;
        }
        processStop(client.getEndpoint(), EventTypeEnum.CLIENT_DESTROY);
    }

    private QName getEndpointName(Endpoint e) {
        ServiceInfo sInfo = e.getBinding().getBindingInfo().getService();
        return sInfo == null ? e.getEndpointInfo().getName() : sInfo.getInterface().getName();
    }

}
