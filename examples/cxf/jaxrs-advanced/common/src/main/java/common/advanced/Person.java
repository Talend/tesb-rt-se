/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package common.advanced;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class can act as JAXB bean, JAX-RS sub-resource delegating to another
 * sub-resource and JAX-RS final resource dealing with the concrete HTTP verbs.
 * See getState() : this method is a JAX-RS resource method which also returns
 * the state of Person See getMother() and other methods having JAX-RS @Path
 * annotation only - these are JAX-RS sub-resource locators delegating to other
 * sub-resources. In this example they all delegate to the same instance to
 * handle the request via getState() methods. Also check updateAge() method. It
 * returns 'void' which will result in HTTP 204 being returned to client. This
 * method can also throw the exceptions which can be caught by JAX-RS
 * ExceptionMappers.
 */
@XmlRootElement(name = "Person", namespace = "http://org.persons")
@XmlAccessorType(XmlAccessType.FIELD)
@Produces({"application/xml", "application/json"})
@Entity
public class Person {
    @Id
    private long id;

    private String name;
    private int age;

    @OneToOne
    private Person mother;
    @OneToOne
    private Person father;
    @OneToOne
    private Person partner;
    
    @OneToMany
    @XmlIDREF
    private Set<Person> children = new HashSet<Person>();

    public Person() {
        this("unknown", 50);
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Person(String name, int age, Person m, Person f, Person p) {
        this.name = name;
        this.age = age;
        this.mother = m;
        this.father = f;
        this.partner = p;
    }

    @GET
    public Person getState() {
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    @Path("mother")
    public Person getMother() {
        return mother;
    }

    public void setMother(Person p) {
        if (mother != null && !mother.equals(p)) {
        	throw new IllegalStateException();
        }
        mother = p;
    }
    
    @Path("father")
    public Person getFather() {
        return father;
    }
    
    public void setFather(Person p) {
        if (father != null && !father.equals(p)) {
        	throw new IllegalStateException();
        }
        father = p;
    }

    @Path("partner")
    public Person getPartner() {
        return partner;
    }

    @GET
    @Path("children")
    public Set<Person> getChildren() {
        return children;
    }

    public void setChildren(Set<Person> all) {
    	this.children = all;
    }
    
    public void addChild(Person child) {
        children.add(child);
    }
    
    @GET
    @Path("descendants")
    public Set<Person> getDescendants() {
        Set<Person> ds = new HashSet<Person>();
        addChildren(ds, this);
        return ds;
    }

    @GET
    @Path("ancestors")
    public Set<Person> getAncestors() {
        Set<Person> as = new HashSet<Person>();
        addParents(as, this);
        return as;
    }

    @PUT
    @Consumes("text/plain")
    @Path("age")
    public void updateAge(int newAge) throws PersonUpdateException {
        if (age > newAge) {
            throw new PersonUpdateException();
        }
        setAge(newAge);
    }

    private void addParents(Set<Person> list, Person p) {
        Person m = p.getMother();
        if (m != null) {
            list.add(m);
            addParents(list, m);
        }
        Person f = p.getFather();
        if (f != null) {
            list.add(f);
            addParents(list, f);
        }
    }

    private void addChildren(Set<Person> list, Person p) {
        for (Person ch : p.getChildren()) {
            list.add(ch);
            addChildren(list, ch);
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode() + age;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Person) {
            Person other = (Person)o;
            return name.equals(other.name) && age == other.age;
        } else {
            return false;
        }
    }
    
    // The following is the hack recommended to bypass JAXB RI limitation
    // to do with XmlId value being strictly of type 'String'
    
    @SuppressWarnings("unused")
	@XmlID
    @XmlAttribute(name = "id")
    private String getXmlID(){
        return String.format("%s-%s", this.getClass().getSimpleName(), Long.valueOf(id));
    }

    @SuppressWarnings("unused")
	private void setXmlID(String xmlid){
        String prefix = String.format("%s-", this.getClass().getSimpleName());
        if(xmlid.startsWith(prefix)){
            this.id = Long.parseLong(xmlid.substring(prefix.length()));
        }else{
            throw new IllegalArgumentException(xmlid+" does not look like "+prefix+"###");
        }
    }
}
