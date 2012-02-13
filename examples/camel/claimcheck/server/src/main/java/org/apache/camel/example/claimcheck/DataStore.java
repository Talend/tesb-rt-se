package org.apache.camel.example.claimcheck;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Allows to store (check in) and retrieve (claim) data
 * 
 * This implementation is especially simple. A more realistiv implementation could use and ftp store or a service
 */
public class DataStore {
    Map<String, byte[]> dataMap = new HashMap<String, byte[]>();
    
    /**
     * Store the given data and return a uuid for later retrieval of the data
     * 
     * @param data
     * @return unique id for the stored data
     */
    public String checkIn(byte[] data) {
        String id = UUID.randomUUID().toString();
        dataMap.put(id, data);
        return id;
    }
    
    /**
     * Retrieve the data referenced by the given id and remove it from the data store
     * 
     * @param id
     * @return retrieved data
     */
    public byte[] claim(String id) {
        return dataMap.remove(id);
    }
}
