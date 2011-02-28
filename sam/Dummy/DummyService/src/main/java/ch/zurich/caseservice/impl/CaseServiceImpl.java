package ch.zurich.caseservice.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;

import ch.zurich.incurancecase.caseservice.AddCaseFault;
import ch.zurich.incurancecase.caseservice.AddCaseFault_Exception;
import ch.zurich.incurancecase.caseservice.CasePort;

import com.example.customerservice.Customer;
import com.example.customerservice.CustomerService;
import com.example.customerservice.NoSuchCustomerException;

public class CaseServiceImpl implements CasePort {

	private long dummyDelay;

	private CustomerService customerService;
	
	public void setCustomerService(CustomerService customerService) {this.customerService = customerService; }
	
	public CustomerService getCustomerService() { return this.customerService; }

	private static Logger logger = Logger.getLogger(CaseServiceImpl.class
			.getName());

	public CaseServiceImpl() {
		logger.info("CaseServiceImpl created.");
	}

	public void addCase(String customerId, String description,
			String amountClaimed, String guiltyParty, String password,
			Holder<String> caseId, Holder<Boolean> accepted)
			throws AddCaseFault_Exception {
		Date startDate = new Date();
		
		List<Customer> customers = null;
		if (description.equalsIgnoreCase("delegate")) {
			System.out.println("Calling customer service...");
						
			try {
            			customers = customerService.getCustomersByName(customerId);
            			System.out.println("Customer found: " + (customers.size()>0));

			} catch (NoSuchCustomerException e) {
				System.out.println(e.getMessage());
				System.out.println("NoSuchCustomer exception was received as expected");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			System.out.println("Customer service called");
		}

		UUID uuid = UUID.randomUUID();
		long randomLong = (Math.round(Math.random() * 100));
		boolean random = (randomLong % 2) == 0;

		caseId.value = uuid.toString();
		accepted.value = random;

		/*
		 * if (wsContext != null && !wsContext.getMessageContext().containsKey(
		 * CommonConstants.FLOW_ID)) {
		 * wsContext.getMessageContext().put(CommonConstants.FLOW_ID, uuid);
		 * System.out.println("Set flowId"); }
		 */

		if (customerId.equals("fault")) {
			AddCaseFault fault = new AddCaseFault();
			fault.setAddCaseFault("FaultValue");
			throw new AddCaseFault_Exception("Fault", fault);
		}

		try {
			Thread.sleep(dummyDelay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		StringBuilder builder = new StringBuilder();
		builder.append("------------------------------\n");
		builder.append("Case added\n");
		builder.append("------------------------------\n");

		builder.append("Customer Id: ");
		builder.append(customerId);
		builder.append("\n");

		builder.append("Description: ");
		builder.append(description);
		builder.append("\n");

		builder.append("Claimed    : ");
		builder.append(amountClaimed);
		builder.append("\n");

		builder.append("Guilty     : ");
		builder.append(guiltyParty);
		builder.append("\n");

		builder.append("Password   : ");
		builder.append(password != null ? "*****\n" : "no password\n");

		builder.append("------------------------------\n");
		builder.append("Response");
		builder.append("------------------------------\n");

		builder.append("Case Id    : ");
		builder.append(uuid.toString());
		builder.append("\n");

		builder.append("Accepted   : ");
		builder.append(random ? "YES\n" : "NO\n");

		Date endDate = new Date();
		builder.append("Service execution time msec: ");
		builder.append(endDate.getTime() - startDate.getTime());
		builder.append("\n");

		builder.append("------------------------------\n");

		System.out.println(builder.toString());
	}

	public String ping(String in) {
		return in + "_pong";
	}

	public long getDummyDelay() {
		return dummyDelay;
	}

	public void setDummyDelay(long dummyDelay) {
		this.dummyDelay = dummyDelay;
	}
}
