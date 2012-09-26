package service;

import javax.jws.WebService;

@WebService(targetNamespace = "http://www.example.org/contract/DoubleIt",
            endpointInterface = "service.DoubleItPortType",
            serviceName = "DoubleItService",
            portName = "DoubleItPort")
public class DoubleItPortTypeImpl implements DoubleItPortType {

    public int doubleIt(int numberToDouble) {
        return numberToDouble * 2;
    }

}
