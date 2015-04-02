package org.talend.services.demos.client;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.springframework.beans.factory.InitializingBean;
import org.talend.esb.mep.requestcallback.feature.RequestCallbackFeature;
import org.talend.services.demos.common.Utils;
import org.talend.services.demos.library._1_0.Library;
import org.talend.services.demos.library._1_0.SeekBookError;
import org.talend.types.demos.library.common._1.ListOfBooks;
import org.talend.types.demos.library.common._1.SearchFor;

/**
 * The Class LibraryTester.
 */
public class LibraryTester implements InitializingBean {

    /** The Library proxy for HTTP calls will be injected either by spring or by a direct call to the setter  */
    Library libraryHttp;

    /** The Library proxy for JMS calls will be injected either by spring or by a direct call to the setter  */
    Library libraryJms;

    public Library getLibraryHttp() {
        return libraryHttp;
    }

    public void afterPropertiesSet() throws Exception {
        System.out.println("Library Client initialized.");
        Thread.sleep(5000);
        testRequestResponsePositive();
//        testOnewayPositive();
//        testRequestCallbackPositive();
    }


    public void setLibraryHttp(Library library) {
        this.libraryHttp = library;
    }

    public Library getLibraryJms() {
        return libraryJms;
    }

    public void setLibraryJms(Library libraryJms) {
        this.libraryJms = libraryJms;
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
        request.getAuthorLastName().add("Sykes");
        ListOfBooks response = libraryHttp.seekBook(request);
        System.out.println("\nResponse received:");
        Utils.showBooks(response);


        if (response.getBook().size() != 1) {
            System.out.println("An error occured: number of books found is not equal to 1");
        }

        if (!"Icebear".equals(response.getBook().get(0).getAuthor().get(0).getLastName())
                && !"Sykes".equals(response.getBook().get(0).getAuthor().get(0).getLastName())) {
            System.out.println("An error occured: the author of the found book does not match the request");
        }
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
            ListOfBooks response = libraryHttp.seekBook(request);

            System.out.println("FAIL: We should get a SeekBookError here");

        } catch (SeekBookError e) {
            if (e.getFaultInfo() == null) {
                System.out.println("FaultInfo must not be null");
            }
            if ("No book available from author Grizzlybear".equals(
                    e.getFaultInfo().getException().get(0).getExceptionText())) {
                System.out.println("Unexpected error message received");
            }



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

        libraryJms.createLending(isbnNumber, birthDate, zip, borrowed);
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
        SearchFor request = new SearchFor();
        request.getAuthorLastName().add("Stripycat");
        Map<String, Object> rctx = ((BindingProvider) libraryJms).getRequestContext();
        Map<String, Object> correlationInfo = new HashMap<String, Object>();
        rctx.put(RequestCallbackFeature.CALL_INFO_PROPERTY_NAME, correlationInfo);
        libraryJms.seekBookInBasement(request);
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
        //testOnewayPositive();

        // Positive TestCase for Request-Callback operation
        testRequestCallbackPositive();

        System.out.println("***************************************************************");
        System.out.println("*** All calls were successful *********************************");
        System.out.println("***************************************************************");

    }

}
