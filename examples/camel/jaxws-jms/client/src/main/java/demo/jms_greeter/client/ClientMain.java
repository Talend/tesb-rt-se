package demo.jms_greeter.client;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ClientMain {

    public static void main(String args[]) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/client.xml");
        Client client = context.getBean("client", Client.class);
        client.run();
        context.close();
    }
}
