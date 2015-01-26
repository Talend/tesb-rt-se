package org.talend.esb.policy.correlation.impl.xpath;

import javax.xml.namespace.QName;

import org.talend.esb.policy.correlation.impl.CorrelationIDPolicyBuilder;

public class XpathPart {
	
	/* part name attribute */
	public static final String PART_NAME_ATTRIBUTE = "name";
	
	/* part xpath attribute */
	public static final String PART_XPATH_ATTRIBUTE = "xpath";
	
	/* part optional attribute */
	public static final String PART_OPTIONAL_ATTRIBUTE = "optional";	
	
	/* correlation part name */
	public static final String XPATH_PART_NODE_NAME = "Part";
	

	/* correlation part id */
	public static final QName CORRELATION_PART_ID = new QName(CorrelationIDPolicyBuilder.NAMESPACE, 
			XPATH_PART_NODE_NAME);

	
	/* part name */
	private String name = null;
	
	/* xpath */
	private String xpath = null;
	
	/* optional */
	private boolean optional = false;	
	
	public XpathPart(){
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

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	
}
