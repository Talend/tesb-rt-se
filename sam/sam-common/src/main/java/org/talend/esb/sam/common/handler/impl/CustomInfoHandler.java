/*
 * #%L
 * Service Activity Monitoring :: Common
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
package org.talend.esb.sam.common.handler.impl;

import java.util.List;
import java.util.Map;

import org.talend.esb.sam.common.event.CustomInfo;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.spi.EventManipulator;

/**
 * Adds a fixed set of custom info fields to the event
 */
public class CustomInfoHandler implements EventManipulator {
    private Map<String, Object> customInfo;

    @Override
    public void handleEvent(Event event) {
        if (customInfo == null) {
            return;
        }
        List<CustomInfo> ciList = event.getCustomInfoList();
        for (Map.Entry<String, Object> props : customInfo.entrySet()) {
            CustomInfo ci = new CustomInfo();
            ci.setCustKey(props.getKey());
            ci.setCustValue(props.getValue());
            ciList.add(ci);
        }
    }

    public void setCustomInfo(Map<String, Object> customInfo) {
        this.customInfo = customInfo;
    }

}
