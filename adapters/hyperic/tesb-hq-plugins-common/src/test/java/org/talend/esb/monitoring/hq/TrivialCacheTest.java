/*
 * #%L
 * Talend ESB :: Adapters :: HQ :: Common
 * %%
 * Copyright (C) 2011 - 2012 Talend Inc.
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
package org.talend.esb.monitoring.hq;

import org.junit.Test;
import org.talend.esb.monitoring.hq.DynamicMxFieldMeasurementPlugin.TrivialCache;

import static org.junit.Assert.*;

public class TrivialCacheTest {
	
	private static final String key1 = "aaa";
	private static final String val1 = "bbb";
	private static final String key2 = "cccc";
	private static final String val2 = "dddd";

	@Test
	public void testBasic() {
		TrivialCache tc = new TrivialCache();
		
		assertEquals(0, tc.size());
		
		tc.put(key1, val1);
		
		assertEquals(1, tc.size());
		assertEquals(val1, tc.get(key1));
		assertNull(tc.get(key2));
		
		tc.put(key2, val2);
		
		assertEquals(2, tc.size());
		assertEquals(val2, tc.get(key2));
		
		tc.invalidate(key2);
		
		assertEquals(1, tc.size());
		assertEquals(val1, tc.get(key1));
		assertNull(tc.get(key2));
	}
	
	@Test
	public void testGc() throws Exception {
		TrivialCache tc = new TrivialCache();
		
		tc.put(key1, val1);
		tc.put(key2, val2);
		
		tc.scheduleGc(1000);
		
		Thread.sleep(1050);
		
		tc.get(key1);
		
		Thread.sleep(1100);
		
		tc.stopGc();
		
		assertEquals(1, tc.size());
		assertEquals(val1, tc.get(key1));
		assertNull(tc.get(key2));
	}
}
