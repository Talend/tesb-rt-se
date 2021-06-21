/*
 * #%L
 * Service Activity Monitoring :: Common
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.talend.esb.sam.common.handler.impl;

import java.util.ArrayList;

import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.common.handler.impl.PasswordHandler;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PasswordHandlerTest extends TestCase {

	private Event event;
	private PasswordHandler passwordHandler;
	private static final String EXPECTED = "<replaced xmlns=\"\"/>";

	public PasswordHandlerTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(PasswordHandlerTest.class);
	}

	/**
	 * Test will execute a list of password tags and validate the replacement
	 */
	public void testPasswordReplacement() {
		String expectedAll = "";
		for (int i = 0; i < 2; i++) {
			expectedAll = expectedAll + EXPECTED;
		}

		ArrayList<String> testStrings = new ArrayList<String>();
		testStrings.add("<password>x</password><password>x</password>");
		testStrings
				.add("<tns:password>x</tns:password><tns:password>x</tns:password>");
		testStrings
				.add("< tns:password >x< /tns:password >< tns:password >x< /tns:password >");
		testStrings.add("<password value='x'/><password value='x'/>");
		testStrings
				.add("<password value='x'>x</password><password value='x'>x</password>");

		for (String testString : testStrings) {
			event.setContent(testString);
			passwordHandler.handleEvent(event);
			assertEquals(event.getContent(), expectedAll);
		}
	}

	public void testMultiline() {
		StringBuilder builder = new StringBuilder();
		builder.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:data=\"http://www.example.com/data\">\n");
		builder.append("   <soapenv:Header/>\n");
		builder.append("   <soapenv:Body>\n");
		builder.append("      <data:addCase>\n");
		builder.append("         <!-- ---replaced--- -->\n");
		builder.append("         <data:description>ok</data:description>\n");
		builder.append("		         <data:amountClaimed>789</data:amountClaimed>\n");
		builder.append("         <data:guiltyParty>contractor</data:guiltyParty>\n");
		builder.append("      </data:addCase>\n");
		builder.append("   </soapenv:Body>\n");
		builder.append("</soapenv:Envelope>\n");

		StringBuilder builder2 = new StringBuilder();
		builder2.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:data=\"http://www.example.com/data\">\n");
		builder2.append("   <soapenv:Header/>\n");
		builder2.append("   <soapenv:Body>\n");
		builder2.append("      <data:addCase>\n");
		builder2.append("         <!-- ---replaced--- -->\n");
		builder2.append("         <data:description>ok</data:description>\n");
		builder2.append("		         <data:amountClaimed>789</data:amountClaimed>\n");
		builder2.append("         <data:guiltyParty>contractor</data:guiltyParty>\n");
		builder2.append("      </data:addCase>\n");
		builder2.append("   </soapenv:Body>\n");
		builder2.append("</soapenv:Envelope>\n");

		event.setContent(builder.toString());
		passwordHandler.handleEvent(event);
		assertEquals(event.getContent(), builder2.toString());
		return;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ArrayList<String> list = new ArrayList<String>();
		list.add("password");
		list.add("customer");

		passwordHandler = new PasswordHandler();
		passwordHandler.setTagnames(list);

		event = new Event();
	}
}
