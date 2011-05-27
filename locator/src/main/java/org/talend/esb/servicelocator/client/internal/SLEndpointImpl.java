package org.talend.esb.servicelocator.client.internal;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.talend.esb.servicelocator.client.SLEndpoint;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.internal.endpoint.EndpointDataType;
import org.w3c.dom.Element;

public class SLEndpointImpl implements SLEndpoint{

    private QName sName;
    
    private String address;
    
    private boolean isLive;
    
    private long lastTimeStarted = -1;
    
    public SLEndpointImpl(QName serviceName, byte[] content, boolean live) throws ServiceLocatorException{
        sName = serviceName;
        isLive = live;
        if (content != null) {
            EndpointDataType endpointData = toEndPointData(content);
            lastTimeStarted = Long.decode(endpointData.getLastTimeStarted());
            Element eprRoot = (Element) endpointData.getAny();
            EndpointReferenceType epr =  toEndPointReference(eprRoot);
            address = epr.getAddress().getValue();
        } else {
            throw new IllegalArgumentException("content must not be null.");
        }
    }
    
    @Override
    public String getAddress() {
        return address;
    }


    @Override
    public QName forService() {
        return sName;
    }

    @Override
    public BindingType getBinding() {
        return BindingType.SOAP;
    }

    @Override
    public TransportType getTransport() {
        return TransportType.HTTP;
    }

    @Override
    public boolean isLive() {
        return isLive;
    }

    @Override
    public SLProperties getProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getLastTimeStarted() {
        return lastTimeStarted;
    }

    @Override
    public long getLastTimeStopped() {
        // TODO Auto-generated method stub
        return 0;
    }

    @SuppressWarnings("unchecked")
    private EndpointDataType toEndPointData(byte[] content) throws ServiceLocatorException {
        
        ByteArrayInputStream is = new ByteArrayInputStream(content);
        try {
            JAXBContext jc = JAXBContext.newInstance("org.talend.esb.servicelocator.client.internal.endpoint");
            Unmarshaller um = jc.createUnmarshaller();
            
            JAXBElement<EndpointDataType> slEndpoint = (JAXBElement<EndpointDataType>) um.unmarshal(is);

            return slEndpoint.getValue();
        } catch( JAXBException e ){
            throw new ServiceLocatorException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private EndpointReferenceType toEndPointReference(Element root) throws ServiceLocatorException {

        try {
            JAXBContext jc = JAXBContext.newInstance("org.apache.cxf.ws.addressing");
            Unmarshaller um = jc.createUnmarshaller();
            
            JAXBElement<EndpointReferenceType> epr = (JAXBElement<EndpointReferenceType>) um.unmarshal(root);

            return epr.getValue();
        } catch( JAXBException e ){
            throw new ServiceLocatorException(e);
        }
    }
}
