package org.talend.esb.examples;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;

public class DoubleItSTSClient {

    final static String NAMESPACE = "http://examples.esb.talend.org/";

    final static QName SERVICE_QNAME = new QName(NAMESPACE, "DoubleItService");
	
	public static void main(String[] args) throws Exception {
		
		SpringBusFactory bf = new SpringBusFactory();

		Bus bus = bf.createBus("META-INF/spring/client-sts-beans.xml");
        SpringBusFactory.setDefaultBus(bus);
        SpringBusFactory.setThreadDefaultBus(bus);
        
        URL wsdl = DoubleItSTSClient.class.getResource("DoubleIt.wsdl");
        Service service = Service.create(wsdl, SERVICE_QNAME);
        
        QName portQName = new QName(NAMESPACE, "DoubleItAsymmetricSAML2Port");
        
        DoubleItPortType symmetricSaml2Port = service.getPort(portQName, DoubleItPortType.class);
        
        int x =  symmetricSaml2Port.execute(10);

        System.out.println(x);
	}
}
