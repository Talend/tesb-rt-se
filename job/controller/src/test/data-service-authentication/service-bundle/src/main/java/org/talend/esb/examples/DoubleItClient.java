package org.talend.esb.examples;

import javax.xml.ws.BindingProvider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DoubleItClient {

    public static final String NAME = "karaf_name";
    
    public static final String PASS = "karaf_password";

    public static void main(String[] args) throws Exception {


        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "META-INF/spring/client-beans.xml");
        DoubleIt client = (DoubleIt) context.getBean("DoubleItClient");

        ((BindingProvider) client).getRequestContext().put(
                BindingProvider.USERNAME_PROPERTY, NAME);
        ((BindingProvider) client).getRequestContext().put(
                BindingProvider.PASSWORD_PROPERTY, PASS);

        int result = client.execute(10);

        System.out.println("Result is " + result);
    }

}
