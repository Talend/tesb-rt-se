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
import org.talend.services.reservation.types.RESCarType;
import org.talend.esb.client.app.Messages;
import org.talend.esb.client.model.CarSearchModel;

@Command(scope = "car", name = "search", description = "Search a car")

public class CarSearch extends OsgiCommandSupport {
	private static String[] lastSearchResult = null;
	private static final String FOUND = "Found {0} cars."; //$NON-NLS-1$
	private static final String[] HN = { Messages.CarRentalClient_POS
		, Messages.CarRentalClient_Brand
		, Messages.CarRentalClient_Model
		, Messages.CarRentalClient_BookingClass
		, Messages.CarRentalClient_DayRate
		, Messages.CarRentalClient_WeekEndRate
		, Messages.CarRentalClient_Insurance};
	private static final String SPC = "  "; //$NON-NLS-1$
	private static final String TO_SELECT = "\nTo reserve a car use \"car:rent <pos>\""; //$NON-NLS-1$
	private CarSearchModel searcher;
	private String header;

	@Argument(index = 0, name = "userName", description = "user name", required = true, multiValued = false)
    String userName;
	@Argument(index = 1, name = "pickupDate", description = "pickup Date", required = true, multiValued = false)
    String pickupDate;
	@Argument(index = 2, name = "returnDate", description = "return Date", required = true, multiValued = false)
    String returnDate;

	@Override
	protected Object doExecute() throws Exception {
		System.out.println("Executing CarSearch");
		racSearch();
		return null;
	}

	public static String[] getLastSearchParams() {
		return CarSearch.lastSearchResult;
	}

	public void racSearch() {
		this.searcher.search(userName, pickupDate, returnDate);
		CarSearch.lastSearchResult = new String[]{userName, pickupDate, returnDate};
		racShow();
	}

	public void racShow() {
		System.out.println(MessageFormat.format(FOUND, this.searcher.getCars().size()));
		System.out.println();
		int pos = 0;

		if (this.searcher.getCars().size() > 0) {
			header = Messages.CarRentalClient_CarDetails;
			System.out.println(header);
			StringBuilder sb = new StringBuilder();

			for (RESCarType car : this.searcher.getCars()) {
				pos++;
				sb.append(padl("" + pos, HN[0].length())).append(SPC); //$NON-NLS-1$
				sb.append(padr(car.getBrand(), HN[1].length())).append(SPC);
				sb.append(padr(car.getDesignModel(), HN[2].length())).append(SPC);
				sb.append(padr(car.getClazz(), HN[3].length())).append(SPC);
				sb.append(padl(car.getRateDay(), HN[4].length())).append(SPC);
				sb.append(padl(car.getRateWeekend(), HN[5].length())).append(SPC);
				sb.append(padl(car.getSecurityGuarantee(), HN[6].length())).append(SPC);
				sb.append("\n"); //$NON-NLS-1$
			}
			System.out.println(sb.toString());
			System.out.println(TO_SELECT);
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
/*	public void setReserver(CarReserveModel reserver) {
		this.reserver = reserver;
	}
			*/

	private static String padr(String src, int length) {
		return pad(src, length, ' ', false);
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
