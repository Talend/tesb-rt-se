package org.talend.esb.client.app;

public enum Stages {
	DATA_INPUT("input"),
	CAR_LIST("list"),
	RESERVATION_INFO("info");
	
	private final String view;
	
	private Stages(String view) {
		this.view = view;
	}
	
	public String getView() {
		return view;
	}
}
