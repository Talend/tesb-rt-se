package org.talend.esb.servicelocator.client.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.talend.esb.servicelocator.client.ServiceLocatorException;
import org.talend.esb.servicelocator.client.internal.endpoint.EndpointDataType;
import org.talend.esb.servicelocator.client.internal.endpoint.ObjectFactory;

public class ContentHolder {
    private static final Logger LOG = Logger.getLogger(ContentHolder.class
            .getName());

    private EndpointDataType epd;

    public ContentHolder(EndpointDataType endpointData) {
        epd = endpointData;
    }
    
    public ContentHolder(byte[] content) throws ServiceLocatorException {
        epd = toEndPointData(content);
    }

    public EndpointDataType getEndpointData() {
        return epd;        
    }

    public byte[] getContent() throws ServiceLocatorException {
        return serialize(epd);
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

}
