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
