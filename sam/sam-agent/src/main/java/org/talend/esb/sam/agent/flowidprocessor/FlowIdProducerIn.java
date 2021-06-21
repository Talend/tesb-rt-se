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
package org.talend.esb.sam.agent.flowidprocessor;

import java.util.logging.Logger;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.addressing.ContextUtils;
import org.talend.esb.sam.agent.message.FlowIdHelper;


/**
 * The Class FlowIdProducerIn used for FlowId generation in incoming messages.
 *
 * @param <T> the generic type
 */
public class FlowIdProducerIn<T extends Message> extends AbstractPhaseInterceptor<T> {

    private static final Logger LOG = Logger.getLogger(FlowIdProducerIn.class.getName());

    /**
     * Instantiates a new flow id producer in.
     */
    public FlowIdProducerIn() {
        super(Phase.PRE_INVOKE);
    }

    /* (non-Javadoc)
     * @see org.apache.cxf.interceptor.Interceptor#handleMessage(org.apache.cxf.message.Message)
     */
    public void handleMessage(T message) throws Fault {
        String flowId = FlowIdHelper.getFlowId(message);

        if (flowId == null) {
            flowId = FlowIdProtocolHeaderCodec.readFlowId(message);
        }

        if (flowId == null) {
            flowId = FlowIdSoapCodec.readFlowId(message);
        }

        if (flowId == null) {
             Exchange ex = message.getExchange();
        if (null!=ex){
        Message reqMsg = ex.getOutMessage();
             if ( null != reqMsg) {
                 flowId = FlowIdHelper.getFlowId(reqMsg);
                 if ( null != flowId) {
                 LOG.fine("Using FlowId '" + flowId + "' from exchange.");
                 }
             }
        }
        }

        if (flowId != null) {
            LOG.fine("FlowId '" + flowId + "' found in incoming message.");
        } else {
            flowId = ContextUtils.generateUUID();
            LOG.fine("No flowId found in incoming message! Generate new flowId " + flowId);
        }

        FlowIdHelper.setFlowId(message, flowId);
    }

}
