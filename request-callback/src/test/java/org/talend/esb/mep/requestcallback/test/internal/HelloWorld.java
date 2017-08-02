package org.talend.esb.mep.requestcallback.test.internal;


import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface HelloWorld {
    String sayHi(@WebParam(name = "text") String text);
}
