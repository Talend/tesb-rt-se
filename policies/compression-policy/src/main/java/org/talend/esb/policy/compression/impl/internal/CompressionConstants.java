package org.talend.esb.policy.compression.impl.internal;

import javax.xml.namespace.QName;

public class CompressionConstants {
	
	/** The treshold attribute default. */
	public static int TRESHOLD_ATTRIBUTE_DEFAULT = 1024;
	
	public static QName SOAP_BODY_TAG_NAME = new QName("http://schemas.xmlsoap.org/soap/envelope/", "body");

	public static enum GZIP_ACTION {COMPRESSION, DECOMPRESSION};
	public static String COMPRESSION_WRAPPER_PREFIX = "tesb";
	public static String COMRESSION_WRAPPER_ALGORITM_PROPERTY = "algorithm";
	public static String COMRESSION_WRAPPER_ENCODING_PROPERTY = "encoding";
	public static String COMPRESSION_WRAPPER_TAG_LOCAL_NAME = "compressed";
	public static String COMPRESSION_WRAPPER_TAG_NAMESPACE = "http://talend.org/interceptors/Compression/1.0";
	public static QName COMPRESSION_WRAPPER_QNAME = new QName(COMPRESSION_WRAPPER_TAG_NAMESPACE, 
			COMPRESSION_WRAPPER_TAG_LOCAL_NAME);
	
	public static String SOAP_BODY_PATTERN = "(?six)<(.*):Body>(.*)</\\1:Body>";
	
	public static String COMPRESSED_SOAP_BODY_PATTERN = "(?six)<(.*):" + 
			COMPRESSION_WRAPPER_TAG_LOCAL_NAME + 
			" .* interceptors/Compression .*>(.*)</\\1:"+
			COMPRESSION_WRAPPER_TAG_LOCAL_NAME+">";
	
	public static String getCompressionWrapperStartTag(String algoritm, String encoding){
		StringBuilder str = new StringBuilder();
		str.append("<").
			append(COMPRESSION_WRAPPER_PREFIX).append(":").
			append(COMPRESSION_WRAPPER_TAG_LOCAL_NAME).
			append(" ").append(COMRESSION_WRAPPER_ALGORITM_PROPERTY).append("=\"").
			append(algoritm).append("\"").
			append(" ").append(COMRESSION_WRAPPER_ENCODING_PROPERTY).append("=\"").
			append(encoding).append("\"").
			append(" xmlns:").append(COMPRESSION_WRAPPER_PREFIX).append("=\"").
			append(COMPRESSION_WRAPPER_TAG_NAMESPACE+"\">"); 
		return str.toString();
	} 
	
	public static String getCompressionWrapperEndTag(){
		StringBuilder str = new StringBuilder();
			str.append("</").append(COMPRESSION_WRAPPER_PREFIX).append(":").
			append(COMPRESSION_WRAPPER_TAG_LOCAL_NAME).append(">");
		return str.toString();
	} 	
}
