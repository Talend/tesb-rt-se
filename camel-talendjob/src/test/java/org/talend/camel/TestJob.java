package org.talend.camel;

import org.apache.camel.Exchange;

import routines.system.api.TalendJob;

public class TestJob implements TalendJob {

    private Exchange exchange;

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    public String[][] runJob(String[] args) {
        return null;
    }

    public int runJobInTOS(String[] args) {
        exchange.getIn().setBody((args.length == 1) ? args[0] : null);
        return 0;
    }

}
