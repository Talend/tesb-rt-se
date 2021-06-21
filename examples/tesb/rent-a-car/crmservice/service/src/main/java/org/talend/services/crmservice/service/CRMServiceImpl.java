/*
 * #%L
 * CRMService Service
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.talend.services.crmservice.service;

import java.math.BigInteger;

import org.talend.services.crm.types.CRMStatusType;
import org.talend.services.crm.types.CustomerDetailsType;
import org.talend.services.crm.types.LoginUserType;
import org.talend.services.crm.types.ObjectFactory;
import org.talend.services.crm.types.RYLCStatusCodeType;
import org.talend.services.crm.types.RYLCStatusType;
import org.talend.services.crmservice.CRMService;

public class CRMServiceImpl implements CRMService {

	private ObjectFactory factory;

	// ####################################################
	// Constructors
	// ####################################################

	public CRMServiceImpl() {
		factory = new ObjectFactory();
	}

	// ####################################################
	// Public methods
	// ####################################################

	/**
	 * {@inheritDoc}
	 */
	public org.talend.services.crm.types.CustomerDetailsType getCRMInformation(
			org.talend.services.crm.types.LoginUserType login) {
		//
		logData("getCRMInformation", "request", login);
		// Load customer data
		CustomerDetailsType customer = getCustomerData(login);

		logData("getCRMInformation", "response", customer);
		return customer;
	}

	/**
	 * {@inheritDoc}
	 */
	public org.talend.services.crm.types.RYLCStatusType getCRMStatus(
			org.talend.services.crm.types.CustomerDetailsType customer) {
		//
		logData("getCRMStatus", "request", customer);
		// Get status
		RYLCStatusType status = getCustomerStatus(customer);

		logData("getCRMStatus", "response", status);
		return status;
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateCRMStatus(
			org.talend.services.crm.types.RYLCStatusType status) {
		//
		logData("updateCRMStatus", "request", status);
		//
		setCustomerStatus(status);
	}

	// ####################################################
	// Private methods
	// ####################################################

	private CustomerDetailsType getCustomerData(LoginUserType login) {
		// TODO Auto-generated method stub
		CustomerDetailsType customer = factory.createCustomerDetailsType();

		// Defaults
		customer.setId(0);
		customer.setGender("M");
		customer.setCity("Bonn");
		customer.setStreet("Straessensweg 10");
		customer.setZip("53113");
		customer.setEmail("info@talend.de");
		customer.setStatus(CRMStatusType.NONE);

		String user = login.getUsername();

		if (user != null) {
			if (user.contains("jdoe")) {
				customer.setId(92301);
				customer.setStatus(CRMStatusType.NORMAL);
				customer.setName("John Doe");
			} else if (user.contains("bbrindle")) {
				customer.setId(50010);
				customer.setStatus(CRMStatusType.GOLD);
				customer.setName("Bernardo Brindle");
			} else if (user.contains("rlambert")) {
				customer.setId(41250);
				customer.setStatus(CRMStatusType.PLATINUM);
				customer.setName("Ricardo Lambert");
			} else if (user.contains("aebert")) {
				customer.setId(45229);
				customer.setStatus(CRMStatusType.PLATINUM);
				customer.setName("Andrea Ebert");
				customer.setGender("F");
				customer.setCity("Munich");
				customer.setStreet("Hohenlindnerstrasse 11b");
				customer.setZip("85622");
				customer.setEmail("info@talend.de");
			}
			customer.setUsername(user);
		}
		// Overwrite Email & Name
		if((login.getEmail()!=null)&&(login.getEmail().length()>0)){
			customer.setEmail(login.getEmail());
		}
		if((login.getName()!=null)&&(login.getName().length()>0)){
			customer.setName(login.getName());
		}

		return customer;
	}

	private RYLCStatusType getCustomerStatus(CustomerDetailsType customer) {
		RYLCStatusType status = factory.createRYLCStatusType();

		// Defaults
		status.setCode(RYLCStatusCodeType.INACTIVE);
		status.setCreditPoints(new BigInteger("0"));
		status.setDescription("You are not registered.");
		status.setCustomerId(customer.getId());

		String user = customer.getUsername();

		if (user != null) {
			if (user.contains("jdoe")) {
				status.setCode(RYLCStatusCodeType.ACTIVE);
				status.setCreditPoints(new BigInteger("230"));
				status.setDescription("All your bookings are closed!");
			} else if (user.contains("bbrindle")) {
				status.setCode(RYLCStatusCodeType.ACTIVE);
				status.setCreditPoints(new BigInteger("1050"));
				status.setDescription("You can pay with your bonus points!");
			} else if (user.contains("rlambert")) {
				status.setCode(RYLCStatusCodeType.ACTIVE);
				status.setCreditPoints(new BigInteger("660"));
				status.setDescription("All your bookings are closed!");
			} else if (user.contains("aebert")) {
				status.setCode(RYLCStatusCodeType.ACTIVE);
				status.setCreditPoints(new BigInteger("790"));
				status.setDescription("Last reservation was canceled by reservation system!");
			}
			customer.setUsername(user);
		}
		return status;
	}

	private void setCustomerStatus(RYLCStatusType value) {
		//
		System.out.println("***** The cunstomer status is updated. *****");
	}

	private void logData(String operation, String option, Object data) {
		System.out.println("###############################################");
		System.out.println(operation + "() invoked ... " + option + " data:");
		System.out.println(data);
		System.out.println("###############################################");
	}

}
