package org.sopera.monitoring.server.persistence;
import org.springframework.jdbc.support.incrementer.AbstractSequenceMaxValueIncrementer;


public class DerbySequenceIncrementer extends AbstractSequenceMaxValueIncrementer {

    @Override
    protected String getSequenceQuery() {
        return "VALUES (NEXT VALUE FOR " + getIncrementerName() + ")";
    }

}
