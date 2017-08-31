package org.talend.esb.policy.correlation.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.esb.policy.correlation.feature.CorrelationIDFeature;
import org.talend.esb.policy.correlation.test.internal.HeaderCatcherInInterceptor;
import org.talend.services.test.library._1_0.Library;
import org.talend.services.test.library._1_0.SeekBookError;
import org.talend.types.test.library.common._1.ListOfBooks;
import org.talend.types.test.library.common._1.SearchFor;

import static org.junit.Assert.*;


public class CorrelationFeatureTest {

	private static final Logger LOG = Logger
			.getLogger(CorrelationIDFeature.class.getName());
	

    @SuppressWarnings("unused")
	private HeaderCatcherInInterceptor providerInInterceptor, consumerInInterceptor;



    private ClassPathXmlApplicationContext serviceContext;

    private ClassPathXmlApplicationContext startContext(String configFileName) {
        ClassPathXmlApplicationContext context;
        context = new ClassPathXmlApplicationContext(new String[] {configFileName});
        context.start();
        return context;
    }

    private ClassPathXmlApplicationContext startParticipants(String dir) {
        String configFileName = "conf/feature-test/"+dir+"/service-context.xml";
        return startContext(configFileName);
    }

    private void retrieveInterceptors(ClassPathXmlApplicationContext ctx) {
        consumerInInterceptor = (HeaderCatcherInInterceptor)ctx.getBean("clientInInterceptor");
        providerInInterceptor = (HeaderCatcherInInterceptor)ctx.getBean("serviceInInterceptor");
    }

    private ListOfBooks searchFor(String authorLastName, Library client) throws SeekBookError {
        SearchFor request = new SearchFor();
        request.getAuthorLastName().add(authorLastName);
        request.setPublisher("Frosty Edition");
        return  client.seekBook(request);
    }

    private void commonTest(String testName, String searchFor, String expectedResult) {

    	LOG.setLevel(Level.FINE);

        final String dir = testName;

        serviceContext = startParticipants(dir);
        retrieveInterceptors(serviceContext);

        Library client = (Library)serviceContext.getBean("libraryHttp");

        try {
            searchFor(searchFor, client);
        } catch (SeekBookError e) {
            fail("Exception during service call");
        }

        //assertEquals("Books amount in response differs from 1", 1, booksInResponse(response));
        assertNotNull(providerInInterceptor.getLatestCorrelationHeader());
        assertTrue(providerInInterceptor.getLatestCorrelationHeader().toString().contains(expectedResult));
        // assertNotNull(consumerInInterceptor.getLatestCorrelationHeader());
        //System.out.println("PROBLEM: " + consumerInInterceptor.getLatestCorrelationHeader());
        // assertTrue(consumerInInterceptor.getLatestCorrelationHeader().toString().contains(expectedResult));
    }


    @After
    public void closeContextAfterEachTest() {
        serviceContext.stop();
        serviceContext.close();
        serviceContext = null;
    }



    @Test
    public void testCallbackBasedCorrelation() {
        commonTest("callback-based", "Icebear",
                "customCorrelationHandler#");
    }
}
