
package org.talend.types.test.library.common._1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Publisher" type="{http://types.talend.org/test/Library/Common/1.0}LongNameType" minOccurs="0"/>
 *         &lt;element name="AuthorLastName" type="{http://types.talend.org/test/Library/Common/1.0}NameType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ISBNNumber" type="{http://types.talend.org/test/Library/Common/1.0}ISBNType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "publisher",
    "authorLastName",
    "isbnNumber"
})
@XmlRootElement(name = "SearchInBasementFor")
public class SearchInBasementFor {

    @XmlElement(name = "Publisher")
    protected String publisher;
    @XmlElement(name = "AuthorLastName")
    protected List<String> authorLastName;
    @XmlElement(name = "ISBNNumber")
    protected String isbnNumber;

    /**
     * Gets the value of the publisher property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Sets the value of the publisher property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPublisher(String value) {
        this.publisher = value;
    }

    /**
     * Gets the value of the authorLastName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the authorLastName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAuthorLastName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAuthorLastName() {
        if (authorLastName == null) {
            authorLastName = new ArrayList<String>();
        }
        return this.authorLastName;
    }

    /**
     * Gets the value of the isbnNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getISBNNumber() {
        return isbnNumber;
    }

    /**
     * Sets the value of the isbnNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setISBNNumber(String value) {
        this.isbnNumber = value;
    }

}
