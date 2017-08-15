
package org.talend.types.test.library.common._1;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter2;


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
 *         &lt;element name="ISBNNumber" type="{http://types.talend.org/test/Library/Common/1.0}ISBNType"/>
 *         &lt;element name="DateOfBirth" type="{http://types.talend.org/test/Library/Common/1.0}DateType"/>
 *         &lt;element name="ZIP" type="{http://types.talend.org/test/Library/Common/1.0}ZIPType"/>
 *         &lt;element name="Borrowed" type="{http://types.talend.org/test/Library/Common/1.0}DateType"/>
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
    "isbnNumber",
    "dateOfBirth",
    "zip",
    "borrowed"
})
@XmlRootElement(name = "createLending")
public class CreateLending {

    @XmlElement(name = "ISBNNumber", required = true)
    protected String isbnNumber;
    @XmlElement(name = "DateOfBirth", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    protected Date dateOfBirth;
    @XmlElement(name = "ZIP", required = true)
    protected String zip;
    @XmlElement(name = "Borrowed", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    protected Date borrowed;

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

    /**
     * Gets the value of the dateOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the value of the dateOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateOfBirth(Date value) {
        this.dateOfBirth = value;
    }

    /**
     * Gets the value of the zip property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZIP() {
        return zip;
    }

    /**
     * Sets the value of the zip property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZIP(String value) {
        this.zip = value;
    }

    /**
     * Gets the value of the borrowed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Date getBorrowed() {
        return borrowed;
    }

    /**
     * Sets the value of the borrowed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBorrowed(Date value) {
        this.borrowed = value;
    }

}
