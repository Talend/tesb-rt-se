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

import java.io.File;
import java.util.regex.Pattern;

import org.hyperic.hq.product.PluginException;
import org.hyperic.hq.product.PluginManager;

import static org.talend.esb.monitoring.hq.HypericUtils.getMandatoryProperty;

public class KarafEmbeddedServerDetector extends AbstractEmbeddedServerDetector {

	private String libraryPath;

	@Override
	public void init(PluginManager manager) throws PluginException {
		super.init(manager);
		libraryPath = getMandatoryProperty(this, TalendHqConstants.PROP_LIBRARY_PATH);
	}

	@Override
	protected File findVersionFile(final File dir, final Pattern pattern) {
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("This is not a directory: "
					+ dir.getAbsolutePath());
		}

		final File libraryDir = new File(dir, libraryPath);
		if (libraryDir.isDirectory()) {
			final File[] files = libraryDir.listFiles();
			if (files == null) {
				return null;
			}

			for (final File app : files) {
				if (app.isDirectory()) {
					final File versionFile = super
							.findVersionFile(app, pattern);
					if (versionFile != null) {
						return versionFile;
					}
				}
			}
		}

		return null;
	}
}
