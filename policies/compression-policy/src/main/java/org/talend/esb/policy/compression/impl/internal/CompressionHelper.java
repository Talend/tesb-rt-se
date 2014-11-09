package org.talend.esb.policy.compression.impl.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.staxutils.StaxUtils;

public class CompressionHelper {

	public static StreamPosition loadSoapBodyContent(InputStream source,
			OutputStream destination) throws XMLStreamException {
		XMLStreamReader reader = null;
		XMLStreamReader filteredReader = null;
		XMLStreamWriter writer = null;

		try {
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			factory.setProperty("com.ctc.wstx.outputValidateStructure", false);
			writer = factory.createXMLStreamWriter(destination, "UTF-8");

			SoapBodyStreamFilter soapBodyFilter = new SoapBodyStreamFilter();
			reader = StaxUtils.createXMLStreamReader(source, "UTF-8");
			filteredReader = StaxUtils.createFilteredReader(reader,
					soapBodyFilter);
			StaxUtils.copy(filteredReader, writer);
			writer.flush();
			return soapBodyFilter.getSoapBodyContentStreamPosition();
		} finally {
			writer.close();
			reader.close();
			filteredReader.close();
		}
	}

	public static void replaceBodyInSOAP(byte[] originalSoap,
			StreamPosition orgBodyPosition, InputStream newBody,
			OutputStream out) throws IOException {
		
		// Write Header
		out.write(originalSoap, 0, orgBodyPosition.getStart());

		// Write uncompressed soap body content
		IOUtils.copyAndCloseInput(newBody, out);

		// Write tail
		out.write(originalSoap, orgBodyPosition.getEnd(), originalSoap.length
				- orgBodyPosition.getEnd());
	}
}
