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

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface SimpleService {

	@WebMethod
	String sayHi(String name);

	@WebMethod
	int doubleIt(int arg);
}
