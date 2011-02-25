package ch.zurich.monitoring.event;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity(name = "ZurichEvent")
@Table(name = "ZURICHEVENTS")
public class ZurichEvent extends org.sopera.monitoring.event.Event {

	private static final long serialVersionUID = 1385876025514924989L;

	@Column(name = "STAGE", length = 4)
	private String stage;

	@Version
	@Column(name = "CREATED")
	private Timestamp created;

	@Column(name = "STATUS", length = 16)
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}


}
