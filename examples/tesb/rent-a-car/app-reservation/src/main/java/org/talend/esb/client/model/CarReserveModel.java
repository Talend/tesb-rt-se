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

package org.talend.esb.client.model;

import org.talend.services.crm.types.CustomerDetailsType;
import org.talend.services.reservation.types.ConfirmationType;
import org.talend.services.reservation.types.RESCarType;
import org.talend.services.reservation.types.RESStatusType;

public interface CarReserveModel {
	public RESStatusType reserveCar(CustomerDetailsType customer, RESCarType car, String pickupDate, String returnDate);

	public ConfirmationType getConfirmation(RESStatusType reservationState, CustomerDetailsType customer,
			RESCarType car, String pickupDate, String returnDate);
}
