/*
 * #%L
 * Service Activity Monitoring :: Common
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
package org.talend.esb.sam.common.handler.impl;

import java.util.logging.Logger;

import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.spi.EventHandler;

/**
 * Content length handler is able to cut the message content within an event. Set the maximum length with setLength.
 * If content is cut it's stored inside <cut><![CDATA[ ]]></cut>
 *
 * @author cschmuelling
 *
 */
public class ContentLengthHandler implements EventHandler {

    //TODO Bei String den Cut wieder entfernen.
    static final String CUT_START_TAG = "<cut><![CDATA[";
    static final String CUT_END_TAG = "]]></cut>";

    private static final Logger LOG = Logger.getLogger(ContentLengthHandler.class.getName());

    private int length;

    /**
     * Instantiates a new content length handler.
     */
    public ContentLengthHandler() {
        super();
    }

    /**
     * Gets the length.
     *
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * Set the maximum length for the message.
     *
     * @param length the new length
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * Cut the message content to the configured length.
     *
     * @param event the event
     */
    public void handleEvent(Event event) {
        LOG.fine("ContentLengthHandler called");

        //if maximum length is shorter then <cut><![CDATA[ ]]></cut> it's not possible to cut the content
        if(CUT_START_TAG.length() + CUT_END_TAG.length() > length) {
            LOG.warning("Trying to cut content. But length is shorter then needed for "
                + CUT_START_TAG + CUT_END_TAG + ". So content is skipped.");
            event.setContent("");
            return;
        }

        int currentLength = length - CUT_START_TAG.length() - CUT_END_TAG.length();

        if (event.getContent() != null && event.getContent().length() > length) {
            LOG.fine("cutting content to " + currentLength
                    + " characters. Original length was "
                    + event.getContent().length());
            event.setContent(CUT_START_TAG
                    + event.getContent().substring(0, currentLength) + CUT_END_TAG);
            LOG.fine("Content after cutting: " + event.getContent());
        }
    }
}
