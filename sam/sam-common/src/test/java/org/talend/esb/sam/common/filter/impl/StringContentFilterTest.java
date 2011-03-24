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
