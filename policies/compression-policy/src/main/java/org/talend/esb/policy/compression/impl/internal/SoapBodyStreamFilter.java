package org.talend.esb.policy.compression.impl.internal;

import javax.xml.namespace.QName;
import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

public class SoapBodyStreamFilter implements StreamFilter {
	
	public static final int DEFAULT_POSITION = -1;

	private int bodyContentStart = DEFAULT_POSITION;
	private int bodyContentEnd = DEFAULT_POSITION;
	

	QName wrapper = null;

	public SoapBodyStreamFilter(QName wrapper) {
		this.wrapper = wrapper;
	}

	private boolean allowStreamFragment = false;
	private boolean soapBodyIsFound = false;
	private boolean wrapperIsFound = false;

	@Override
	public boolean accept(XMLStreamReader reader) {

		int eventType = reader.getEventType();
		QName currentTag = null;

		switch (eventType) {

		case XMLStreamConstants.START_ELEMENT:


			if (bodyContentStart == DEFAULT_POSITION
					&& soapBodyIsFound) {

				bodyContentStart = reader.getLocation().getCharacterOffset();
			}

			// Do not allow processing stream fragments outside SOAP body
			currentTag = reader.getName();

			if (CompressionHelper.isEqual(currentTag, CompressionConstants.SOAP_BODY_TAG_NAME, true)){
				soapBodyIsFound = true;
			}
			
			if (CompressionHelper.isEqual(currentTag, wrapper, true)){
				wrapperIsFound = true;
				allowStreamFragment = true;
				return false; // to skip body tag
			}

			break;

		case XMLStreamConstants.END_ELEMENT:

			// Do not allow processing stream fragments outside SOAP body
			currentTag = reader.getName();
			
			
			if (CompressionHelper.isEqual(currentTag, wrapper, true)){
				allowStreamFragment = false;
			}
			
			if (CompressionHelper.isEqual(currentTag, CompressionConstants.SOAP_BODY_TAG_NAME, true)){
				// Get SOAP body content last position inside stream
				bodyContentEnd = reader.getLocation().getCharacterOffset();
			}

			break;

		default:

			if (bodyContentStart == DEFAULT_POSITION
					&& soapBodyIsFound) {

				bodyContentStart = reader.getLocation().getCharacterOffset();
			}
		}

		return allowStreamFragment;
	}


	public boolean isWrapperFound() {
		return wrapperIsFound;
	}
	
	public int getBodyContentStart() {
		return bodyContentStart;
	}


	public int getBodyContentEnd() {
		return bodyContentEnd;
	}


	public int getBodyContentSize() {
		if (bodyContentEnd == DEFAULT_POSITION || bodyContentStart == DEFAULT_POSITION) {
			return 0;
		}
		return bodyContentEnd - bodyContentStart;
	}

	public boolean isBodyContentFound() {
		return (this.bodyContentStart != DEFAULT_POSITION && this.bodyContentEnd != DEFAULT_POSITION);
	}

}
