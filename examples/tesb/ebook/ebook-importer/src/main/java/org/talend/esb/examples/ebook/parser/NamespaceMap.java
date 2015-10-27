package org.talend.esb.examples.ebook.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.NamespaceContext;

public class NamespaceMap implements NamespaceContext {
    private Map<String, String> namespaces;

    public NamespaceMap() {
        namespaces = new HashMap<String, String>();
    }

    public void add(String prefix, String namespaceURI) {
        namespaces.put(prefix, namespaceURI);
    }

    public String[] getDeclaredPrefixes() {
        Set<String> keys = namespaces.keySet();
        return (String[])keys.toArray(new String[keys.size()]);
    }
    
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("The prefix must not be null.");
        }
        return (String)namespaces.get(prefix); 
    }    

    public String getPrefix(String namespaceURI) {
        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            if (entry.getValue().toString().equals(namespaceURI)) {
                return (String)entry.getKey();
            }
        }
        return null;
    }

    public Iterator<String> getPrefixes(String namespaceURI) {
        List<String> list = new ArrayList<String>();
        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            if (entry.getValue().toString().equals(namespaceURI)) {
                list.add(entry.getKey());
            }
        }
        return list.iterator();
    }
}
