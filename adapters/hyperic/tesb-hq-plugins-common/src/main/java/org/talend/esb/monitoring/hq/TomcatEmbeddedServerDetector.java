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
import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TomcatEmbeddedServerDetector extends
		AbstractEmbeddedServerDetector {

	private static final String WEBINF_LIB = "WEB-INF" + File.separator + "lib";

	@Override
	protected File findVersionFile(File dir, Pattern pattern) {
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("This is not a directory: "
					+ dir.getAbsolutePath());
		}

		final File tomcatLibResult = searchInDir(new File(dir, "lib"), pattern);
		if (tomcatLibResult != null) {
			return tomcatLibResult;
		}

		final File tomcatWebapps = new File(dir, "webapps");
		if (tomcatWebapps.isDirectory()) {
			final File[] files = tomcatWebapps.listFiles();
			if (files == null) {
				return null;
			}

			for (final File webapp : files) {
				if (webapp.isDirectory()) {
					final File result = searchInDir(
							new File(webapp, WEBINF_LIB), pattern);
					if (result != null) {
						return result;
					}
				} else if (webapp.getName().endsWith(".war")) {
					final File result = searchInWarFile(webapp, pattern);
					if (result != null) {
						return result;
					}
				}
			}
		}

		return null;
	}

	protected File searchInWarFile(final File warFile, final Pattern pattern) {
		ZipFile file = null;
		try {
			file = new ZipFile(warFile);
			final Enumeration<? extends ZipEntry> fileContent = file.entries();
			while (fileContent.hasMoreElements()) {
				final String fn = fileContent.nextElement().getName();
				if (versionFileMatches(fn, pattern)) {
					return new File(warFile + "!" + fn);
				}
			}
		} catch (IOException e) {
			// Ignoring this exception as it just means that we should keep looking
			// for the version file
			log.debug("Unable to check file '" + warFile + "'.", e);
		} finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException e) {
					log.debug("Unable to close file '" + warFile + "'.", e);
				}
			}
		}
		return null;
	}

	protected File searchInDir(final File libDir, final Pattern pattern) {
		if (libDir.isDirectory()) {
			final File result = super.findVersionFile(libDir, pattern);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
}
