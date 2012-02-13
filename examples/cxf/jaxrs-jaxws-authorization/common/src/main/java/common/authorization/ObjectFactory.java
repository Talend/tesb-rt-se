/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */

package common.authorization;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

    private final static QName _UserImpl_QNAME = new QName("http://hello.com", "User");
    private final static QName _IntegerUserMap_QNAME = new QName("http://hello.com", "IntegerUserMap");
    
    /**
     * Create a new ObjectFactory that can be used to create new instances of
     * schema derived classes for package: common
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UserImpl }
     */
    public UserImpl createUserImpl() {
        return new UserImpl();
    }
    
    /**
     * Create an instance of {@link IntegerUserMap }
     */
    public IntegerUserMap createIntegerUserMap() {
        return new IntegerUserMap();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}
     * {@link UserImpl }{@code >}
     */
    @XmlElementDecl(namespace = "http://hello.com", name = "User")
    public JAXBElement<UserImpl> createUserImpl(UserImpl value) {
        return new JAXBElement<UserImpl>(_UserImpl_QNAME, UserImpl.class, null, value);
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}
     * {@link IntegerUserMap }{@code >}
     */
    @XmlElementDecl(namespace = "http://hello.com", name = "IntegerUserMap")
    public JAXBElement<IntegerUserMap> createIntegerUserMap(IntegerUserMap value) {
        return new JAXBElement<IntegerUserMap>(_IntegerUserMap_QNAME, IntegerUserMap.class, null, value);
    }

}
