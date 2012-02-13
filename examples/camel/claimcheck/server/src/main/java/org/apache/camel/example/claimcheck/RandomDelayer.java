package org.apache.camel.example.claimcheck;

import java.util.Random;

/**
 * Delays a message by a random time of up to 1s
 */
public class RandomDelayer {
    Random random;
    
    public RandomDelayer() {
        random = new Random();
    }
    
    public void randomDelay() throws InterruptedException {
        Thread.sleep(random.nextInt(1000));
    }
}
