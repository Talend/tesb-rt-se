
package org.talend.services.test.library._1_0;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.7.12
 * 2014-09-29T09:31:37.055+03:00
 * Generated source version: 2.7.12
 */

@WebFault(name = "Exceptions", targetNamespace = "http://types.talend.org/test/GeneralObjects/ErrorHandling/1.0")
public class SeekBookError extends Exception {
    public static final long serialVersionUID = 1L;
    
    private org.talend.types.test.generalobjects.errorhandling._1.ExceptionFrame exceptions;

    public SeekBookError() {
        super();
    }
    
    public SeekBookError(String message) {
        super(message);
    }
    
    public SeekBookError(String message, Throwable cause) {
        super(message, cause);
    }

    public SeekBookError(String message, org.talend.types.test.generalobjects.errorhandling._1.ExceptionFrame exceptions) {
        super(message);
        this.exceptions = exceptions;
    }

    public SeekBookError(String message, org.talend.types.test.generalobjects.errorhandling._1.ExceptionFrame exceptions, Throwable cause) {
        super(message, cause);
        this.exceptions = exceptions;
    }

    public org.talend.types.test.generalobjects.errorhandling._1.ExceptionFrame getFaultInfo() {
        return this.exceptions;
    }
}
