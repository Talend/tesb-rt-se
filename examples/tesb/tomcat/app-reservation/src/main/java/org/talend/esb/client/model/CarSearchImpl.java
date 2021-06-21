/*
 * #%L
 * App Reservation Basic
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

package org.talend.esb.client.model;

import java.util.Collections;
import java.util.List;

import org.talend.services.crm.types.CustomerDetailsType;
import org.talend.services.crm.types.LoginUserType;
import org.talend.services.crmservice.CRMService;
import org.talend.services.reservation.types.RESCarListType;
import org.talend.services.reservation.types.RESCarType;
import org.talend.services.reservation.types.RESProfileType;
import org.talend.services.reservationservice.ReservationService;

public class CarSearchImpl implements CarSearchModel {
	private CRMService crms;
	private ReservationService reserve;
	private CustomerDetailsType customer;
	private RESCarListType cars;

	public void setCrms(CRMService crms) {
		this.crms = crms;
	}

	public void setReserve(ReservationService reserve) {
		this.reserve = reserve;
	}

	public int search(String userName, String pickupDate, String returnDate) {
		LoginUserType loginUser = new LoginUserType();
		loginUser.setUsername(userName);
		customer = crms.getCRMInformation(loginUser);

		RESProfileType reservationProfile = new RESProfileType();
		reservationProfile.setFromDate(pickupDate);
		reservationProfile.setToDate(returnDate);
		reservationProfile.setCrmStatus(customer.getStatus());
		cars = reserve.getAvailableCars(reservationProfile);
		return cars.getCar().size();
	}

	public CustomerDetailsType getCustomer() {
		return customer;
	}

	public List<RESCarType> getCars() {
		return Collections.unmodifiableList(cars.getCar());
	}

}
