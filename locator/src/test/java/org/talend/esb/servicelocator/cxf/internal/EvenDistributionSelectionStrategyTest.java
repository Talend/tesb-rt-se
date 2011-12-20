/*
 * #%L
 * Service Locator Client for CXF
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
package org.talend.esb.servicelocator.cxf.internal;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class EvenDistributionSelectionStrategyTest {

    private static final String ADDR1 = "addr1";
    private static final String ADDR2 = "addr2";
    private static final String ADDR3 = "addr3";

    private EvenDistributionSelectionStrategy strategy = new EvenDistributionSelectionStrategy();

    @Test
    public void getRotatedList() {
        List<String> result = strategy.getRotatedList(Arrays.asList(ADDR1, ADDR2, ADDR3));
        Assert.assertTrue(Arrays.asList(ADDR2, ADDR3, ADDR1).equals(result));
    }

}
