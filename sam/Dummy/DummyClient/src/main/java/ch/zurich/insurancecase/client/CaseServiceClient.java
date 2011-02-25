package ch.zurich.insurancecase.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.ws.Holder;

import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.sopera.monitoring.feature.EventFeature;

import ch.zurich.incurancecase.caseservice.AddCaseFault_Exception;
import ch.zurich.incurancecase.caseservice.CasePort;

public class CaseServiceClient {

	private static int limit = 1;
	private static long delay = 20;
	private static List<Boolean> finished = new ArrayList<Boolean>();
	private static Date start;

	public static void main(String[] args) throws InterruptedException {
		List<Thread> runs = new ArrayList<Thread>();

		// Send without feature
		// CaseService service = new CaseService();
		// CasePort port = service.getCaseServiceSOAP();
		// for (int i = 0; i < 1; i++) {
		// send(port, customerId + i, description, amountClaimed, guiltyParty);
		// }

		// An alternate way to get the SOAP service interface; includes logging
		// interceptors.

		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.getFeatures().add(new EventFeature());
		factory.getFeatures().add(new LoggingFeature());
		factory.setServiceClass(CasePort.class);
		factory.setAddress("http://localhost:8080/DummyService/services/CaseServiceSOAP");
		final CasePort port = (CasePort) factory.create();

		for (int i = 0; i < limit; i++) {
			runs.add(new Thread() {

				public void run() {
					CaseServiceClient client = new CaseServiceClient();
					client.runRequest(port);
				}
			});
		}

		start = new Date();

		for (int i = 0; i < limit; i++) {
			System.out.println("Start " + i);
			runs.get(i).start();
			Thread.sleep(delay);
		}
	}

	private void runRequest(CasePort port) {
		String customerId = "fault";
		String description = "desc";
		String amountClaimed = "amountClaimed";
		String guiltyParty = "contractor";

		try {
			send(port, customerId, description, amountClaimed, guiltyParty);
			finished.add(Boolean.TRUE);
		} catch (Exception e) {
			finished.add(Boolean.FALSE);
		}

		checkFinished();
	}

	private void send(CasePort port, String customerId, String description,
			String amountClaimed, String guiltyParty) {

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("\n------------------------------\n");
		stringBuilder.append("Send (CLIENT)\n");
		stringBuilder.append("------------------------------\n");
		stringBuilder.append("Customer Id: " + customerId + "\n");
		stringBuilder.append("Description: " + description + "\n");
		stringBuilder.append("Claimed    : " + amountClaimed + "\n");
		stringBuilder.append("Guilty     : " + guiltyParty + "\n");
		stringBuilder.append("------------------------------\n");

		System.out.println(stringBuilder.toString());

		Holder<String> caseId = new Holder<String>();
		Holder<Boolean> accepted = new Holder<Boolean>();
		try {
			port.addCase(customerId, description, amountClaimed, guiltyParty,
					null, caseId, accepted);
		} catch (AddCaseFault_Exception e) {
			e.printStackTrace();
		}

		stringBuilder = new StringBuilder();

		stringBuilder.append("------------------------------\n");
		stringBuilder.append("Response (CLIENT)\n");
		stringBuilder.append("------------------------------\n");
		stringBuilder.append("Case Id    : " + caseId.value.toString() + "\n");
		stringBuilder
				.append("Accepted   : " + accepted.value.toString() + "\n");
		stringBuilder.append("------------------------------\n");

		stringBuilder = new StringBuilder();
	}

	private void checkFinished() {
		if (finished.size() == limit) {
			Date end = new Date();
			int success = 0;
			int error = 0;
			for (int i = 0; i < limit; i++) {
				if (finished.get(i).equals(Boolean.TRUE))
					success++;
				else
					error++;
			}
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Duration: "
					+ (end.getTime() - start.getTime()) + "\n");
			stringBuilder.append("TRUE : " + success + "\n");
			stringBuilder.append("FALSE: " + error);
			System.out.println(stringBuilder.toString());
			return;
		}
	}
}
