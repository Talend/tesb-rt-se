package service;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class DoubleItPortTypeImplTest {

    @Test
    public void testDoubleItWorksWithPositiveNumbers() {
        DoubleItPortTypeImpl port = new DoubleItPortTypeImpl();
        int response = port.doubleIt(12);
        assertEquals("DoubleIt isn't working with positive numbers", 24, response);
    }
    
    @Test
    public void testDoubleItWorksWithZero() {
        DoubleItPortTypeImpl port = new DoubleItPortTypeImpl();
        int response = port.doubleIt(0);
        assertEquals("DoubleIt isn't doubling zero correctly", 0, response);
    }

    @Test
    public void testDoubleItWorksWithNegativeNumbers() {
        DoubleItPortTypeImpl port = new DoubleItPortTypeImpl();
        int response = port.doubleIt(-8);
        assertEquals("DoubleIt isn't working with negative numbers", -16, response);
    }
}
