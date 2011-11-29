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
package org.talend.esb.sam.common.filter.impl;

import java.util.List;
import java.util.logging.Logger;

import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.spi.EventFilter;

public class StringContentFilter implements EventFilter {

    private static final Logger LOG = Logger.getLogger(StringContentFilter.class.getName());

    private List<String> wordsToFilter;

    public List<String> getWordsToFilter() {
        return wordsToFilter;
    }

    public void setWordsToFilter(List<String> wordsToFilter) {
        this.wordsToFilter = wordsToFilter;
    }

    /**
     * Filter event if word occurs in wordsToFilter
     */
    public boolean filter(Event event) {
        LOG.info("StringContentFilter called");
        
        if (wordsToFilter != null) {
            for (String filterWord : wordsToFilter) {
                if (event.getContent() != null
                        && -1 != event.getContent().indexOf(filterWord)) {
                    return true;
                }
            }
        }
        return false;
    }
}
