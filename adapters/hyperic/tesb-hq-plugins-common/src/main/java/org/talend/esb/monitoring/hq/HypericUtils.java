/*
 * #%L
 * Talend ESB :: Adapters :: HQ :: Common
 * %%
 * Copyright (C) 2011 - 2013 Talend Inc.
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

import org.apache.commons.logging.Log;
import org.hyperic.hq.product.GenericPlugin;
import org.hyperic.hq.product.PluginException;

public final class HypericUtils {

	public static String getMandatoryProperty(final GenericPlugin plugin,
			final String propName) throws PluginException {
		final String result = plugin.getTypeProperty(plugin.getTypeInfo(), propName);
		if (result == null) {
			throw new PluginException("Mandatory property '" + propName + "' is not set.");
		}
		return result;
	}
	
	public static String getOptionalProperty(final GenericPlugin plugin,
			final String propName) {
		return plugin.getTypeProperty(plugin.getTypeInfo(), propName);
	}

	// logging helper method, to prevent unnecessary string concatenation
	public static void logInfo(final Log log, final Object o1, final Object... info) {
		if (!log.isInfoEnabled()) {
			return;
		}

		if (info.length == 0) {
			log.info(o1.toString());
			return;
		}

		final StringBuilder sb = new StringBuilder(o1.toString());
		for (final Object o : info) {
			sb.append(o.toString());
		}

		log.info(sb.toString());
	}

	private HypericUtils() {
	}
}
