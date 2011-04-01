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

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.spi.EventFilter;

public class JxPathFilter implements EventFilter {
    String expression;

    public JxPathFilter() {
    }
    
    public JxPathFilter(String expression) {
        super();
        this.expression = expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public boolean filter(Event event) {
        JXPathContext context = JXPathContext.newContext(event);
        Pointer pointer = context.getPointer(expression);
        boolean shouldFilter = (Boolean)pointer.getValue();
        return shouldFilter;
    }

}
