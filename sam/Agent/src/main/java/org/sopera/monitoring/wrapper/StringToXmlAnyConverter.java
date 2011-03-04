package org.sopera.monitoring.wrapper;

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

import org.sopera.monitoring._2010._09.common.ContentType;
import org.sopera.monitoring._2010._09.common.ExtensionType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * convert string to ContentType and ExtensionType
 * @author Xilai Dai
 *
 */
public class StringToXmlAnyConverter {
	private static Logger logger = Logger.getLogger(StringToXmlAnyConverter.class
			.getName());
	
	public static ContentType convertToContentType(String str){
		if (str != null && !str.equals("")){
			Object content = getDocument(str);
			if (content == null) {
				content = getJaxBElementWrapper(str);
			}	
			
			ContentType cType = new ContentType();
			cType.getAny().add(content);
			
			return cType;
		}
		return null;
	}

	public static ExtensionType convertToExtensionType(String str){
		if (str != null && !str.equals("")){
			Object content = getDocument(str);
			if (content == null) {
				content = getJaxBElementWrapper(str);
			}
			
			ExtensionType eType = new ExtensionType();
			eType.getAny().add(content);
			
			return eType;
		}
		return null;
	}

	private static Object getJaxBElementWrapper(Object sourceFieldValue) {
		JAXBElement<String> wrapper = new JAXBElement<String>(new QName(
				"http://www.sopera.com/monitoring", "invalidContent"),
				String.class, ((String) sourceFieldValue));
		return wrapper;
	}

	private static Node getDocument(Object sourceFieldValue) {
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
