package org.talend.esb.sam.server.persistence;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AggregatedFlow {

    private String flowID;

    private long timestamp;

    private long elapsed;

    Set<String> types;


    private String port;

    private String operation;

    private String transport;


    private String consumerHost;

    private String consumerIP;

    private String providerHost;

    private String providerIP;


    URI details;


    public String getFlowID() {
        return flowID;
    }

    public void setFlowID(String flowID) {
        this.flowID = flowID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getElapsed() {
        return elapsed;
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    public Set<String> getTypes() {
        return types;
    }

    public void setTypes(Set<String> types) {
        this.types = types;
    }


    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }


    public String getConsumerHost() {
        return consumerHost;
    }

    public void setConsumerHost(String consumerHost) {
        this.consumerHost = consumerHost;
    }

    public String getConsumerIP() {
        return consumerIP;
    }

    public void setConsumerIP(String consumerIP) {
        this.consumerIP = consumerIP;
    }

    public String getProviderHost() {
        return providerHost;
    }

    public void setProviderHost(String providerHost) {
        this.providerHost = providerHost;
    }

    public String getProviderIP() {
        return providerIP;
    }

    public void setProviderIP(String providerIP) {
        this.providerIP = providerIP;
    }


    public URI getDetails() {
        return details;
    }

    public void setDetails(URI details) {
        this.details = details;
    }

}
