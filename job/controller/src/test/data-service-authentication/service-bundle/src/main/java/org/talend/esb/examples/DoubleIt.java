package org.talend.esb.examples;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface DoubleIt {

    @WebMethod
    int execute(int number);
}
