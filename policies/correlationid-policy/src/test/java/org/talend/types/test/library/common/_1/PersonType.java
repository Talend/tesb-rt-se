
package org.talend.types.test.library.common._1;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter2;


/**
 * <p>Java class for PersonType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PersonType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PersonID" type="{http://types.talend.org/test/Library/Common/1.0}IDType"/>
 *         &lt;element name="LastName" type="{http://types.talend.org/test/Library/Common/1.0}NameType"/>
 *         &lt;element name="FirstName" type="{http://types.talend.org/test/Library/Common/1.0}NameType"/>
 *         &lt;element name="TitleID" type="{http://types.talend.org/test/Library/Common/1.0}IDType" minOccurs="0"/>
 *         &lt;element name="SalutationID" type="{http://types.talend.org/test/Library/Common/1.0}IDType" minOccurs="0"/>
 *         &lt;element name="DateOfBirth" type="{http://types.talend.org/test/Library/Common/1.0}DateType"/>
 *         &lt;element name="EMail" type="{http://types.talend.org/test/Library/Common/1.0}MailToType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonType", propOrder = {
    "personID",
    "lastName",
    "firstName",
    "titleID",
    "salutationID",
    "dateOfBirth",
    "eMail"
})
public class PersonType {

    @XmlElement(name = "PersonID", required = true)
    protected String personID;
    @XmlElement(name = "LastName", required = true)
    protected String lastName;
    @XmlElement(name = "FirstName", required = true)
    protected String firstName;
    @XmlElement(name = "TitleID")
    protected String titleID;
    @XmlElement(name = "SalutationID")
    protected String salutationID;
    @XmlElement(name = "DateOfBirth", required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    protected Date dateOfBirth;
    @XmlElement(name = "EMail", required = true)
    protected String eMail;

    /**
     * Gets the value of the personID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPersonID() {
        return personID;
    }

    /**
     * Sets the value of the personID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPersonID(String value) {
        this.personID = value;
    }

    /**
     * Gets the value of the lastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the titleID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitleID() {
        return titleID;
    }

    /**
     * Sets the value of the titleID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitleID(String value) {
        this.titleID = value;
    }

    /**
     * Gets the value of the salutationID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSalutationID() {
        return salutationID;
    }

    /**
     * Sets the value of the salutationID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSalutationID(String value) {
        this.salutationID = value;
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
     * Gets the value of the eMail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEMail() {
        return eMail;
    }

    /**
     * Sets the value of the eMail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEMail(String value) {
        this.eMail = value;
    }

}
