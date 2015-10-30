package org.talend.esb.examples.ebook.model;

import java.net.URI;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Format {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private URI file;
    
    /**
     * See http://dublincore.org/documents/2012/06/14/dcmi-terms/?v=terms#IMT
     */
    private String mediaType;
    
    private int extent;
    
    private String modified;
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }

    public URI getFile() {
        return file;
    }

    public void setFile(URI file) {
        this.file = file;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public int getExtent() {
        return extent;
    }

    public void setExtent(int extent) {
        this.extent = extent;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }
    
    
}
