package org.talend.esb.sam.server.persistence;

import org.talend.esb.sam.common.event.EventTypeEnum;

public class Flow {

    private String flowID;

    private long timestamp;

    private EventTypeEnum eventType;


    private String host;

    private String ip;


    private String port;

    private String operation;

    private String transport;


    public String getflowID() {
        return flowID;
    }

    public void setflowID(String flowID) {
        this.flowID = flowID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public EventTypeEnum getEventType() {
        return eventType;
    }

    public void setEventType(EventTypeEnum eventType) {
        this.eventType = eventType;
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

}
