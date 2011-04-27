package org.talend.esb.job.api.test;

import routines.system.api.TalendJob;

public class FakeTalendJob implements TalendJob {

	public String[][] runJob(String[] args) {
		System.out.println("TalendJob: Here it goes, the Fake Talend Job, it will take ages to execute");
		System.out.println("TalendJob: done!");
		return new String[0][0];
	}

	public int runJobInTOS(String[] args) {
		runJob(new String[0]);
		return 0;
	}

}
