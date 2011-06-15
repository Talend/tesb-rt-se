/*
 * Copyright (C) 2011, Talend Inc. – www.talend.com
 * This file is part of Talend ESB
 *
 * Talend ESB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * Talend ESB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Talend ESB.  If not, see <http://www.gnu.org/licenses/>.
 */ 
package org.talend.esb.examples;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class SimpleClient {

	public SimpleClient() {
	}

	public static void main(String[] args) throws Exception {

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "META-INF/spring/client-bean.xml" });

		SimpleService client = (SimpleService) context.getBean("simpleClient");

		for (int i = 0; i < 100; i++) {
			String response = client.sayHi("Alex");
			System.out.println(response);
		}

		for (int i = 1; i < 6; i++) {
			int result = client.doubleIt(i);
			System.out.println(result);
		}

		// "Incorrect name" exception would be thrown
		String response = client.sayHi("Joe");
		System.out.println(response);

		System.exit(0);
	}

}
