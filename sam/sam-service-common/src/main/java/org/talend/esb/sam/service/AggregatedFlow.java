package org.talend.esb.sam.service;

import java.net.URL;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AggregatedFlow {

    private String flowID;

    private long timeStamp;

    private String port;

    private String operation;

    private String transport;

    private long elapsed;

    Set<String> types;

    URL details;

    private String consumerHost;

    private String consumerIP;

    private String providerHost;

    private String providerIP;

    public void setflowID(String flowID) {
        this.flowID = flowID;
    }

    public String getflowID() {
        return flowID;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPort() {
        return port;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    public long getElapsed() {
        return elapsed;
    }

    public void setTypes(Set<String> types) {
        this.types = types;
    }

    public Set<String> getTypes() {
        return types;
    }

    public void setDetails(URL details) {
        this.details = details;
    }

    public URL getDetails() {
        return details;
    }

    public void setConsumerHost(String consumerHost) {
        this.consumerHost = consumerHost;
    }

    public String getConsumerHost() {
        return consumerHost;
    }

    public void setConsumerIP(String consumerIP) {
        this.consumerIP = consumerIP;
    }

    public String getConsumerIP() {
        return consumerIP;
    }

    public void setProviderHost(String providerHost) {
        this.providerHost = providerHost;
    }

    public String getProviderHost() {
        return providerHost;
    }

    public void setProviderIP(String providerIP) {
        this.providerIP = providerIP;
    }

    public String getProviderIP() {
        return providerIP;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getTransport() {
        return transport;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
