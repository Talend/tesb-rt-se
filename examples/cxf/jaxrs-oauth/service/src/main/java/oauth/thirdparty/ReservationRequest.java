/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth.thirdparty;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.cxf.rs.security.oauth.client.OAuthClientUtils.Token;

@XmlRootElement
public class ReservationRequest {
	
	private Token requestToken;
	
    private String reserveName;
    private String contactPhone;
    private int hour;
	public void setReserveName(String reserveName) {
		this.reserveName = reserveName;
	}
	public String getReserveName() {
		return reserveName;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getHour() {
		return hour;
	}
	public void setRequestToken(Token requestToken) {
		this.requestToken = requestToken;
	}
	public Token getRequestToken() {
		return requestToken;
	}
}
