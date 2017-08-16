package org.talend.esb.servicelocator.cxf.internal.internal;


import javax.jws.WebParam;

public class HelloWorldImpl implements HelloWorld {
    @Override
    public String sayHi(@WebParam(name = "text") String text) {
        return "Hello " + text;
    }
}
