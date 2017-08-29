package org.talend.esb.policy.samenabling.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.services.test.library._1_0.Library;
import org.talend.services.test.library._1_0.SeekBookError;
import org.talend.types.test.library.common._1.ListOfBooks;
import org.talend.types.test.library.common._1.SearchFor;

public class SamEnablingFeatureTest {

	private static final Logger LOG = Logger
			.getLogger(SamEnablingFeatureTest.class.getName());

	@Before
	public void init() {
		LOG.setLevel(Level.FINE);
	}

	private ClassPathXmlApplicationContext serviceContext;

	private ClassPathXmlApplicationContext startContext(String configFileName) {
		ClassPathXmlApplicationContext context;
		context = new ClassPathXmlApplicationContext(
				new String[] { configFileName });
		context.start();
		return context;
	}

	private ClassPathXmlApplicationContext startProvider(String dir) {
		String configFileName = "conf/feature-test/" + dir
				+ "/service-context.xml";
		return startContext(configFileName);
	}

	private ListOfBooks searchFor(String authorLastName, String isbn,
			Library client) throws SeekBookError {
		SearchFor request = new SearchFor();
		request.getAuthorLastName().add(authorLastName);
		request.setISBNNumber(isbn);
		return client.seekBook(request);
	}

	private int booksInResponse(ListOfBooks response) {
		return response.getBook().size();
	}

	private String authorLastName(ListOfBooks response) {
		return response.getBook().get(0).getAuthor().get(0).getLastName();
	}

	private void commonTest(String testName, String searchFor, String isbn,
			String expectedResult) {

		final String dir = testName;

		serviceContext = startProvider(dir);

		Library client = (Library) serviceContext.getBean("libraryHttp");

		ListOfBooks response = null;

		try {
			response = searchFor(searchFor, isbn, client);
		} catch (SeekBookError e) {
			fail("Exception during service call");
		}

		assertEquals("Books amount in response differs from 1", 1,
				booksInResponse(response));
		assertEquals("Received unexpected author name", expectedResult,
				authorLastName(response));
	}

	@After
	public void closeContextAfterEachTest() {
		if (serviceContext != null) {
			serviceContext.stop();
			serviceContext.close();
			serviceContext = null;
		}
	}

	@Test
	public void testAlways() {
		commonTest("always", "Icebear", "123", "Icebear");
	}
}
