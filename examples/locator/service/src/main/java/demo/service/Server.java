/*******************************************************************************
*
* Copyright (c) 2011 Talend Inc. - www.talend.com
* All rights reserved.
*
* This program and the accompanying materials are made available
* under the terms of the Apache License v2.0
* which accompanies this distribution, and is available at
* http://www.apache.org/licenses/LICENSE-2.0
*
*******************************************************************************/

package demo.service;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Server {

	public static void main(String args[]) throws Exception {
		new ClassPathXmlApplicationContext(new String[] {"/META-INF/spring/beans.xml"});
		
		Thread.sleep(30 * 60 * 1000);

		System.out.println("Server exiting");
		System.exit(0);
	}
}
