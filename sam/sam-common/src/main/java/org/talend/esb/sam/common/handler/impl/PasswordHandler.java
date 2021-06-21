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

import java.util.List;
import java.util.logging.Logger;

import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.spi.EventHandler;

/**
 * Password handler removes xml-tags within the content of the event. The complete tag will be removed with
 * "<!-- ---replaced--- -->"
 *
 * @author cschmuelling
 *
 */
public class PasswordHandler implements EventHandler {

    private static final String REPLACE = "<replaced xmlns=\"\"/>";

    private static final Logger LOG = Logger.getLogger(PasswordHandler.class.getName());

    private List<String> tagnames;

    /**
     * Instantiates a new password handler.
     */
    public PasswordHandler() {
        super();
    }

    /**
     * Gets the tagnames.
     *
     * @return the tagnames
     */
    public List<String> getTagnames() {
        return tagnames;
    }

    /**
     * Set a list with names, which should be filtered. For example "password" "passwort" This search is case
     * sensitive.
     *
     * @param tagnames the new tagnames
     */
    public void setTagnames(List<String> tagnames) {
        this.tagnames = tagnames;
    }

    /**
     * Replaces all configured elements with a ---replaced--- string.
     *
     * @param event the event
     */
    public void handleEvent(Event event) {
        LOG.fine("PasswordHandler called");

        if (tagnames==null || tagnames.size() == 0) {
            LOG.warning("Password filter is active but there is no filter tagname configured!");
        }

        if (tagnames != null && event.getContent() != null
                && event.getContent().length() > 0) {
            for (String tagname : tagnames) {
                event.setContent(event.getContent().replaceAll(
                        "<([^>]*)" + tagname
                                + "([^>]*)>([^<]*)<([^>]*)/([^>]*)" + tagname
                                + "([^>]*)>", REPLACE));
                event.setContent(event.getContent().replaceAll(
                        "<([^>]*)" + tagname + "([^>]*)/([^>]*)>",
                        REPLACE));

            }
            LOG.fine("Content after: " + event.getContent());
        }
    }
}
