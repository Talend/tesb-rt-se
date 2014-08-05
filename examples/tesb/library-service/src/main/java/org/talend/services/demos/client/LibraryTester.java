package org.talend.services.demos.client;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import junit.framework.TestCase;

import org.talend.esb.mep.requestcallback.feature.RequestCallbackFeature;
import org.talend.services.demos.common.Utils;
import org.talend.services.demos.library._1_0.Library;
import org.talend.services.demos.library._1_0.SeekBookError;
import org.talend.types.demos.library.common._1.ListOfBooks;
import org.talend.types.demos.library.common._1.SearchFor;
import org.talend.types.demos.library.common._1.SearchInBasementFor;

/**
 * The Class LibraryTester.
 */
public class LibraryTester {

    /** The Library proxy will be injected either by spring or by a direct call to the setter  */
	Library library;
    
    /**
     * Gets the library.
     *
     * @return the library
     */
    public Library getLibrary() {
        return library;
    }

    /**
     * Sets the library.
     *
     * @param library the new library
     */
    public void setLibrary(Library library) {
        this.library = library;
    }

    /**
     * Test request response positive.
     *
     * @throws SeekBookError the seek book error
     */
    public void testRequestResponsePositive() throws SeekBookError {
        // Test the positive case where author(s) are found and we retrieve
        // a list of books
    	System.out.println("***************************************************************");        	
    	System.out.println("*** Request-Response operation ********************************");
    	System.out.println("***************************************************************");    
        System.out.println("\nSending request for authors named Icebear");
        SearchFor request = new SearchFor();
        request.getAuthorLastName().add("Icebear");
        ListOfBooks response = library.seekBook(request);
        System.out.println("\nResponse received:");
        Utils.showBooks(response);

        TestCase.assertEquals(1, response.getBook().size());
        TestCase.assertEquals("Icebear", response.getBook().get(0).getAuthor().get(0).getLastName());    	
    }
    
    /**
     * Test request response business fault.
     *
     * @throws SeekBookError the seek book error
     */
    @SuppressWarnings("unused")
    public void testRequestResponseBusinessFault() throws SeekBookError {
    	
        // Test for an unknown Customer name and expect the NoSuchCustomerException
    	System.out.println("***************************************************************");          
        System.out.println("*** Request-Response operation with Business Fault ************");
        System.out.println("***************************************************************");    
        try {
        	SearchFor request = new SearchFor();
            System.out.println("\nSending request for authors named Grizzlybear");
            request.getAuthorLastName().add("Grizzlybear");
            ListOfBooks response = library.seekBook(request);
            TestCase.fail("We should get a SeekBookError here");
        } catch (SeekBookError e) {
            TestCase.assertNotNull("FaultInfo must not be null", e.getFaultInfo());
            TestCase.assertEquals("No book available from author Grizzlybear",
            		e.getFaultInfo().getException().get(0).getExceptionText());
            
            System.out.println("\nSeekBookError exception was received as expected:\n");
            
            Utils.showSeekBookError(e);

        }   	
    }    
    
    /**
     * Test oneway positive.
     *
     * @throws SeekBookError the seek book error
     */
    public void testOnewayPositive() throws SeekBookError {
    	
        // The implementation of updateCustomer is set to sleep for some seconds. 
        // Still this method should return instantly as the method is declared
        // as a one way method in the WSDL
        
    	System.out.println("***************************************************************");          
        System.out.println("*** Oneway operation ******************************************");
        System.out.println("***************************************************************");
        
        String isbnNumber = "111-22222";
        Date birthDate = (new GregorianCalendar(101, Calendar.MARCH, 5)).getTime();
        String zip = "12345";
        Date borrowed = new Date();
        
        System.out.println("Sending createLending request with parameters:");
        Utils.showLendingRequest(isbnNumber, birthDate, zip, borrowed);
        
        library.createLending(isbnNumber, birthDate, zip, borrowed);	
    }    
    
    /**
     * Test request callback positive.
     *
     * @throws SeekBookError the seek book error
     */
    public void testRequestCallbackPositive() throws SeekBookError {
        // Test the positive case where author(s) are found and we retrieve
        // a list of books
        System.out.println("***************************************************************");
        System.out.println("*** Request-Callback operation ********************************");
        System.out.println("***************************************************************");
        System.out.println("\nSending request(callback) for authors named Stripycat");
        SearchInBasementFor request = new SearchInBasementFor();
        request.getAuthorLastName().add("Stripycat");
        Map<String, Object> rctx = ((BindingProvider) library).getRequestContext();
        Map<String, Object> correlationInfo = new HashMap<String, Object>();
        rctx.put(RequestCallbackFeature.CALL_INFO_PROPERTY_NAME, correlationInfo);
        library.seekBookInBasement(request);
        String correlationId = (String) correlationInfo.get(
        		RequestCallbackFeature.CALL_ID_NAME);
        System.out.println("\nRequest sent.");
        System.out.println("Call ID is " + correlationId);
        try {
        	boolean moreToCome = LibraryConsumerImpl.waitForResponse() instanceof ListOfBooks;
        	System.out.println("\nProcessing of first callback response confirmed.\n");
        	if (moreToCome) {
        		LibraryConsumerImpl.waitForResponse();
        		System.out.println("\nProcessing of second callback response confirmed.\n");
        	}
        } catch (InterruptedException e) {
        	throw new RuntimeException("Request-callback test interrupted: ", e);
        }
    }
    
	/**
	 * Test library.
	 *
	 * @throws SeekBookError the seek book error
	 */
	public void testHttp() throws SeekBookError {

    	// Positive TestCase for Request-Response operation	
		testRequestResponsePositive();
    	
    	// Negative TestCase for Request-Response operation (with Business Fault)
    	// testRequestResponseBusinessFault();
    	
        System.out.println("***************************************************************");
        System.out.println("*** All calls were successful *********************************");
        System.out.println("***************************************************************");
        
    }

	/**
	 * Test library.
	 *
	 * @throws SeekBookError the seek book error
	 */
	public void testJms() throws SeekBookError {
		
    	// Positive TestCase for Oneway operation
    	testOnewayPositive();

    	// Positive TestCase for Request-Callback operation
    	testRequestCallbackPositive();
        
        System.out.println("***************************************************************");
        System.out.println("*** All calls were successful *********************************");
        System.out.println("***************************************************************");
        
    }

}
