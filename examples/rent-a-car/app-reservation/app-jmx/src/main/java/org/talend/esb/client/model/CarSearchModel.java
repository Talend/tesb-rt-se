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

package org.talend.esb.client.model;

import java.util.List;

import org.talend.services.reservation.types.RESCarType;
import org.talend.services.crm.types.CustomerDetailsType;

public interface CarSearchModel {
	public int search(String userName, String pickupDate, String returnDate);

	public CustomerDetailsType getCustomer();

	public List<RESCarType> getCars();
}
