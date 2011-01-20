/*******************************************************************************
 * Copyright (c) 2010 SOPERA GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     SOPERA GmbH - initial API and implementation
 *******************************************************************************/
package org.talend.esb.client.model;

import org.sopera.services.crm.types.CustomerDetailsType;
import org.sopera.services.reservation.types.ConfirmationType;
import org.sopera.services.reservation.types.RESCarType;
import org.sopera.services.reservation.types.RESStatusType;
import org.sopera.services.reservation.types.ReservationType;
import org.sopera.services.reservationservice.ReservationService;

public class CarReserveImpl implements CarReserveModel {
	private ReservationService reserve;

	public void setReserve(ReservationService reserve) {
		this.reserve = reserve;
	}

	public RESStatusType reserveCar(CustomerDetailsType customer, RESCarType car, String pickupDate, String returnDate) {
		return reserve.submitCarReservation(createReservation(customer, car, pickupDate, returnDate));
	}

	public ConfirmationType getConfirmation(RESStatusType resStatus, CustomerDetailsType customer, RESCarType car,
			String pickupDate, String returnDate) {
		ReservationType reservation = createReservation(customer, car, pickupDate, returnDate);
		reservation.setReservationId(resStatus.getId());
		return reserve.getConfirmationOfReservation(reservation);
	}

	private ReservationType createReservation(CustomerDetailsType customer, RESCarType car, String pickupDate,
			String returnDate) {
		ReservationType res = new ReservationType();
		res.setCustomer(customer);
		res.setCar(car);
		res.setFromDate(pickupDate);
		res.setToDate(returnDate);
		return res;
	}
}
