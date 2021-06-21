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
package org.talend.esb.sam.agent.message;

import org.apache.cxf.message.Message;

/**
 * The Class FlowIdHelper used as helper for flow id setting.
 */
public final class FlowIdHelper {

    public static final String FLOW_ID_KEY = "FlowId";

    /**
     * Instantiates a new flow id helper.
     */
    private FlowIdHelper() {
    }

    /**
     * Get FlowId from message.
     *
     * @param message the message
     * @return flowId or null if not set
     */
    public static String getFlowId(Message message) {
        return (String) message.get(FLOW_ID_KEY);
    }

    /**
     * Sets the flow id.
     *
     * @param message the message
     * @param flowId the flow id
     */
    public static void setFlowId(Message message, String flowId) {
        message.put(FLOW_ID_KEY, flowId);
    }

}
