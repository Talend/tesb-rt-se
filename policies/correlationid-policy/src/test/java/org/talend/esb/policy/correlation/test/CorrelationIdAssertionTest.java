package org.talend.esb.policy.correlation.test;


import static org.junit.Assert.*;


import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.esb.policy.correlation.test.internal.HeaderCatcherInInterceptor;
import org.talend.esb.policy.correlation.test.internal.HeaderCatherOutInterceptor;
import org.talend.services.test.library._1_0.Library;
import org.talend.services.test.library._1_0.SeekBookError;
import org.talend.types.test.library.common._1.ListOfBooks;
import org.talend.types.test.library.common._1.SearchFor;


public class CorrelationIdAssertionTest {


    private HeaderCatherOutInterceptor consumerOutInterceptor, providerOutInterceptor;
    private HeaderCatcherInInterceptor providerInInterceptor, consumerInInterceptor;



    private ClassPathXmlApplicationContext serviceContext;

    private ClassPathXmlApplicationContext startContext(String configFileName) {
        ClassPathXmlApplicationContext context;
        context = new ClassPathXmlApplicationContext(new String[] {configFileName});
        context.start();
        return context;
    }

    private ClassPathXmlApplicationContext startParticipants(String dir) {
        String configFileName = "conf/assertion-test/"+dir+"/service-context.xml";
        return startContext(configFileName);
    }

    private void retrieveInterceptors(ClassPathXmlApplicationContext ctx) {
        consumerOutInterceptor = (HeaderCatherOutInterceptor)ctx.getBean("clientOutInterceptor");
        providerOutInterceptor = (HeaderCatherOutInterceptor)ctx.getBean("serviceOutInterceptor");
        consumerInInterceptor = (HeaderCatcherInInterceptor)ctx.getBean("clientInInterceptor");
        providerInInterceptor = (HeaderCatcherInInterceptor)ctx.getBean("serviceInInterceptor");
    }

    private ListOfBooks searchFor(String authorLastName, Library client) throws SeekBookError {
        SearchFor request = new SearchFor();
        request.getAuthorLastName().add(authorLastName);
        request.setPublisher("Frosty Edition");
        return  client.seekBook(request);
    }

    private int booksInResponse(ListOfBooks response) {
        return response.getBook().size();
    }

    private String authorLastName(ListOfBooks response) {
        return response.getBook().get(0).getAuthor().get(0).getLastName();
    }


    private void commonTest(String testName, String searchFor, String expectedResult) {

        final String dir = testName;

        serviceContext = startParticipants(dir);
        retrieveInterceptors(serviceContext);

        Library client = (Library)serviceContext.getBean("libraryHttp");

        ListOfBooks response = null;

        try {
            response = searchFor(searchFor, client);
        } catch (SeekBookError e) {
            fail("Exception during service call");
        }

        //assertEquals("Books amount in response differs from 1", 1, booksInResponse(response));
        assertNotNull(consumerOutInterceptor.getLatestCorrelationHeader());
        assertNotNull(providerInInterceptor.getLatestCorrelationHeader());
        assertTrue(providerInInterceptor.getLatestCorrelationHeader().toString().contains(expectedResult));
        assertNotNull(consumerInInterceptor.getLatestCorrelationHeader());
        assertTrue(consumerInInterceptor.getLatestCorrelationHeader().toString().contains(expectedResult));

        String providerInCorrelationId = providerInInterceptor.getLatestCorrelationHeader().toString();
        String consumerInCorrelationId = consumerInInterceptor.getLatestCorrelationHeader().toString();
        assertEquals(providerInCorrelationId, consumerInCorrelationId);
    }


    @After
    public void closeContextAfterEachTest() {
        serviceContext.stop();
        serviceContext.close();
        serviceContext = null;
    }



    @Test
    public void testXpathBasedCorrelation() {
        commonTest("xpath-based", "Icebear",
                    "customer#customerLastName=Icebear;publisher=Frosty Edition");
    }


    @Test
    public void testCallbackBased() {
        commonTest("callback-based", "Icebear",
                "customCorrelationHandler#");
    }


    @Test
    public void testGuidBased() {
        commonTest("dummy", "Icebear",
                "urn:uuid:");
    }
}
