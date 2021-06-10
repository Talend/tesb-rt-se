package org.talend.esb.client.app.model;

import jakarta.validation.constraints.NotBlank;

import org.talend.esb.client.app.validation.DateNotInPast;
import org.talend.esb.client.app.validation.DatesConsistent;
import org.talend.esb.client.app.validation.ValidDateFormat;

@DatesConsistent
public class SearchRequestParameters {

	@NotBlank
	private String customerName;

	@NotBlank
	@ValidDateFormat
	@DateNotInPast
	private String pickupDate;

	@NotBlank
	@ValidDateFormat
	@DateNotInPast
	private String returnDate;

	public SearchRequestParameters() {
	}

	public SearchRequestParameters(SearchRequestParameters orig) {
		customerName = orig.customerName;
		pickupDate = orig.pickupDate;
		returnDate = orig.returnDate;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getPickupDate() {
		return pickupDate;
	}

	public void setPickupDate(String pickupDate) {
		this.pickupDate = pickupDate;
	}

	public String getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}
}
