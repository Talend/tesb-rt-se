package org.talend.esb.sam.service;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Flow {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
