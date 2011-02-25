package org.sopera.monitoring.handler.impl;

import java.util.logging.Logger;

import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.handler.EventManipulator;

/**
 * Content length handler is able to cut the message content within an event. Set the maximum length with setLength.
 * If content is cuttet it's stored inside <cut><![CDATA[ ]]></cut> 
 * 
 * @author cschmuelling
 *
 */
public class ContentLengthHandler extends AbstractFilteredHandler<Event> implements EventManipulator<Event> {

	private static Logger logger = Logger.getLogger(ContentLengthHandler.class
			.getName());
	//TODO Bei String den Cut wieder entfernen.
	final static String CUT_START_TAG = "<cut><![CDATA[";
	final static String CUT_END_TAG = "]]></cut>";
	
	private int length;

	public ContentLengthHandler() {
		super();
	}

	public int getLength() {
		return length;
	}

	/**
	 * Set the maximum length for the message. 
	 * @param length
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * Cut the message content to the configured length
	 */
	public void handleEvent(Event event) {
		logger.info("ContentLengthHandler called");

		//if maximum length is shorter then <cut><![CDATA[ ]]></cut> it's not possible to cut the content
		if(CUT_START_TAG.length()+CUT_END_TAG.length()>length){
			logger.warning("Trying to cut content. But length is shorter then needed for "+CUT_START_TAG+CUT_END_TAG+". So content is skipped.");
			event.setContent("");
			return;
		}
		
		int currentLength = length - CUT_START_TAG.length() - CUT_END_TAG.length();

		if (event.getContent() != null && event.getContent().length() > length) {
			logger.info("cutting content to " + currentLength
					+ " characters. Original length was "
					+ event.getContent().length());
			logger.fine("Content before cutting: " + event.getContent());
			event.setContent(CUT_START_TAG
					+ event.getContent().substring(0, currentLength) + CUT_END_TAG);
			logger.fine("Content after cutting: " + event.getContent());
		}
	}
}
