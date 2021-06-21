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

package org.talend.esb.client.commands;

import java.text.MessageFormat;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Argument;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.talend.services.crm.types.CustomerDetailsType;
import org.talend.services.reservation.types.ConfirmationType;
import org.talend.services.reservation.types.RESCarType;
import org.talend.services.reservation.types.RESStatusType;
import org.talend.esb.client.app.Messages;
import org.talend.esb.client.model.CarReserveModel;
import org.talend.esb.client.model.CarSearchModel;

@Command(scope = "car", name = "rent", description = "Rent a car")

public class CarRent extends OsgiCommandSupport {
	private static final String SP = " "; //$NON-NLS-1$
	private static final String CONFIRMATION = "\n{0}\n\n" + //$NON-NLS-1$
	Messages.CarRentalClient_ReservationID + " {1}\n\n" + //$NON-NLS-2$
	Messages.CarRentalClient_CustomerDetails + "\n" + //$NON-NLS-2$
	"----------------\n" + //$NON-NLS-1$
	SP + Messages.CarRentalClient_Name + ":   {2}\n" + //$NON-NLS-2$
	SP + Messages.CarRentalClient_eMail + ":  {3}\n" + //$NON-NLS-2$
	SP + Messages.CarRentalClient_City + ":   {4}\n" + //$NON-NLS-2$
	SP + Messages.CarRentalClient_Status + ": {5}\n\n" + //$NON-NLS-2$
	Messages.CarRentalClient_CarDetails + "\n" + //$NON-NLS-2$
	"-----------\n" + //$NON-NLS-1$
	SP + "Brand" + ": {6}\n" + //$NON-NLS-1$ //$NON-NLS-2$
	SP + "Model" + ": {7}\n\n" + //$NON-NLS-1$ //$NON-NLS-2$
	Messages.CarRentalClient_ReservationDetails + "\n" + //$NON-NLS-2$
	"-------------------\n" + //$NON-NLS-1$
	SP + Messages.CarRentalClient_Pickup + ": {8}\n" + //$NON-NLS-2$
	SP + Messages.CarRentalClient_Return + ":  {9}\n" + //$NON-NLS-2$
	SP + Messages.CarRentalClient_DayRate + ":   {10}\n" + //$NON-NLS-2$
	SP + Messages.CarRentalClient_WeekEndRate + ": {11}\n" + //$NON-NLS-2$
	SP + Messages.CarRentalClient_Credits + ":      {12}\n" + //$NON-NLS-2$
	Messages.CarRentalClient_Thanks;

	private CarSearchModel searcher;
	private CarReserveModel reserver;

	@Argument(index = 0, name = "pos", description = "Rent a car listed in search result of racSearch", required = true, multiValued = false)
    int pos;

	@Override
	protected Object doExecute() throws Exception {
		if (null == CarSearch.getLastSearchParams()) {
			System.out.println("Do car:search first.");
			return null;
		}
		racRent();
		return null;
	}

	/**
	 * Rent a car available in the last serach result
	 * @param intp - the command interpreter instance
	 */
	public void racRent() {
		pos = pos - 1;
		String userName = CarSearch.getLastSearchParams()[0];
		String pickupDate = CarSearch.getLastSearchParams()[1];
		String returnDate = CarSearch.getLastSearchParams()[2];
		this.searcher.search(userName, pickupDate, returnDate);
		if (searcher!=null && searcher.getCars()!= null && pos < searcher.getCars().size() && searcher.getCars().get(pos) != null) {
			RESStatusType resStatus = reserver.reserveCar(searcher.getCustomer()
															, searcher.getCars().get(pos)
															, pickupDate
															, returnDate);
			ConfirmationType confirm = reserver.getConfirmation(resStatus
															, searcher.getCustomer()
															, searcher.getCars().get(pos)
															, pickupDate
															, returnDate);

			RESCarType car = confirm.getCar();
			CustomerDetailsType customer = confirm.getCustomer();

			System.out.println(MessageFormat.format(CONFIRMATION
					, confirm.getDescription()
					, confirm.getReservationId()
					, customer.getName()
					, customer.getEmail()
					, customer.getCity()
					, customer.getStatus()
					, car.getBrand()
					, car.getDesignModel()
					, confirm.getFromDate()
					, confirm.getToDate()
					, padl(car.getRateDay(), 10)
					, padl(car.getRateWeekend(), 10)
					, padl(confirm.getCreditPoints().toString(), 7)));
		} else {
			System.out.println("Invalid selection: " + (pos+1)); //$NON-NLS-1$
		}
	}

	/**
	 * Set the CarSearchModel used to look for cars
	 * @param searcher - the CarSearchModel instance
	 */
	public void setSearcher(CarSearchModel searcher) {
		this.searcher = searcher;
	}


	/**
	 * Set the CarReserveModel used to reserve a car
	 * @param reserver - the CarReserveModel instance
	 */
	public void setReserver(CarReserveModel reserver) {
		this.reserver = reserver;
	}


	private static String padl(String src, int length) {
		return pad(src, length, ' ', true);
	}


	private static String pad(String src, int length, char c, boolean insert) {
		StringBuilder sb = new StringBuilder(src);

		if (insert) {
			while(sb.length() < length) sb.insert(0, c);
		} else {
			while(sb.length() < length) sb.append(c);
		}
		return sb.toString();
	}

}
