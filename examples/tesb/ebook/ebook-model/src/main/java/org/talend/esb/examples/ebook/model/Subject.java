package org.talend.esb.examples.ebook.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Subject {
    @Id
    String subject;
    
    public Subject() {
    }
    
    public Subject(String subject) {
        super();
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
}
