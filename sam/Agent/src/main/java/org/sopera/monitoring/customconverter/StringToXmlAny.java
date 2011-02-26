package org.sopera.monitoring.customconverter;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.dozer.CustomConverter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class StringToXmlAny implements CustomConverter {
	private static Logger logger = Logger.getLogger(StringToQName.class
			.getName());

	@SuppressWarnings("unchecked")
	public Object convert(Object existingDestinationFieldValue,
			Object sourceFieldValue, Class<?> destinationClass,
			Class<?> sourceClass) {

		if (sourceFieldValue != null && sourceFieldValue instanceof String
				&& !sourceFieldValue.equals("")) {

			// see other attempts to work with xml in Subversion revision 4882
			Object content = getDocument(sourceFieldValue);
			if (content == null) {
				content = getJaxBElementWrapper(sourceFieldValue);
			}

			Object target = null;
			try {
				target = destinationClass.newInstance();

				Method[] methodArray = destinationClass.getMethods();
				for (Method method : methodArray) {
					if ("getAny".equals(method.getName())) {

						Object object = method.invoke(target, new Object[] {});
						if (object != null && object instanceof List) {
							List<Object> objectList = (List<Object>) object;
							objectList.add(content);
						}
						break;
					}
				}
			} catch (Exception e) {
				logger.severe("Couldn't transform " + sourceClass + " to "
						+ destinationClass
						+ ". Error in 'any' element. Set Value to null.");
				return null;
			}
			return target;
		}

		return null;
	}

	private Object getJaxBElementWrapper(Object sourceFieldValue) {
		JAXBElement<String> wrapper = new JAXBElement<String>(new QName(
				"http://www.sopera.com/monitoring", "invalidContent"),
				String.class, ((String) sourceFieldValue));
		return wrapper;
	}

	private Node getDocument(Object sourceFieldValue) {
		if (sourceFieldValue != null && sourceFieldValue instanceof String) {
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db;

				db = dbf.newDocumentBuilder();
				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(
						(String) sourceFieldValue));

				Document doc = db.parse(is);
				return doc.getDocumentElement();
			} catch (ParserConfigurationException e) {
			} catch (SAXException e) {
			} catch (IOException e) {
			}
			logger.warning("Invalid message content.");
			return null;
		} else {
			return null;
		}
	}
}
