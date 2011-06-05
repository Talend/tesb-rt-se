package org.talend.esb.servicelocator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

public class NamespaceContextImpl implements NamespaceContext {

    public static String SL_NS = "http://talend.org/esb/serviceLocator/4.2";

    public static String SL_PREFIX = "sl";

    public static String WSA_NS = "http://www.w3.org/2005/08/addressing";

    public static String WSA_PREFIX = "wsa";

    public static final NamespaceContext SL_NS_CONTEXT =
        new NamespaceContextImpl(SL_PREFIX , SL_NS);

    public static final NamespaceContext WSA_NS_CONTEXT =
        new NamespaceContextImpl(WSA_PREFIX , WSA_NS);

    public static final NamespaceContext WSA_SL_NS_CONTEXT =
        new NamespaceContextImpl(WSA_PREFIX , WSA_NS).add(SL_PREFIX, SL_NS);

    private Map<String, String> prefixMap = new HashMap<String, String>();

    private Map<String, List<String>> namespaceURIMap = new HashMap<String, List<String>>();

    public NamespaceContextImpl(String prefix, String namespaceURI) {
        prefixMap.put(prefix, namespaceURI);
        List<String> prefixes = new ArrayList<String>();
        prefixes.add(prefix);
        namespaceURIMap.put(namespaceURI, prefixes);
    }
    
    public NamespaceContextImpl add(String prefix, String namespaceURI) {
        prefixMap.put(prefix, namespaceURI);
        
        
        List<String> prefixes = namespaceURIMap.get(namespaceURI);
        if (prefixes == null) {
            prefixes = new ArrayList<String>();
            namespaceURIMap.put(namespaceURI, prefixes);
        }

        prefixes.add(prefix);
        return this;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return prefixMap.get(prefix);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        List<String> prefixes = namespaceURIMap.get(namespaceURI);
        if (prefixes != null) {
            return prefixes.get(0);
        } else {
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Iterator getPrefixes(String namespaceURI) {
        List<String> prefixes = namespaceURIMap.get(namespaceURI);
        if (prefixes != null) {
            return prefixes.iterator();
        } else {
            return Collections.emptyList().iterator();
        }
    }
}