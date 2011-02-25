package org.sopera.monitoring.handler.impl;

import java.util.List;
import java.util.logging.Logger;

import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.handler.EventManipulator;

/**
 * Password handler removes xml-tags within the content of the event. The complete tag will be removed with "<!-- ---replaced--- -->"
 *  
 * @author cschmuelling
 *
 */
public class PasswordHandler extends AbstractFilteredHandler<Event> implements EventManipulator<Event> {

	private static final String REPLACE = "<replaced xmlns=\"\"/>";
	
	private static Logger logger = Logger.getLogger(PasswordHandler.class
			.getName());

	private List<String> tagnames;

	public PasswordHandler() {
		super();
	}

	public List<String> getTagnames() {
		return tagnames;
	}

	/**
	 * Set a list with names, which should be filtered. For example "password" "passwort" This search is case sensitive.
	 * @param tagnames
	 */
	public void setTagnames(List<String> tagnames) {
		this.tagnames = tagnames;
	}

	/**
	 * Replaces all configured elements with a ---replaced--- string
	 */
	public void handleEvent(Event event) {
		logger.info("PasswordHandler called");
		
		if(tagnames==null||tagnames.size()==0)
			logger.warning("Password filter is active but there is no filter tagname configured!");

		if (tagnames != null && event.getContent() != null
				&& event.getContent().length() > 0) {
			logger.fine("Content before: " + event.getContent());
			for (String tagname : tagnames) {
				event.setContent(event.getContent().replaceAll(
						"<([^>]*)" + tagname
								+ "([^>]*)>([^<]*)<([^>]*)/([^>]*)" + tagname
								+ "([^>]*)>", REPLACE));
				event.setContent(event.getContent().replaceAll(
						"<([^>]*)" + tagname + "([^>]*)/([^>]*)>",
						REPLACE));

			}
			logger.fine("Content after: " + event.getContent());
		}
	}
}
