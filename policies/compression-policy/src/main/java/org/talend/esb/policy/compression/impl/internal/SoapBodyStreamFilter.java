package org.talend.esb.policy.compression.impl.internal;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

public class SoapBodyStreamFilter implements StreamFilter {

	StreamPosition position = new StreamPosition();
	private boolean allowStreamFragment = false;

	@Override
	public boolean accept(XMLStreamReader reader) {
		
		int eventType = reader.getEventType();
		String currentTag = null;

		switch (eventType){
		
			case XMLStreamConstants.START_ELEMENT:
				
				// Get SOAP body child element position inside stream
				if (position.getStart() == StreamPosition.DEFAULT_POSITION
						&& allowStreamFragment) {

					position.setStart(reader.getLocation().getCharacterOffset());
				}

				// Do not allow processing stream fragments outside SOAP body
				currentTag = reader.getLocalName();
				if (currentTag.equalsIgnoreCase("body")) {
					allowStreamFragment = true;
					return false; // to skip body tag
				}
				
			break;
		
			case  XMLStreamConstants.END_ELEMENT:
				
				// Do not allow processing stream fragments outside SOAP body
				currentTag = reader.getLocalName();
				if (currentTag.equalsIgnoreCase("body")) {

					// Get SOAP body content last position inside stream
					position.setEnd(reader.getLocation().getCharacterOffset());
					allowStreamFragment = false;
				}
				
			break;

		default:
			
			if (position.getStart() == StreamPosition.DEFAULT_POSITION
			&& allowStreamFragment) {

				position.setStart(reader.getLocation().getCharacterOffset());
			}
			
		}
		
		return allowStreamFragment;
	}

	public StreamPosition getSoapBodyContentStreamPosition() {
		return position;
	}

}
