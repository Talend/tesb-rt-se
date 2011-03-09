package org.sopera.monitoring.filter.impl;

import java.util.List;

import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.filter.EventFilter;

public class StringContentFilter implements EventFilter<Event> {

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
