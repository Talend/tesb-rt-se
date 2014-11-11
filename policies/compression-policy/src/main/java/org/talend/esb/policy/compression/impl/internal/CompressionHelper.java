package org.talend.esb.policy.compression.impl.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.staxutils.StaxUtils;

public class CompressionHelper {

	public static void loadSoapBodyContent(InputStream source,
			OutputStream destination, SoapBodyStreamFilter soapBodyFilter)
			throws XMLStreamException {
		XMLStreamReader reader = null;
		XMLStreamReader filteredReader = null;
		XMLStreamWriter writer = null;

		try {
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			factory.setProperty("com.ctc.wstx.outputValidateStructure", false);
			writer = factory.createXMLStreamWriter(destination, "UTF-8");

			reader = StaxUtils.createXMLStreamReader(source, "UTF-8");
			filteredReader = StaxUtils.createFilteredReader(reader,
					soapBodyFilter);
			StaxUtils.copy(filteredReader, writer);
			writer.flush();
		} finally {
			writer.close();
			reader.close();
			filteredReader.close();
		}
	}

	public static void replaceBodyInSOAP(byte[] originalSoap,
			SoapBodyStreamFilter filter, InputStream newBody,
			OutputStream out, String wrapperStartTag, String wrapperEndTag)
			throws IOException, XMLStreamException {

		// Write Header
		out.write(originalSoap, 0, filter.getBodyContentStart());

		// Write wrapper start tag
		if (wrapperStartTag != null) {
			out.write(wrapperStartTag.getBytes());
		}

		IOUtils.copyAndCloseInput(newBody, out);

		// Write wrapper end tag
		if (wrapperEndTag != null) {
			out.write(wrapperEndTag.getBytes());
		}

		// Write SOAP "tail"
		out.write(originalSoap, filter.getBodyContentEnd(), originalSoap.length
				- filter.getBodyContentEnd());
	}

	public static boolean isEqual(QName qn1, QName qn2, boolean ignoreCase) {
		if (qn1 == null && qn2 == null) {
			return true;
		}

		if (qn1 != null
				&& qn2 != null
				&& isEqual(qn1.getLocalPart(), qn2.getLocalPart(), ignoreCase)
				&& isEqual(qn1.getNamespaceURI(), qn2.getNamespaceURI(),
						ignoreCase)) {
			return true;
		}

		return false;

	}

	public static boolean isEqual(String str1, String str2, boolean ignoreCase) {
		if (str1 == null && str2 == null) {
			return true;
		}

		if (str1 != null && str2 != null) {
			if (ignoreCase) {
				return str1.equalsIgnoreCase(str2);
			} else {
				return str1.equals(str2);
			}
		}

		return false;
	}
}
