/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth2.common;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ReservationFailure {
	
	private String message;
    public ReservationFailure() {
        
    }
    
    public ReservationFailure(String message) {
        this.setMessage(message);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    
	
}
