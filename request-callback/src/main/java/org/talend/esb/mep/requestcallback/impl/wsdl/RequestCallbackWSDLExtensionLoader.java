package org.talend.esb.mep.requestcallback.impl.wsdl;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.xml.bind.JAXBException;

import org.apache.cxf.Bus;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.wsdl.JAXBExtensionHelper;
import org.apache.cxf.wsdl.WSDLExtensionLoader;
import org.apache.cxf.wsdl.WSDLManager;

@NoJSR250Annotations
public class RequestCallbackWSDLExtensionLoader implements WSDLExtensionLoader {

	private static final Class<?>[][] EXTENSORS = new Class[][] {
		{Binding.class, CallbackExtension.class},
		{Definition.class, PLType.class}
    };

    public RequestCallbackWSDLExtensionLoader(Bus bus) {
    	WSDLManager manager = bus.getExtension(WSDLManager.class);
        for (Class<?>[] ext : EXTENSORS) {
            addExtensions(bus, manager, ext[0], ext[1]);
        }
    }

    public void addExtensions(Bus bus, WSDLManager manager, Class<?> parentType, Class<?> elementType) {
        try {
            JAXBExtensionHelper.addExtensions(
                    bus,
            		manager.getExtensionRegistry(),
            		parentType, elementType, null,
                    getClass().getClassLoader());
        } catch (JAXBException e) {
            // ignore, won't support XML
        }
    }
}
