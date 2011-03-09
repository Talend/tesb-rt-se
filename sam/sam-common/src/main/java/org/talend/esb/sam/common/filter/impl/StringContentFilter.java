package org.talend.esb.sam.common.filter.impl;

import java.util.List;

import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.spi.EventFilter;

public class StringContentFilter implements EventFilter {

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
