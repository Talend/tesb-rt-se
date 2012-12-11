/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package common.advanced;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Basic Person Representation
 */
@XmlRootElement(name = "PersonInfo", namespace = "http://org.persons")
public class PersonInfo {
    private long id;

    public PersonInfo() {
    	
    }
    
    public PersonInfo(long id) {
    	this.id = id;
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
    
    
}
