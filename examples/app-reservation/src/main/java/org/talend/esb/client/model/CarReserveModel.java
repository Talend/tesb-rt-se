package org.talend.esb.client.model;

import org.talend.services.crm.types.CustomerDetailsType;
import org.talend.services.reservation.types.ConfirmationType;
import org.talend.services.reservation.types.RESCarType;
import org.talend.services.reservation.types.RESStatusType;

public interface CarReserveModel {
	public RESStatusType reserveCar(CustomerDetailsType customer, RESCarType car, String pickupDate, String returnDate);

	public ConfirmationType getConfirmation(RESStatusType reservationState, CustomerDetailsType customer,
			RESCarType car, String pickupDate, String returnDate);
}
