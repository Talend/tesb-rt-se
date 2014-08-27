package org.talend.esb.policy.correlation.impl;

import javax.xml.namespace.QName;

public class CorrelationIDPart {
	
	/* part name attribute */
	public static final String PART_NAME_ATTRIBUTE = "name";
	
	/* part xpath attribute */
	public static final String PART_XPATH_ATTRIBUTE = "xpath";
	
	/* part optional attribute */
	public static final String PART_OPTIONAL_ATTRIBUTE = "optional";	
	
	/* correlation part name */
	public static final String CORRELATION_PART_NAME = "Part";

	/* correlation part id */
	public static final QName CORRELATION_PART_ID = new QName(CorrelationIDPolicyBuilder.NAMESPACE, 
			CORRELATION_PART_NAME);

	
	/* part name */
	private String name = null;
	
	/* xpath */
	private String xpath = null;
	
	/* optional */
	private boolean optional = false;	
	
	/* result of XPATH extraction */
	private String value = null;	
	
	public CorrelationIDPart(){
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}
	
	
	
}
