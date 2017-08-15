package org.talend.esb.policy.correlation.test.internal;


import org.springframework.beans.factory.InitializingBean;
import org.talend.services.test.library._1_0.Library;
import org.talend.services.test.library._1_0.SeekBookError;
import org.talend.types.test.library.common._1.ListOfBooks;
import org.talend.types.test.library.common._1.SearchFor;


/**
 * The Class LibraryTester.
 */
public class LibraryTester implements InitializingBean {

    /** The Library proxy for HTTP calls will be injected either by spring or by a direct call to the setter  */
    Library libraryHttp;


    public Library getLibraryHttp() {
        return libraryHttp;
    }

    public void afterPropertiesSet() throws Exception {
        System.out.println("Library Client initialized.");
        Thread.sleep(5000);
      //  testRequestResponsePositive();
    }


    public void setLibraryHttp(Library library) {
        this.libraryHttp = library;
    }

    public void testRequestResponsePositive() throws SeekBookError {
        // Test the positive case where author(s) are found and we retrieve
        // a list of books
        System.out.println("***************************************************************");
        System.out.println("*** Request-Response operation ********************************");
        System.out.println("***************************************************************");
        System.out.println("\nSending request for authors named Icebear");
        SearchFor request = new SearchFor();
        request.getAuthorLastName().add("Icebear");
        ListOfBooks response = libraryHttp.seekBook(request);
        System.out.println("\nResponse received:");


        if (response.getBook().size() != 1) {
            System.out.println("An error occured: number of books found is not equal to 1");
        }

        if (!"Icebear".equals(response.getBook().get(0).getAuthor().get(0).getLastName())) {
            System.out.println("An error occured: the author of the found book is not Icebear");
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

        }
    }
}
