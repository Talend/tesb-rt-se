package org.talend.esb.mep.requestcallback.impl.wsdl;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.apache.cxf.wsdl.TExtensibilityElementImpl;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "partnerLinkType", namespace = "http://schemas.xmlsoap.org/ws/2003/05/partner-link/")
public class PLType extends TExtensibilityElementImpl {

	@XmlAttribute(name = "name", required = true)
	@XmlSchemaType(name = "anyURI")
	private String name;
	
	@XmlElement(name = "role")
	private List<PLRole> roles;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<PLRole> getRoles() {
		return roles;
	}
	
	public void setRoles(List<PLRole> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Partner Link '");
		sb.append(getName());
		sb.append("': ");
		if (roles == null || roles.size() == 0) {
			sb.append("<empty>");
			return sb.toString();
		}
		boolean first = true;
		for (PLRole r : roles) {
			if (!first) {
				sb.append(" / ");
			} else {
				first = false;
			}
			sb.append(r.toString());
		}
		return sb.toString();
	}
}
