package org.talend.esb.client.commands;


import java.text.SimpleDateFormat;

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.springframework.beans.factory.InitializingBean;
import org.talend.esb.client.app.CarRentalClientGui;
import org.talend.esb.client.app.Messages;
import org.talend.esb.client.model.CarReserveModel;
import org.talend.esb.client.model.CarSearchModel;

@Command(scope = "car", name = "GUI", description = "Rent a car GUI")

public class CarGUI extends OsgiCommandSupport implements InitializingBean{
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
	
	@Override
	protected Object doExecute() throws Exception {
		CarRentalClientGui.openApp(searcher, reserver);
		return null;
	}
	
	public String getHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n---SOPERA: Rent a Car (OSGi) Commands---\n"); //$NON-NLS-1$
		sb.append("\tcar:GUI \t\t\t\t\t\t (Show GUI)\n"); //$NON-NLS-1$
//		sb.append("\tracSearch <user> <pickupDate> <returnDate> \t (Search for cars to rent, date format yyyy/mm/dd)\n"); //$NON-NLS-1$
	//	sb.append("\tracShow \t\t\t\t\t (Show last search result of racSearch)\n"); //$NON-NLS-1$
		//sb.append("\tracRent <pos> \t\t\t\t\t (Rent a car listed in search result of racSearch)\n\n"); //$NON-NLS-1$
		return sb.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		StringBuilder hdr1 = new StringBuilder();
		StringBuilder hdr2 = new StringBuilder();

		for (String hd : HN) {
			hdr1.append(hd + SPC);
			hdr2.append(pad("", hd.length(), '-', false) + SPC); //$NON-NLS-1$
		}

		header = hdr1.toString() + "\n" + hdr2.toString(); //$NON-NLS-1$
		System.out.println(getHelp()); //$NON-NLS-1$
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
		
}