package org.talend.esb.auxiliary.storage.client.rest.test;


public class Pojo {

    private int id;
    private String name;

    public Pojo() {
        this.id = 0;
        this.name = "John";
    }

    public Pojo(int id, String name) {
        this.id = id;
        this.name = name;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
