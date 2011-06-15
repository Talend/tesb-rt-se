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

import javax.jws.WebService;

@WebService
public class SimpleServiceImpl implements SimpleService {

	@Override
	public String sayHi(String name) {

		if (name.equals("Joe"))
			throw new RuntimeException("Incorrect name");

		return "Hi " + name + "!";
	}

	@Override
	public int doubleIt(int arg) {
		return arg * 2;
	}

}
