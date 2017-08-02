package org.talend.esb.mep.requestcallback.test.internal;


public class HelloWorldImpl implements HelloWorld {

    public String sayHi(String text) {
        return "Hello " + text;
    }
}