package org.sopera.monitoring.event;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class Originator implements Serializable {

	@Transient
	private static final long serialVersionUID = 3926684116318585338L;

	@Basic(optional = false)
	@Column(name = "ORIG_PROCESS_ID")
	private String processId;
	@Basic(optional = false)
	@Column(name = "ORIG_IP", length=64)
	private String ip;
	@Basic(optional = false)
	@Column(name = "ORIG_HOSTENAME", length=128)
	private String hostname;
	@Column(name = "ORIG_CUSTOM_ID")
	private String customId;

	public Originator(String processId, String ip, String hostname,
			String customId) {
		super();
		this.processId = processId;
		this.ip = ip;
		this.hostname = hostname;
		this.customId = customId;
	}

	public Originator() {
		super();
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}
}
