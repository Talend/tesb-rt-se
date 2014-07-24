package org.talend.esb.sam.agent.serviceclient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class AvailabilityChecker implements Runnable {

    private static final Logger LOG = Logger.getLogger(AvailabilityChecker.class.getName());

    private static Thread t;
    private static volatile boolean shouldStop = true;
    private static volatile int checkRequestCounter = 0;
    private static String samServerAddress;
    private static final int NUMBER_OF_PING_RETRIES = 3;

    public boolean isAvailable() {
        LOG.fine("Checking availability: " + String.valueOf(checkRequestCounter < NUMBER_OF_PING_RETRIES));
        return checkRequestCounter < NUMBER_OF_PING_RETRIES;
    }

    public void setSamServerAddress(String samServerAddress) {
        LOG.fine("SAM Server ping url was set to " + samServerAddress + "?wsdl");
        samServerAddress = samServerAddress + "?wsdl";
    }

    public void start() {
        LOG.info("Starting SAM Agent ping thread");
        shouldStop = false;
        checkRequestCounter = 0;
        if((t != null && !t.isAlive()) || (t == null)) {
            synchronized (AvailabilityChecker.class) {
                if((t != null && !t.isAlive()) || (t == null)) {
                    t = new Thread(this, "Ping Thread");
                    t.start();
                    LOG.info("SAM Agent ping thread was created");
                }
            }
        }
        else {
            LOG.info("SAM Agent ping thread is already started");
        }
    }

    public void stop() {
        LOG.info("Stopping SAM Agent ping thread");
        shouldStop = true;
        checkRequestCounter = 0;
    }

    private boolean checkAvailability() {
        try {
            if (samServerAddress == null) return false;
            HttpURLConnection connection = (HttpURLConnection) new URL(samServerAddress).openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (IOException exception) {
            return false;
        }
    }

    @Override
    public void run() {
        while(!shouldStop)
        {
            boolean result = checkAvailability();
            if(result) {
                stop();
                break;
            }
            checkRequestCounter++;
            LOG.fine("Fail request counter: " + checkRequestCounter);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        t = null;
    }
}
