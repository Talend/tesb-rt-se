package org.talend.esb.job.api.test;

import org.dom4j.DocumentException;

import routines.system.api.ESBConsumer;
import routines.system.api.ESBEndpointInfo;
import routines.system.api.ESBEndpointRegistry;
import routines.system.api.ESBJobInterruptedException;
import routines.system.api.ESBProviderCallback;
import routines.system.api.TalendESBJob;
import routines.system.api.TalendJob;

public class Launcher {

	public static void main(String[] args) {
		// Instantiate a Job
		TalendJob job = new TestProviderJob();
		TalendJob job1 = new TestConsumerJob();
		TalendJob job2 = new FakeTalendJob();
		Launcher launcher = new Launcher();
		launcher.run(job2);
		System.out.println();
		System.out.println("==================================================");
		System.out.println();
		launcher.run(job1);
		System.out.println();
		System.out.println("==================================================");
		System.out.println();
		launcher.run(job);
	}

	public Launcher() {};

	public int run(TalendJob job) {
		if (job instanceof TalendESBJob) {
			// We have an ESB Job;
			runTalendESBJob((TalendESBJob) job);
		} else {
			// Run job as usual
			runTalendJob(job);
		}
		return 0;
	}

	private void runTalendJob(TalendJob job) {
		// Just start it
		job.runJob(new String[0]);
	}

	private void runTalendESBJob(TalendESBJob job) {
		// init consumer communication part - set endpoint registry
		// (will be used by ESB job containing consumer components)
		job.setEndpointRegistry(new ESBEndpointRegistry() {

			// all below is TestConsumerJob specific implementation
			public ESBConsumer createConsumer(final ESBEndpointInfo endpoint) {
				System.out.println("ESB [consumer]: Creating a consumer to communicate with service " + endpoint.getEndpointProperties().get("wsdlURL"));
				System.out.println("ESB [consumer]: consumer endpoint info - key = " + endpoint.getEndpointKey());
				System.out.println("ESB [consumer]: consumer endpoint info - uri = " + endpoint.getEndpointUri());
				System.out.println("ESB [consumer]: consumer endpoint info - properties = " + endpoint.getEndpointProperties());
				return new ESBConsumer() {
					public Object invoke(Object payload) throws Exception {
						// System.out.println("ESB: Job sent message " + ((org.dom4j.Document) payload).asXML());
						System.out.println("ESB [consumer]: Job sent message to " + endpoint.getEndpointProperties().get("wsdlURL"));
						try {Thread.sleep(1000);} catch (InterruptedException e) {}
						return getDocument("<GetWeatherResponse xmlns='http://litwinconsulting.com/webservices/'><GetWeatherResult>Sunny</GetWeatherResult></GetWeatherResponse>");
					}
				};
			}
		});

		String jobName = job.getClass().getSimpleName();

		// get provider end point information
		ESBEndpointInfo endpoint = job.getEndpoint();
		if (null == endpoint) {
			System.out.println("Launcher: ESB job [" + jobName + "] is NOT provider job");
			// job contains only consumer components
			// i.e. don't expose itself as web service provider
			// start job immediately
			job.runJobInTOS(new String[0]);
		} else {
			System.out.println("Launcher: ESB job [" + jobName + "] is provider job");
			System.out.println("ESB [provider]: provider endpoint info - key = " + endpoint.getEndpointKey());
			System.out.println("ESB [provider]: provider endpoint info - uri = " + endpoint.getEndpointUri());
			System.out.println("ESB [provider]: provider endpoint info - properties = " + endpoint.getEndpointProperties());

			// init provider communication part - set provider callback
			job.setProviderCallback(new ESBProviderCallback() {
				// all below is TestProviderJob specific implementation
				int count = 0;

				public Object getRequest() throws ESBJobInterruptedException {
					System.out.println("ESB [provider]: Job want to process next request...");
					try {Thread.sleep(1000);} catch (InterruptedException e) {}

					count++;
					System.out.println();
					System.out.println("--------------------------------------------------");
					if (1 == count) {
						System.out.println("ESB [provider]: Giving a request to Job...");
						return getDocument("<jobInput xmlns='http://talend.org/esb/service/job'>world</jobInput>");
					}

					if (2 == count) {
						System.out.println("ESB [provider]: Giving a request to Job...");
						return getDocument("<jobInput xmlns='http://talend.org/esb/service/job'></jobInput>");
					}

					if (3 == count) {
						System.out.println("ESB [provider]: Giving a request to Job...");
						return getDocument("<jobInput xmlns='http://talend.org/esb/service/job'>xxx</jobInput>");
					}

					System.out.println("ESB [provider]: Now I want to stop the Job...");
					throw new ESBJobInterruptedException("Stop processing");
				}

				public void sendResponse(Object response) {
					System.out.println("ESB [provider]: Have got a response from the Job, sending it...");
					if (response instanceof Exception) {
						System.out.println("\t fault: " + ((Throwable) response).getMessage());
						((Throwable) response).printStackTrace();
					} else if (response instanceof TestProviderJob.ProviderFault) {
						TestProviderJob.ProviderFault businessFault =
							(TestProviderJob.ProviderFault) response;
						System.out.println("\t business fault: " + businessFault.getMessage());

						org.dom4j.Document faultDetail = (org.dom4j.Document) businessFault.getDetail();
						if (null != faultDetail) {
							System.out.println("\t business fault detail: " + faultDetail.asXML());
						}
					} else {
						System.out.println("\t payload: " + ((org.dom4j.Document) response).asXML());
					}

					try {Thread.sleep(1000);} catch (InterruptedException e) {}
				}
			});

			System.out.println("Launcher: opening endpoint");
			// open ESB endpoint (by provided endpoint info from job)
			System.out.println("Launcher: wait for request");
			// job will be executed only after first request received by ESB endpoint
			try {Thread.sleep(1000);} catch (InterruptedException e) {}
			System.out.println("Launcher: got first request");
			System.out.println("Launcher: launching ESB Job");
			job.runJobInTOS(new String[0]);
		}
	}

	private org.dom4j.Document getDocument(String xml) {
		try {
			return org.dom4j.DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
	}

}
