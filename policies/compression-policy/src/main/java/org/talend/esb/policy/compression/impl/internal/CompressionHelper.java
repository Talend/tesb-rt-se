package org.talend.esb.policy.compression.impl.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.regex.MatchResult;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.cxf.helpers.IOUtils;

public class CompressionHelper {
	
	public static int SOAP_BODY_WRAPPER_INDX = 0;
	public static int SOAP_BODY_INDX = 2;
	
	public static MatchResult loadSoapBodyContent(OutputStream destination, Scanner scanner, String pattern)
			throws XMLStreamException {

		try {
            scanner.findWithinHorizon(pattern, 0);
            MatchResult result = scanner.match();
            String bodyContent = result.group(CompressionHelper.SOAP_BODY_INDX);
            if(bodyContent!=null){
            	destination.write(bodyContent.getBytes());
            	return result;
            }else{
            	throw new RuntimeException("Compression: Can not find SOAP body");
            }
            
		}catch(IllegalStateException ex) {
			//TODO: Log message body is not found
			return null;
		} catch(Exception ex) {
			throw new RuntimeException("Compression: can not read SOAP body content", ex);	
		} finally {
			if(scanner!=null)scanner.close();
		}
	}

	public static void replaceBodyInSOAP(byte[] originalSoap,
			MatchResult bodyPosition, InputStream newBody,
			OutputStream out, String wrapperStartTag, String wrapperEndTag, 
			boolean removeWrapper)
			throws IOException, XMLStreamException {
		
		int patternGroupIndex = removeWrapper? CompressionHelper.SOAP_BODY_WRAPPER_INDX:
			CompressionHelper.SOAP_BODY_INDX;
		
		int orgBodyStart =  bodyPosition.start(patternGroupIndex);
		int orgBodyEnd =  bodyPosition.end(patternGroupIndex);

		// Write Header
		out.write(originalSoap, 0, orgBodyStart);

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
		out.write(originalSoap, orgBodyEnd, originalSoap.length
				- orgBodyEnd);
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
