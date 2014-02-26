package org.talend.services.demos.client;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.talend.services.demos.library._1_0.Library;
import org.talend.services.demos.library._1_0.SeekBookError;
import org.talend.types.demos.library.common._1.ListOfBooks;
import org.talend.types.demos.library.common._1.SearchFor;

public class LibraryTester {

	// The Library proxy will be injected either by spring or by a direct call to the setter 
    Library library;
    
    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public void testLibrary() throws SeekBookError {
        // First we test the positive case where customers are found and we retreive
        // a list of customers
        System.out.println("Sending request for authors named Icebear");
        SearchFor request = new SearchFor();
        request.getAuthorLastName().add("Icebear");
        ListOfBooks response = library.seekBook(request);
        System.out.println("Response received");
        TestCase.assertEquals(1, response.getBook().size());
        TestCase.assertEquals("Icebear", response.getBook().get(0).getAuthor().get(0).getLastName());
        
        // Then we test for an unknown Customer name and expect the NoSuchCustomerException
        try {
            request = new SearchFor();
            request.getAuthorLastName().add("Grizzlybear");
            response = library.seekBook(request);
            TestCase.fail("We should get a SeekBookError here");
        } catch (SeekBookError e) {
            System.out.println(e.getMessage());
            TestCase.assertNotNull("FaultInfo must not be null", e.getFaultInfo());
            TestCase.assertEquals("No book available from author Grizzlybear",
            		e.getFaultInfo().getException().get(0).getExceptionText());
            System.out.println("SeekBookError exception was received as expected");
        }
        
        // The implementation of updateCustomer is set to sleep for some seconds. 
        // Still this method should return instantly as the method is declared
        // as a one way method in the WSDL
        Date birthDate = (new GregorianCalendar(101, Calendar.MARCH, 5)).getTime();
        library.createLending("111-22222", birthDate, "12345", new Date());
        
        System.out.println("All calls were successful");
    }

}
