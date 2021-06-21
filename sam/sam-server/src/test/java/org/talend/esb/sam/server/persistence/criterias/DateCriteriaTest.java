/*
 * #%L
 * Service Activity Monitoring :: Server
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
package org.talend.esb.sam.service.criterias;

import org.talend.esb.sam.server.persistence.criterias.Criteria;
import org.talend.esb.sam.server.persistence.criterias.DateCriteria;

import junit.framework.TestCase;


public class DateCriteriaTest extends TestCase {

	public void testCriteria() throws Exception {
		DateCriteria criteria = new DateCriteria("timestamp_before", "TIMESTAMP");
		Criteria value = criteria.parseValue("1307570400000")[0];
		assertEquals("TIMESTAMP < :timestamp_before", value.getFilterClause().toString());

		criteria = new DateCriteria("timestamp_after", "TIMESTAMP");
		value = criteria.parseValue("1307570400000")[0];
		assertEquals("TIMESTAMP > :timestamp_after", value.getFilterClause().toString());

		criteria = new DateCriteria("timestamp", "TIMESTAMP");
		value = criteria.parseValue("1307570400000")[0];
		assertEquals("TIMESTAMP = :timestamp", value.getFilterClause().toString());
	}

	public void testOnDayCriteria() throws Exception {
		DateCriteria criteria = new DateCriteria("timestamp_on", "TIMESTAMP");
		Criteria[] values = criteria.parseValue("1307570400000");
		assertEquals("TIMESTAMP > :timestamp_on_after", values[0].getFilterClause().toString());
		assertEquals("TIMESTAMP < :timestamp_on_before", values[1].getFilterClause().toString());
	}


}
