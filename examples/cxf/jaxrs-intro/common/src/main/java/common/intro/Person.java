/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package common.intro;

import javax.xml.bind.annotation.XmlRootElement;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

/**
 * This class serves as the JAXB bean for Java to XML translations between the
 * client and REST service as well as the JAX-RS sub-resource for the
 * MembershipService. Note subresources do not have a @Path annotation as root
 * resources do.
 */
@XmlRootElement(name = "Person")
public class Person {
    private int id;
    private String name;
    private int age = -1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @GET
    public Person getState() {
        return this;
    }

    @PUT
    public void updateSelf(Person newData) {
        this.name = newData.name;
        this.age = newData.age;
    }

    @Path("name")
    public String getName() {
        return name;
    }

    @PUT
    @Consumes("text/plain")
    @Path("name")
    public void setName(String name) {
        this.name = name;
    }

    @Path("age")
    public int getAge() {
        return age;
    }

    @PUT
    @Consumes("text/plain")
    @Path("age")
    public void setAge(int age) {
        this.age = age;
    }

}
