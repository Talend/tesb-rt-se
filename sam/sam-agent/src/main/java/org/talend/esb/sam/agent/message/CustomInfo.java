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

import java.util.HashMap;

import org.apache.cxf.message.Message;

/**
 * Customer interceptors should store the data that should go into the monitoring event
 * in this map. All key/value pairs will be automatically copied to the customInfo map of the Event
 */
public class CustomInfo extends HashMap<String, String> {

    /**
     *
     */
    private static final long serialVersionUID = 3011186676959189278L;

    /**
     * Access the customInfo of a message using this accessor. The CustomInfo
     * map will be automatically created and stored in the event if it is not yet present
     *
     * @param message
     * @return
     */
    public static CustomInfo getOrCreateCustomInfo(Message message) {
        CustomInfo customInfo = message.get(CustomInfo.class);
        if (customInfo == null) {
            customInfo = new CustomInfo();
            message.put(CustomInfo.class, customInfo);
        }
        return customInfo;
    }

}
