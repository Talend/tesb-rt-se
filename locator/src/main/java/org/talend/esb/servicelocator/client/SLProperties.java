package org.talend.esb.servicelocator.client;

import java.io.Serializable;
import java.util.Collection;

/*
 * #%L
 * Service Locator Client for CXF
 * %%
 * Copyright (C) 2011 Talend Inc.
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
public interface SLProperties extends Serializable {

	Collection<? extends String> getPropertyNames();
	
	boolean hasProperty(String name);

	String getValue(String name);

	Collection<? extends String> getValues(String name);

	boolean includesValues(String name, String... values);

}