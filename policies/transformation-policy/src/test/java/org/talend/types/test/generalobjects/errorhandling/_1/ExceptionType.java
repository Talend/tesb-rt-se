
package org.talend.types.test.generalobjects.errorhandling._1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExceptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExceptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Parameter" type="{http://types.talend.org/test/GeneralObjects/ErrorHandling/1.0}ParameterType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="ServiceParticipant" use="required" type="{http://types.talend.org/test/GeneralObjects/ErrorHandling/1.0}ServiceParticipantType" />
 *       &lt;attribute name="DomainName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ServiceName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Version" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Operation" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ExceptionClass" use="required" type="{http://types.talend.org/test/GeneralObjects/ErrorHandling/1.0}ExceptionClassType" />
 *       &lt;attribute name="ExceptionID" use="required" type="{http://types.talend.org/test/GeneralObjects/ErrorHandling/1.0}ExceptionIDType" />
 *       &lt;attribute name="ExceptionText" use="required" type="{http://types.talend.org/test/GeneralObjects/ErrorHandling/1.0}ExceptionTextType" />
 *       &lt;attribute name="CAT_Severity" use="required" type="{http://types.talend.org/test/GeneralObjects/ErrorHandling/1.0}CAT_ITOLogLevel" />
 *       &lt;attribute name="hasBeenLogged" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="LogTargetList" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExceptionType", propOrder = {
    "parameter"
})
public class ExceptionType {

    @XmlElement(name = "Parameter")
    protected List<ParameterType> parameter;
    @XmlAttribute(name = "ServiceParticipant", required = true)
    protected String serviceParticipant;
    @XmlAttribute(name = "DomainName", required = true)
    protected String domainName;
    @XmlAttribute(name = "ServiceName", required = true)
    protected String serviceName;
    @XmlAttribute(name = "Version", required = true)
    protected String version;
    @XmlAttribute(name = "Operation", required = true)
    protected String operation;
    @XmlAttribute(name = "ExceptionClass", required = true)
    protected String exceptionClass;
    @XmlAttribute(name = "ExceptionID", required = true)
    protected BigDecimal exceptionID;
    @XmlAttribute(name = "ExceptionText", required = true)
    protected String exceptionText;
    @XmlAttribute(name = "CAT_Severity", required = true)
    protected CATITOLogLevel catSeverity;
    @XmlAttribute(name = "hasBeenLogged", required = true)
    protected boolean hasBeenLogged;
    @XmlAttribute(name = "LogTargetList")
    protected String logTargetList;

    /**
     * Gets the value of the parameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ParameterType }
     * 
     * 
     */
    public List<ParameterType> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<ParameterType>();
        }
        return this.parameter;
    }

    /**
     * Gets the value of the serviceParticipant property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceParticipant() {
        return serviceParticipant;
    }

    /**
     * Sets the value of the serviceParticipant property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceParticipant(String value) {
        this.serviceParticipant = value;
    }

    /**
     * Gets the value of the domainName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomainName() {
        return domainName;
    }

    /**
     * Sets the value of the domainName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomainName(String value) {
        this.domainName = value;
    }

    /**
     * Gets the value of the serviceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Sets the value of the serviceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceName(String value) {
        this.serviceName = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the operation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Sets the value of the operation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperation(String value) {
        this.operation = value;
    }

    /**
     * Gets the value of the exceptionClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExceptionClass() {
        return exceptionClass;
    }

    /**
     * Sets the value of the exceptionClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExceptionClass(String value) {
        this.exceptionClass = value;
    }

    /**
     * Gets the value of the exceptionID property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getExceptionID() {
        return exceptionID;
    }

    /**
     * Sets the value of the exceptionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setExceptionID(BigDecimal value) {
        this.exceptionID = value;
    }

    /**
     * Gets the value of the exceptionText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExceptionText() {
        return exceptionText;
    }

    /**
     * Sets the value of the exceptionText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExceptionText(String value) {
        this.exceptionText = value;
    }

    /**
     * Gets the value of the catSeverity property.
     * 
     * @return
     *     possible object is
     *     {@link CATITOLogLevel }
     *     
     */
    public CATITOLogLevel getCATSeverity() {
        return catSeverity;
    }

    /**
     * Sets the value of the catSeverity property.
     * 
     * @param value
     *     allowed object is
     *     {@link CATITOLogLevel }
     *     
     */
    public void setCATSeverity(CATITOLogLevel value) {
        this.catSeverity = value;
    }

    /**
     * Gets the value of the hasBeenLogged property.
     * 
     */
    public boolean isHasBeenLogged() {
        return hasBeenLogged;
    }

    /**
     * Sets the value of the hasBeenLogged property.
     * 
     */
    public void setHasBeenLogged(boolean value) {
        this.hasBeenLogged = value;
    }

    /**
     * Gets the value of the logTargetList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLogTargetList() {
        return logTargetList;
    }

    /**
     * Sets the value of the logTargetList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLogTargetList(String value) {
        this.logTargetList = value;
    }

}
