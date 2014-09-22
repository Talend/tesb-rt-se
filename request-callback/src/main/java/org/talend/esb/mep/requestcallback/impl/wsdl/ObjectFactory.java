package org.talend.esb.mep.requestcallback.impl.wsdl;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PLType }
     * 
     */
    public PLType createPLType() {
        return new PLType();
    }
    
    /**
     * Create an instance of {@link PLPortType }
     * 
     */
    public PLPortType createPLPortType() {
        return new PLPortType();
    }

    /**
     * Create an instance of {@link PLRole }
     * 
     */
    public PLRole createPLRole() {
        return new PLRole();
    }
}
