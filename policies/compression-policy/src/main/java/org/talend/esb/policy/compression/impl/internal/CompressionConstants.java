package org.talend.esb.policy.compression.impl.internal;

import javax.xml.namespace.QName;

public class CompressionConstants {
	
	/** The treshold attribute default. */
	public static int TRESHOLD_ATTRIBUTE_DEFAULT = 1024;
	
	public static QName SOAP_BODY_TAG_NAME = new QName("http://schemas.xmlsoap.org/soap/envelope/", "body");

	public static enum GZIP_ACTION {COMPRESSION, DECOMPRESSION};
	public static String COMPRESSION_WRAPPER_PREFIX = "tesb";
	public static String COMPRESSION_WRAPPER_TAG_LOCAL_NAME = "GZIPCompressed";
	public static String COMPRESSION_WRAPPER_TAG_NAMESPACE = "http://talend.org/interceptors/Compression/1.0";
	public static QName COMPRESSION_WRAPPER_QNAME = new QName(COMPRESSION_WRAPPER_TAG_NAMESPACE, 
			COMPRESSION_WRAPPER_TAG_LOCAL_NAME);
	
	// <tesb:GZIPCompressed xmlns:tesb="http://headers.talend.org/interceptors/Compression/1.0">
	public static String COMPRESSION_WRAPPER_START_TAG = 
			"<"+
			COMPRESSION_WRAPPER_PREFIX +":"+
			COMPRESSION_WRAPPER_TAG_LOCAL_NAME + 
			" xmlns:"+COMPRESSION_WRAPPER_PREFIX+
			"=\""+
			COMPRESSION_WRAPPER_TAG_NAMESPACE+"\">";
	
	// </tesb:GZIPCompressed>
	public static String COMPRESSION_WRAPPER_END_TAG = "</" +
			COMPRESSION_WRAPPER_PREFIX+":" + 
			COMPRESSION_WRAPPER_TAG_LOCAL_NAME+">";
}
