package org.talend.esb.client.commands;

import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.springframework.beans.factory.InitializingBean;
import org.talend.esb.client.app.CarRentalClientGui;
import org.talend.esb.client.model.CarReserveModel;
import org.talend.esb.client.model.CarSearchModel;

@Command(scope = "car", name = "GUI", description = "Rent a car GUI")

public class CarGUI extends OsgiCommandSupport implements InitializingBean{
	private CarSearchModel searcher;
	private CarReserveModel reserver;
	
	@Override
	protected Object doExecute() throws Exception {
		CarRentalClientGui.openApp(searcher, reserver);
		return null;
	}
	
	public String getHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n---SOPERA: Rent a Car (OSGi) Commands---\n"); //$NON-NLS-1$
		sb.append("\tcar:GUI \t\t\t\t (Show GUI)\n"); //$NON-NLS-1$
		sb.append("\tcar:search <user> <pickupDate> <returnDate> \n (Search for cars to rent, date format yyyy/mm/dd)\n"); //$NON-NLS-1$
		sb.append("\tcar:rent   <user> <pickupDate> <returnDate> <pos> \n (Rent a car listed in search result of carSearch)\n\n"); //$NON-NLS-1$
		return sb.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		System.out.println(getHelp()); //$NON-NLS-1$
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