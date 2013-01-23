package org.talend.esb.sam.service;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FlowEvent {
    
    private String Type;
    
    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }

    private String ID;
}
