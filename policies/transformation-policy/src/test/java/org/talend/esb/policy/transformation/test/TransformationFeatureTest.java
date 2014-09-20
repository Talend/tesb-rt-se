package org.talend.esb.policy.transformation.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.services.test.library._1_0.Library;
import org.talend.services.test.library._1_0.SeekBookError;
import org.talend.types.test.library.common._1.ListOfBooks;
import org.talend.types.test.library.common._1.SearchFor;

public class TransformationFeatureTest {

    private ClassPathXmlApplicationContext serviceContext;

    private ClassPathXmlApplicationContext startContext(String configFileName) {
        ClassPathXmlApplicationContext context;
        context = new ClassPathXmlApplicationContext(new String[] {configFileName});
        context.start();
        return context;
    }

    private ClassPathXmlApplicationContext startProvider(String dir) {
        String configFileName = "conf/feature-test/"+dir+"/service-context.xml";
        return startContext(configFileName);
    }

    private ListOfBooks searchFor(String authorLastName, Library client) throws SeekBookError {
        SearchFor request = new SearchFor();
        request.getAuthorLastName().add(authorLastName);
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

        serviceContext = startProvider(dir);

        Library client = (Library)serviceContext.getBean("libraryHttp");

        ListOfBooks response = null;

        try {
            response = searchFor(searchFor, client);
        } catch (SeekBookError e) {
            fail("Exception during service call");
        }

        assertEquals("Books amount in response differs from 1", 1, booksInResponse(response));
        assertEquals("Received unexpected author name", expectedResult, authorLastName(response));
    }


    @After
    public void closeContextAfterEachTest() {
        serviceContext.stop();
        serviceContext.close();
        serviceContext = null;
    }


    @Test
    public void testConsumerRequest() {
        commonTest("consumer-request", "Panda", "Icebear");
    }

    @Test
    public void testConsumerResponse() {
         commonTest("consumer-response", "Icebear", "Grizzly");

    }

    @Test
    public void testConsumerAll() {
         commonTest("consumer-all", "Panda", "Panda");
    }

    @Test
    public void testConsumerNone() {
         commonTest("consumer-none", "Icebear", "Icebear");
    }

    @Test
    public void testProviderRequest() {
         commonTest("provider-request", "Panda", "Icebear");
    }

    @Test
    public void testProviderResponse() {
         commonTest("provider-response", "Icebear", "Panda");
    }

    @Test
    public void testProviderAll() {
         commonTest("provider-all", "Panda", "Panda");
    }

    @Test
    public void testProviderNone() {
         commonTest("provider-none", "Icebear", "Icebear");
    }

    @Test
    public void testAlwaysRequest() {
         commonTest("always-request", "Icebear", "Icebear");
    }

    @Test
    public void testAlwaysResponse() {
         commonTest("always-response", "Icebear", "Grizzly");
    }

    @Test
    public void testAlwaysAll() {
         commonTest("always-all", "Icebear", "Icebear");
    }

    @Test
    public void testAlwaysNone() {
         commonTest("always-none", "Icebear", "Icebear");
    }

    @Test
    public void testNoneRequest() {
         commonTest("none-request", "Icebear", "Icebear");
    }

    @Test
    public void testNoneResponse() {
         commonTest("none-response", "Icebear", "Icebear");
    }

    @Test
    public void testNoneAll() {
         commonTest("none-all", "Icebear", "Icebear");
    }

    @Test
    public void testNoneNone() {
         commonTest("none-none", "Icebear", "Icebear");
    }
}
