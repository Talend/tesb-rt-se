package org.talend.esb.client.model;

import java.util.List;

import org.talend.services.reservation.types.RESCarType;
import org.talend.services.crm.types.CustomerDetailsType;

public interface CarSearchModel {
	public int search(String userName, String pickupDate, String returnDate);

	public CustomerDetailsType getCustomer();

	public List<RESCarType> getCars();
}
