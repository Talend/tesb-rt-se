package org.talend.esb.sam.service;

import java.net.URL;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.talend.esb.sam.common.event.EventTypeEnum;

@XmlRootElement
public class AggregatedFlowEvent {

    private long id;

    private long timestamp;

    private EventTypeEnum type;


    private String customId;

    private String process;

    private String host;

    private String ip;

    private String principal;


    private String flowID;

    private String messageID;

    private String transport;

    private String port;

    private String operation;


    private boolean isContentCut;

    private Set<CustomInfo> customInfo;

    private URL details;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public EventTypeEnum getType() {
        return type;
    }

    public void setType(EventTypeEnum type) {
        this.type = type;
    }


    public String getCustomId() {
        return customId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String hostname) {
        this.host = hostname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }


    public String getFlowID() {
        return flowID;
    }

    public void setFlowID(String flowID) {
        this.flowID = flowID;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
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


    public boolean isContentCut() {
        return isContentCut;
    }

    public void setContentCut(boolean isContentCut) {
        this.isContentCut = isContentCut;
    }

    public Set<CustomInfo> getCustomInfo() {
        return customInfo;
    }

    public void setCustomInfo(Set<CustomInfo> custom) {
        this.customInfo = custom;
    }

    public URL getDetails() {
        return details;
    }

    public void setDetails(URL details) {
        this.details = details;
    }

}
