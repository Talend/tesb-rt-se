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
package org.talend.esb.sam.common.filter.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.talend.esb.sam.common.event.Event;

public class StringContentFilterTest {
    @Test
    public void testFilter() {
        StringContentFilter filter = new StringContentFilter();
        List<String> wordsList = new ArrayList<String>();
        wordsList.add("confidential");
        filter.setWordsToFilter(wordsList );

        Event event = new Event();
        event.setContent("This event is confidential");

        Assert.assertTrue(filter.filter(event));
    }
}
