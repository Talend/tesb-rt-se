package org.talend.esb.policy.schemavalidate.tests.common;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public abstract class AbstractTest {

	public static String[] SERVER_APP_CTX_LOCATION = 
			new String[]{"classpath:spring/server/server-applicationContext.xml"};
	
	public static String VALID_CUSTOMER_NAME = "Smith";
	
	public static String NOT_VALID_CUSTOMER_NAME = "Not Valid Customer Name";
	
	public static String VALID_SCHEMA_RELATIVE_PATH = "schema/valid.xsd";
	
	public static String NOT_VALID_SCHEMA_RELATIVE_PATH = "schema/notValid.xsd";
	
	public static String VALID_SCHEMA_ABSOLUTE_PATH = AbstractTest.class.getClassLoader().getResource(VALID_SCHEMA_RELATIVE_PATH).getPath();
	
	public static String NOT_EXISTING_SCHEMA_RELATIVE_PATH = VALID_SCHEMA_RELATIVE_PATH + "doesNotExist";
	
	public static String NOT_EXISTING_SCHEMA_ABSOLUTE_PATH = VALID_SCHEMA_ABSOLUTE_PATH + "doesNotExist";
	
	protected ClassPathXmlApplicationContext clientCtxt;
	
	public AbstractTest(){
	}
	
	@BeforeClass
	public static void setUp(){
	}
   
	@AfterClass
	public static void shutdown(){
	}
	
}
