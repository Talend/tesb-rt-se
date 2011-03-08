package org.sopera.monitoring.server.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sopera.monitoring.event.CustomInfo;
import org.springframework.jdbc.core.RowMapper;

public class CustomInfoRowMapper implements RowMapper<CustomInfo>{

	@Override
	public CustomInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
		CustomInfo ci = new CustomInfo();
		ci.getProperties().put(rs.getString("CUST_KEY"), rs.getString("CUST_VALUE"));

		return ci;
	}

}
