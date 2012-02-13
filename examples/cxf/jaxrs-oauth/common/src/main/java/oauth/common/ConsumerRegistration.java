/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth.common;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ConsumerRegistration {
    private String id;
    private String secret;
    
    public ConsumerRegistration() {
        
    }
    
    public ConsumerRegistration(String id, String secret) {
        this.id = id;
        this.secret = secret;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }
}