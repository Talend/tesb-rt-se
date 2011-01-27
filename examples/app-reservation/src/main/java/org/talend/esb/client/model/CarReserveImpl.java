package org.talend.esb.client.model;

import org.talend.services.crm.types.CustomerDetailsType;
import org.talend.services.reservation.types.ConfirmationType;
import org.talend.services.reservation.types.RESCarType;
import org.talend.services.reservation.types.RESStatusType;
import org.talend.services.reservation.types.ReservationType;
import org.talend.services.reservationservice.ReservationService;

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
