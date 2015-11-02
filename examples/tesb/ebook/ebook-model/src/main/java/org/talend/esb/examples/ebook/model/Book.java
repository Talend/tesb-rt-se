package org.talend.esb.examples.ebook.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class Book {
    @Id
    private String id;
    
    private String title;
    
    /*
     * See http://dublincore.org/documents/2012/06/14/dcmi-terms/?v=terms#LCSH
     */
    @OneToMany(cascade=CascadeType.ALL)
    private List<Subject> subjects = new ArrayList<Subject>();
    
    private String language;
    
    private String creator;

    private Date birthDate;
    
    private int downloads;
    
    private String publisher;
    
    @OneToMany(cascade=CascadeType.ALL)
    private List<Format> formats = new ArrayList<Format>();

    private URI links;
    
    private URI cover;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public int getDownloads() {
        return downloads;
    }

    public void setDownloads(int downloads) {
        this.downloads = downloads;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public List<Format> getFormats() {
        return formats;
    }

    public void setFormats(List<Format> formats) {
        this.formats = formats;
    }

    public URI getLinks() {
        return links;
    }

    public void setLinks(URI links) {
        this.links = links;
    }
    
    public void setCover(URI cover) {
        this.cover = cover;
    }
    
    public URI getCover() {
        return cover;
    }
    
    @Override
    public String toString() {
        return getCreator() + " - " + getTitle();
    }
}
