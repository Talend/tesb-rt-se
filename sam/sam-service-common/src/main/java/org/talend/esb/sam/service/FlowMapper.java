package org.talend.esb.sam.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class FlowMapper implements RowMapper<Flow> {

    @Override
    public Flow mapRow(ResultSet rs, int rowNum) throws SQLException {
        Flow flow = new Flow();
        flow.setId(rs.getString("MI_FLOW_ID"));
        return flow;
    }

}
