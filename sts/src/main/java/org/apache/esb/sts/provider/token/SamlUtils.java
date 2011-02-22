package org.apache.esb.sts.provider.token;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.w3c.dom.Document;

public class SamlUtils {

	public static Document toDom(XMLObject object) throws MarshallingException, ParserConfigurationException, ConfigurationException {
		Document document = getDocumentBuilder().newDocument();

		DefaultBootstrap.bootstrap();

		Marshaller out = Configuration.getMarshallerFactory().getMarshaller(
				object);
		out.marshall(object, document);
		return document;
	}

	private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);

		return factory.newDocumentBuilder();
	}


}
