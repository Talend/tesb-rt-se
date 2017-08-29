package org.talend.esb.policy.compression.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.message.Message;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.talend.esb.policy.compression.feature.CompressionFeature;
import org.talend.esb.policy.compression.impl.CompressionInInterceptor;
import org.talend.esb.policy.compression.impl.CompressionOutInterceptor;
import org.talend.services.test.library._1_0.Library;
import org.talend.services.test.library._1_0.SeekBookError;
import org.talend.types.test.library.common._1.ListOfBooks;
import org.talend.types.test.library.common._1.SearchFor;

public class CompressionFeatureTest {

	private static final Logger LOG = Logger.getLogger(CompressionFeature.class
			.getName());

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
	public void testConsumerRequest() {
		commonTest("consumer-request", "Icebear", generateString(1000),
				"Icebear");
	}

	@Test
	public void testConsumerResponse() {
		commonTest("consumer-request", "Icebear", generateString(1000),
				"Icebear");
	}

	@Test
	public void testLogging() {
		Level l = LOG.getLevel();
		LOG.setLevel(Level.FINE);
		commonTest("consumer-request", "Icebear", generateString(1000),
				"Icebear");
		LOG.setLevel(l);

	}

	@Test
	public void removeInterceptors() {

		final List<Interceptor<? extends Message>> outInterceptors = new ArrayList<Interceptor<? extends Message>>();
		outInterceptors.add(new CompressionOutInterceptor());
		outInterceptors.add(new CompressionOutInterceptor());

		final List<Interceptor<? extends Message>> outFaultInterceptors = new ArrayList<Interceptor<? extends Message>>();
		outFaultInterceptors.add(new CompressionOutInterceptor());
		outFaultInterceptors.add(new CompressionOutInterceptor());

		final List<Interceptor<? extends Message>> inInterceptors = new ArrayList<Interceptor<? extends Message>>();
		inInterceptors.add(new CompressionInInterceptor());

		final List<Interceptor<? extends Message>> inFaultInterceptors = new ArrayList<Interceptor<? extends Message>>();
		inFaultInterceptors.add(new CompressionInInterceptor());

		InterceptorProvider interceptorProvider = new InterceptorProvider() {

			@Override
			public List<Interceptor<? extends Message>> getOutInterceptors() {
				return outInterceptors;
			}

			@Override
			public List<Interceptor<? extends Message>> getOutFaultInterceptors() {
				return outFaultInterceptors;
			}

			@Override
			public List<Interceptor<? extends Message>> getInInterceptors() {
				return inInterceptors;
			}

			@Override
			public List<Interceptor<? extends Message>> getInFaultInterceptors() {
				return inFaultInterceptors;
			}
		};

		CompressionFeature f = new CompressionFeature();
		f.initialize(interceptorProvider, null);

		assertTrue(outInterceptors.size() == 1);
		assertTrue(outFaultInterceptors.size() == 1);

		assertTrue(inInterceptors.size() == 2);
		assertTrue(inFaultInterceptors.size() == 1);

	}

	public String generateString(int size) {
		char[] chars = new char[size];
		Arrays.fill(chars, '*');
		return new String(chars);
	}

}
