package org.sopera.monitoring.customconverter;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.dozer.CustomConverter;
import org.w3c.dom.Node;

public class XmlAnyToString implements CustomConverter {

	private static Logger logger = Logger.getLogger(XmlAnyToString.class
			.getName());

	public Object convert(Object existingDestinationFieldValue,
			Object sourceFieldValue, Class<?> destinationClass,
			Class<?> sourceClass) {
		StringBuffer stringBuffer = new StringBuffer();

		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();

		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");

			if (sourceFieldValue instanceof List) {
				@SuppressWarnings("rawtypes")
				List list = (List) sourceFieldValue;
				if (!list.isEmpty()) {
					for (Object object : list) {
						if (object instanceof Node) {
							Node node = (Node) object;
							DOMSource source = new DOMSource(
									node.getOwnerDocument());
							ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
							Result output = new StreamResult(outputStream);

							transformer.transform(source, output);
							stringBuffer.append(outputStream);
						} else {
							logger.warning("Couldn't map xml any content: "
									+ object.toString());
						}
					}
				}
			}

			String anyString = stringBuffer.toString();
			if (anyString != null && !"".equals(anyString))
				logger.info("Transforming XML any to String: " + anyString);

			return anyString;

		} catch (TransformerConfigurationException e) {
			logger.log(
					Level.SEVERE,
					"Error in mapping web service event to business logic event. ",
					e);

		} catch (TransformerException e) {
			logger.log(
					Level.SEVERE,
					"Error in mapping web service event to business logic event. ",
					e);
		}
		return null;

	}

}
