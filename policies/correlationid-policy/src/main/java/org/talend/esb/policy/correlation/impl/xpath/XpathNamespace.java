package org.talend.esb.policy.correlation.impl.xpath;

import javax.xml.namespace.QName;

import org.talend.esb.policy.correlation.impl.CorrelationIDPolicyBuilder;

public class XpathNamespace {
	
	/* name space prefix attribute */
	public static final String PREFIX_ATTRIBUTE = "prefix";
	
	/* name space uri attribute */
	public static final String URI_ATTRIBUTE = "uri";
	
	/* name space node name */
	public static final String XPATH_NAMESPACE_NODE_NAME = "Namespace";
	
	/* name space node QName */
	public static final QName  CORRELATION_NAMESPACE_NODE_QNAME = new QName(CorrelationIDPolicyBuilder.NAMESPACE, 
			XPATH_NAMESPACE_NODE_NAME);

	
	/* name space prefix */
	private String prefix = null;
	
	/* name space uri */
	private String uri = null;
	
	
	public XpathNamespace(){
	}


	public String getPrefix() {
		return prefix;
	}


	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}


	public String getUri() {
		return uri;
	}


	public void setUri(String uri) {
		this.uri = uri;
	}
}
