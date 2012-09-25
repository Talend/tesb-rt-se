package client;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import java.net.URL;

import service.DoubleItPortType;

public class WSClient {
    private static final QName SERVICE_NAME
        = new QName("http://www.example.org/contract/DoubleIt", "DoubleItService");
    private static final QName PORT_NAME
        = new QName("http://www.example.org/contract/DoubleIt", "DoubleItPort");

    public static void main (String[] args) throws Exception {
        // Use below line for Talend ESB deployment of web service provider
        String endpointAddress = "http://localhost:8040/services/doubleit";
        // Use below instead for servlet deployment
        // String endpointAddress = "http://localhost:8080/doubleit/services/doubleit";
        Service service = Service.create(new URL(endpointAddress +"?wsdl"), SERVICE_NAME);
        DoubleItPortType port = service.getPort(DoubleItPortType.class);

        doubleIt(port, 10);
        doubleIt(port, 0);
        doubleIt(port, -10);
    } 

    public static void doubleIt(DoubleItPortType port, 
            int numToDouble) {
        int resp = port.doubleIt(numToDouble);
        System.out.println("The number " + numToDouble + " doubled is " 
            + resp);
    }
}

