/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package client;

import org.example.contract.doubleit.DoubleItPortType;
import org.example.contract.doubleit.DoubleItService;

public class WSClient {
    
    public WSClient() {
        //
    }

    public WSClient(
        DoubleItPortType utPortType,
        DoubleItPortType x509PortType,
        DoubleItPortType saml2PortType
    ) {
        int value = 10;
        doubleIt(utPortType, value);
        value += 5;
        doubleIt(x509PortType, value);
        value += 5;
        doubleIt(saml2PortType, value);
    }
    
    public final void doWork() {
        DoubleItService service = new DoubleItService();

        // UsernameToken port
        DoubleItPortType utPort = service.getDoubleItPortUT();
        doubleIt(utPort, 10);

        // X.509 port
        DoubleItPortType x509Port = service.getDoubleItPortX509();
        doubleIt(x509Port, 15);

        // SAML2 port
        DoubleItPortType saml2Port = service.getDoubleItPortSAML2();
        doubleIt(saml2Port, 20);
    }

    public static void doubleIt(DoubleItPortType port, int numToDouble) {
        int resp = port.doubleIt(numToDouble);
        System.out.println("The number " + numToDouble + " doubled is " + resp);
    }
    
    public static void main(String[] args) {
        new WSClient().doWork();
    }
}
