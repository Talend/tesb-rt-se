/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package common.advanced;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Simple wrapper around an explicit collection. Generally, dealing with beans
 * wrapping the existing Java classes is recommended. This allows for
 * controlling the lower-level changes better and for these beans be represented
 * in generated schemas
 */
@XmlRootElement(name = "Persons", namespace = "http://org.persons")
public class PersonCollection {
    private List<Person> list = new ArrayList<Person>();

    public void addPerson(Person person) {
        getList().add(person);
    }

    public void setList(List<Person> list) {
        this.list = list;
    }

    public List<Person> getList() {
        return list;
    }
}
