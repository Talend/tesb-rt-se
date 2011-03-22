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
