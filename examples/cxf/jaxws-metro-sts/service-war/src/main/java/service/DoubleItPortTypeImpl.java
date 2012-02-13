/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package service;

import javax.jws.WebService;
import org.example.contract.doubleit.DoubleItPortType;
import org.apache.cxf.feature.Features;

@WebService(targetNamespace = "http://www.example.org/contract/DoubleIt", 
            serviceName="DoubleItService", 
            endpointInterface="org.example.contract.doubleit.DoubleItPortType")
@Features(features = "org.apache.cxf.feature.LoggingFeature")              
public class DoubleItPortTypeImpl implements DoubleItPortType {

    public int doubleIt(int numberToDouble) {
        return numberToDouble * 2;
    }
}
