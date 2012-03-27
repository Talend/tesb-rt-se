/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package oauth2.thirdparty;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("reception")
public class RestaurantService {

	private AtomicBoolean availableHours[] = new AtomicBoolean[24];
	public RestaurantService() {
		for (int i = 0; i < 24; i++) {
			availableHours[i] = new AtomicBoolean();
		}
	}
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public String reserveAt(@FormParam("name") String name,
                             @FormParam("phone") String phone, 
                             @FormParam("hour") int hour) {
		if (availableHours[hour].compareAndSet(false, true)) {
			return getAddress();
		} else {
			return null;
		}
	}
	
	public String getAddress() {
		return "5 Star Pizza, Muensterstrasse 1012, Mainz City";
	}
	
}
