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
