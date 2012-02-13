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
        DoubleItPortType portType
    ) {
        doubleIt(portType, 10);
    }
    
    public static void main (String[] args) {
        DoubleItService service = new DoubleItService();
        DoubleItPortType port = service.getDoubleItPort();           

        doubleIt(port, 10);
    } 
    
    public static void doubleIt(DoubleItPortType port, 
            int numToDouble) {
        int resp = port.doubleIt(numToDouble);
        System.out.println("The number " + numToDouble + " doubled is " + resp);
    }
}
