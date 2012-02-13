/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package common.codefirst;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(namespace = "http://hello.com", name = "User")
@XmlType(name = "User")
public class UserImpl implements User {
    String name;

    public UserImpl() {
    }

    public UserImpl(String s) {
        name = s;
    }

    public String getName() {
        return name;
    }

    public void setName(String s) {
        name = s;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object o) {
        return o instanceof User && name.equals(((User)o).getName());
    }
}
