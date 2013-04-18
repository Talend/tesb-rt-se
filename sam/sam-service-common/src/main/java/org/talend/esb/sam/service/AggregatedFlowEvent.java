package org.talend.esb.sam.service;

import java.net.URL;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.talend.esb.sam.common.event.EventTypeEnum;

@XmlRootElement
public class AggregatedFlowEvent {

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

    public String getFlowID() {
        return flowID;
    }

    public void setFlowID(String flowID) {
        this.flowID = flowID;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public boolean isContentCut() {
        return isContentCut;
    }

    public void setContentCut(boolean isContentCut) {
        this.isContentCut = isContentCut;
    }

    public URL getDetails() {
        return details;
    }

    public void setDetails(URL details) {
        this.details = details;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public Map<String, String> getCustomInfo() {
        return customInfo;
    }

    public void setCustomInfo(Map<String, String> customInfo) {
        this.customInfo = customInfo;
    }

    private long id;

    private long timestamp;

    private EventTypeEnum type;

    private String customId;

    private String process;

    private String host;

    private String ip;

    private String principal;

    private String port;

    private String operation;

    private String flowID;

    private String transport;

    private boolean isContentCut;

    private Map<String, String> customInfo;

    private String messageID;

    private URL details;
}
