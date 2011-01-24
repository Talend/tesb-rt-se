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
package org.talend.esb.client.app;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.sopera.services.crm.types.CustomerDetailsType;
import org.sopera.services.reservation.types.ConfirmationType;
import org.sopera.services.reservation.types.RESCarType;
import org.sopera.services.reservation.types.RESStatusType;
import org.springframework.beans.factory.InitializingBean;
import org.talend.esb.client.model.CarReserveModel;
import org.talend.esb.client.model.CarSearchModel;

public class CarRentalClientCommandProvider implements CommandProvider, InitializingBean {
	private static final String SP = " "; //$NON-NLS-1$
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy/MM/dd"); //$NON-NLS-1$
	private static final String SPC = "  "; //$NON-NLS-1$
	private static final String FOUND = "Found {0} cars."; //$NON-NLS-1$
	private static final String[] HN = { Messages.CarRentalClient_POS
										, Messages.CarRentalClient_Brand
										, Messages.CarRentalClient_Model
										, Messages.CarRentalClient_BookingClass
										, Messages.CarRentalClient_DayRate
										, Messages.CarRentalClient_WeekEndRate
										, Messages.CarRentalClient_Insurance};
	private static final String TO_SELECT = "\nTo reserve a car use \"racRent <pos>\""; //$NON-NLS-1$
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
	private String pickupDate = ""; //$NON-NLS-1$
	private String returnDate = ""; //$NON-NLS-1$

	private String header;


	
	/**
	 * Search for cars to rent
	 * @param intp - the command interpreter instance
	 */
	public void _racSearch(CommandInterpreter intp) {
		String userName = getMandatoryParameter(intp, Messages.CarRentalClient_User);
		pickupDate = getDateParameter(intp, Messages.CarRentalClient_Pickup);
		returnDate = getDateParameter(intp, Messages.CarRentalClient_Return);
		this.searcher.search(userName, pickupDate, returnDate);
		_racShow(intp);
	}

	
	/**
	 * Print last search results for cars to rent
	 * @param intp - the command interpreter instance
	 */
	public void _racShow(CommandInterpreter intp) {
		intp.println(MessageFormat.format(FOUND, this.searcher.getCars().size()));
		intp.println();
		int pos = 0;
		
		if (this.searcher.getCars().size() > 0) {
			intp.println(header);
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
			intp.println(sb.toString());
			intp.println(TO_SELECT);
		}
	}
	
	
	/**
	 * Rent a car available in the last serach result
	 * @param intp - the command interpreter instance
	 */
	public void _racRent(CommandInterpreter intp) {
		int pos = getPositiveIntParameter(intp, Messages.CarRentalClient_Pos) - 1;
		
		if (pos <= searcher.getCars().size() && searcher.getCars().get(pos) != null) {
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
			
			intp.println(MessageFormat.format(CONFIRMATION
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
			intp.println("Invalid selection: " + pos); //$NON-NLS-1$
		}
	}
	
	
	public void _racGUI(CommandInterpreter intp) {
		CarRentalClientGui.openApp(searcher, reserver);
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
	 */
	public String getHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n---SOPERA: Rent a Car (OSGi) Commands---\n"); //$NON-NLS-1$
		sb.append("\tracGUI \t\t\t\t\t\t (Show GUI)\n"); //$NON-NLS-1$
		sb.append("\tracSearch <user> <pickupDate> <returnDate> \t (Search for cars to rent, date format yyyy/mm/dd)\n"); //$NON-NLS-1$
		sb.append("\tracShow \t\t\t\t\t (Show last search result of racSearch)\n"); //$NON-NLS-1$
		sb.append("\tracRent <pos> \t\t\t\t\t (Rent a car listed in search result of racSearch)\n\n"); //$NON-NLS-1$
		return sb.toString();
	}

	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		StringBuilder hdr1 = new StringBuilder();
		StringBuilder hdr2 = new StringBuilder();

		for (String hd : HN) {
			hdr1.append(hd + SPC);
			hdr2.append(pad("", hd.length(), '-', false) + SPC); //$NON-NLS-1$
		}

		header = hdr1.toString() + "\n" + hdr2.toString(); //$NON-NLS-1$
		System.out.println(getHelp()); //$NON-NLS-1$
		
		CarRentalClientGui.openApp(searcher, reserver);
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
	

	/**
	 * Get a command line argument and give an error message if it's missing
	 * @param intp - the command interpreter
	 * @param name - the name of the argument
	 * @return - the argument value
	 */
	private String getMandatoryParameter(CommandInterpreter intp, String name) {
		String parameter = intp.nextArgument();

		if (parameter == null || "".equals(parameter) || parameter.startsWith("-")) { //$NON-NLS-1$ //$NON-NLS-2$
			String msg = "Invalid or missing \"" + name + "\" parameter: " + parameter; //$NON-NLS-1$ //$NON-NLS-2$
			throw new IllegalArgumentException(msg);
		}
		
		return parameter;
	}


	/**
	 * Get a date command line argument and give an error message if it's missing
	 * @param intp - the command interpreter
	 * @param name - the name of the argument
	 * @return - the argument value
	 */
	private String getDateParameter(CommandInterpreter intp, String name) {
		String strDate = getMandatoryParameter(intp, name);
		
		try {
			SDF.parse(strDate);
		
		} catch (ParseException e) {
			String msg = "Illegal date format in " + name + " parameter: " + strDate + " (expected: " + SDF.toPattern() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			throw new IllegalArgumentException(msg);
		}
		return strDate;
	}


	/**
	 * Get an integer command line argument and give an error message if it's missing
	 * @param intp - the command interpreter
	 * @param name - the name of the argument
	 * @return - the argument value
	 */
	private int getPositiveIntParameter(CommandInterpreter intp, String name) {
		int result = 0;
		String strInt = getMandatoryParameter(intp, name);
		
		try {
			result = Integer.parseInt(strInt);
		
		} catch (NumberFormatException e) {
			String msg = "Illegal number in " + name + " parameter: " + strInt; //$NON-NLS-1$ //$NON-NLS-2$
			throw new IllegalArgumentException(msg);
		}
		
		if (result <= 0) {
			throw new IllegalArgumentException("Must enter a " + name + " > 0"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return result;
	}


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
