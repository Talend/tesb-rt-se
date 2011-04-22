/*******************************************************************************
*
* Copyright (c) 2011 Talend Inc. - www.talend.com
* All rights reserved.
*
* This program and the accompanying materials are made available
* under the terms of the Apache License v2.0
* which accompanies this distribution, and is available at
* http://www.apache.org/licenses/LICENSE-2.0
*
*******************************************************************************/
package org.talend.esb.client.app;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.talend.esb.client.app.messages"; //$NON-NLS-1$
	public static String CarRentalClient_BookingClass;
	public static String CarRentalClient_Brand;
	public static String CarRentalClient_CarDetails;
	public static String CarRentalClient_City;
	public static String CarRentalClient_Credits;
	public static String CarRentalClient_CustomerDetails;
	public static String CarRentalClient_DayRate;
	public static String CarRentalClient_eMail;
	public static String CarRentalClient_Insurance;
	public static String CarRentalClient_Model;
	public static String CarRentalClient_Name;
	public static String CarRentalClient_Pickup;
	public static String CarRentalClient_Pos;
	public static String CarRentalClient_POS;
	public static String CarRentalClient_ReservationDetails;
	public static String CarRentalClient_ReservationID;
	public static String CarRentalClient_Return;
	public static String CarRentalClient_Status;
	public static String CarRentalClient_Thanks;
	public static String CarRentalClient_User;
	public static String CarRentalClient_WeekEndRate;
	public static String CarRentalClient_Title;
	public static String CarRentalClient_CmdFind;
	public static String CarRentalClient_CmdReserve;
	public static String CarRentalClient_CmdCancel;
	public static String CarRentalClient_CmdClose;
	public static String CarRentalClient_CmdBack;
	public static String CarRentalClient_Offering;
	public static String CarRentalClient_SelectInfo;
	public static String CarRentalClient_Help;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
