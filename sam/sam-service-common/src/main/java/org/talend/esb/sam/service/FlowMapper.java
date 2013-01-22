package org.talend.esb.sam.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.talend.esb.sam.common.event.Event;

public class FlowMapper implements RowMapper<Flow> {

    @Override
    public Flow mapRow(ResultSet rs, int rowNum) throws SQLException {
        Flow flow = new Flow();
        //TODO: implement mapper
        return flow;
    }

}
