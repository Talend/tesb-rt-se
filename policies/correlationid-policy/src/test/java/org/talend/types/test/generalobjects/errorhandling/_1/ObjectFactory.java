
package org.talend.types.test.generalobjects.errorhandling._1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.talend.types.test.generalobjects.errorhandling._1 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Exceptions_QNAME = new QName("http://types.talend.org/test/GeneralObjects/ErrorHandling/1.0", "Exceptions");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.talend.types.test.generalobjects.errorhandling._1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ExceptionFrame }
     * 
     */
    public ExceptionFrame createExceptionFrame() {
        return new ExceptionFrame();
    }

    /**
     * Create an instance of {@link ParameterType }
     * 
     */
    public ParameterType createParameterType() {
        return new ParameterType();
    }

    /**
     * Create an instance of {@link ExceptionType }
     * 
     */
    public ExceptionType createExceptionType() {
        return new ExceptionType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExceptionFrame }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://types.talend.org/test/GeneralObjects/ErrorHandling/1.0", name = "Exceptions")
    public JAXBElement<ExceptionFrame> createExceptions(ExceptionFrame value) {
        return new JAXBElement<ExceptionFrame>(_Exceptions_QNAME, ExceptionFrame.class, null, value);
    }

}
