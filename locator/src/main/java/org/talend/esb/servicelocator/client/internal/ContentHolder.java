package org.talend.esb.servicelocator.client.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.ws.addressing.MetadataType;
import org.talend.esb.servicelocator.client.SLProperties;
import org.talend.esb.servicelocator.client.SLPropertiesImpl;
import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.internal.endpoint.EndpointDataType;
import org.talend.esb.servicelocator.client.internal.endpoint.ObjectFactory;
import org.talend.esb.servicelocator.client.internal.endpoint.ServiceLocatorPropertiesType;
import org.talend.esb.servicelocator.cxf.internal.SLPropertiesConverter;
import org.w3c.dom.Element;

public class ContentHolder {
    private static final Logger LOG = Logger.getLogger(ContentHolder.class
            .getName());

    protected EndpointDataType epd;
    
    protected EndpointReferenceType epr;
    
    private SLProperties props;

    public ContentHolder(EndpointDataType endpointData) {
        epd = endpointData;
    }
    
    public ContentHolder(byte[] content) throws ServiceLocatorException {
        if (content != null) {
            epd = toEndPointData(content);
            
            Element eprRoot = (Element) epd.getAny();
            epr =  toEndPointReference(eprRoot);

            props = extractProperties(epr);
        } else {
            throw new IllegalArgumentException("content must not be null.");
        }
    }

    public byte[] getContent() throws ServiceLocatorException {
        return serialize(epd);
    }

    public long getLastTimeStarted() {
        return epd.getLastTimeStarted();
    }

    public long getLastTimeStopped() {
        return  epd.getLastTimeStopped();       
    }

    public void  setLastTimeStopped(long lastTimeStopped) {
        epd.setLastTimeStopped(lastTimeStopped);       
    }

    public String getAddress() {
        return epr.getAddress().getValue();
    }

    public SLProperties getProperties() {
        return props;
    }

    private static byte[] serialize(EndpointDataType endpointData) throws ServiceLocatorException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(50000);
        try {
            ObjectFactory of = new ObjectFactory();

            JAXBElement<EndpointDataType> epd =
                of.createEndpointData(endpointData);
            JAXBContext jc = JAXBContext.newInstance("org.talend.esb.servicelocator.client.internal.endpoint");
            Marshaller m = jc.createMarshaller();
            m.marshal(epd, outputStream);
        } catch( JAXBException e ){
            if (LOG.isLoggable(Level.SEVERE)) {
                LOG.log(Level.SEVERE,
                        "Failed to serialize endpoint data", e);
            }
            throw new ServiceLocatorException("Failed to serialize endpoint data", e);

        }
        return outputStream.toByteArray();
    }

    @SuppressWarnings("unchecked")
    private static EndpointDataType toEndPointData(byte[] content) throws ServiceLocatorException {
        
        ByteArrayInputStream is = new ByteArrayInputStream(content);
        try {
            JAXBContext jc = JAXBContext.newInstance("org.talend.esb.servicelocator.client.internal.endpoint");
            Unmarshaller um = jc.createUnmarshaller();
            
            JAXBElement<EndpointDataType> slEndpoint = (JAXBElement<EndpointDataType>) um.unmarshal(is);

            return slEndpoint.getValue();
        } catch( JAXBException e ){
            if (LOG.isLoggable(Level.SEVERE)) {
                LOG.log(Level.SEVERE,
                        "Failed to deserialize endpoint data", e);
            }
            throw new ServiceLocatorException("Failed to deserialize endpoint data", e);
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

    private SLProperties extractProperties(EndpointReferenceType epr) throws ServiceLocatorException {
        MetadataType metadata = epr.getMetadata();
        if (metadata != null) {
            List<Object> metaAny = metadata.getAny();
            for(Object any : metaAny) {
                if (any instanceof Element) {
                    Element root = (Element) any;
                    if (root.getLocalName().equals("ServiceLocatorProperties")) {
                        ServiceLocatorPropertiesType slp = toServiceLocatorProperties(root); 
                        return SLPropertiesConverter.toSLProperties(slp);
                    }
                }
            }
        }
        return new SLPropertiesImpl();
    }

    @SuppressWarnings("unchecked")
    private ServiceLocatorPropertiesType toServiceLocatorProperties(Element root) throws ServiceLocatorException {

        try {
            JAXBContext jc = JAXBContext.newInstance("org.talend.esb.servicelocator.client.internal.endpoint");
            Unmarshaller um = jc.createUnmarshaller();
            
            JAXBElement<ServiceLocatorPropertiesType> slp = (JAXBElement<ServiceLocatorPropertiesType>) um.unmarshal(root);

            return slp.getValue();
        } catch( JAXBException e ){
            throw new ServiceLocatorException(e);
        }
    }
}
