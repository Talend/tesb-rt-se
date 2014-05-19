package org.talend.esb.mep.requestcallback.impl.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.apache.cxf.wsdl.TExtensibilityElementImpl;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "callback", namespace = "http://www.talend.org/esb/mep/requestcallback")
public class CallbackExtension extends TExtensibilityElementImpl {

	@XmlAttribute(name = "name", required = true)
	@XmlSchemaType(name = "anyURI")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Extension named '" + getName() + "' with type '"
				+ getElementType() + "'";
	}
}
