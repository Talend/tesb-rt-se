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

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.talend.esb.client.app.CarRentalClientGui;
import org.talend.esb.client.model.CarReserveModel;
import org.talend.esb.client.model.CarSearchModel;

@Command(scope = "car", name = "GUI", description = "Rent a car GUI")
public class CarGUI extends OsgiCommandSupport {
	private CarSearchModel searcher;
	private CarReserveModel reserver;

	@Override
	protected Object doExecute() throws Exception {
		CarRentalClientGui.openApp(searcher, reserver);
		return null;
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
