/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */

package common.advanced;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

    private final static QName _Person_QNAME = new QName("http://org.persons", "Person");
    private final static QName _PersonCollection_QNAME = new QName("http://org.persons", "Persons");

    /**
     * Create a new ObjectFactory that can be used to create new instances of
     * schema derived classes for package: com.example.customerservice
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Person }
     */
    public Person createPerson() {
        return new Person();
    }

    /**
     * Create an instance of {@link PersonCollection }
     */
    public PersonCollection createPersonCollection() {
        return new PersonCollection();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}
     * {@link Person }{@code >}
     */
    @XmlElementDecl(namespace = "http://org/persons/", name = "Person")
    public JAXBElement<Person> createPerson(Person value) {
        return new JAXBElement<Person>(_Person_QNAME, Person.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}
     * {@link PersonCollection }{@code >}
     */
    @XmlElementDecl(namespace = "http://org/persons/", name = "Persons")
    public JAXBElement<PersonCollection> createPersonCollection(PersonCollection value) {
        return new JAXBElement<PersonCollection>(_PersonCollection_QNAME, PersonCollection.class, null, value);
    }

}
