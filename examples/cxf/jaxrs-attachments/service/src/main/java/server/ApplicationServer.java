/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package server;

import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;

/* 
 * This class is currently activated only if you use the mvn test -Pserver command
 * from the service folder (see this example's README.txt file).  It shows manual 
 * configuration of root resources, providers, etc. via the PersonApplication class.
 * 
 * For OSGi deployment the resources/META-INF/spring/beans.xml file is read and
 * configuration is performed from that file, and for standalone Tomcat or
 * embedded Jetty the beans.xml defined in the WAR submodule is used instead.
 */
public class ApplicationServer {

    protected ApplicationServer() throws Exception {
        AttachmentApplication application = new AttachmentApplication();
        RuntimeDelegate delegate = RuntimeDelegate.getInstance();

        JAXRSServerFactoryBean bean = delegate.createEndpoint(application, JAXRSServerFactoryBean.class);
        bean.setAddress("http://localhost:8080/services" + bean.getAddress());
        bean.create().start();
    }

    public static void main(String args[]) throws Exception {
        new ApplicationServer();
        System.out.println("Server ready...");

        Thread.sleep(125 * 60 * 1000);
        System.out.println("Server exiting");
        System.exit(0);
    }
}
