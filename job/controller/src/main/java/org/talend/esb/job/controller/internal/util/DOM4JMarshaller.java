/*
 * #%L
 * Talend :: ESB :: Job :: Controller
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
package org.talend.esb.job.controller.internal.util;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.dom4j.DocumentException;
import org.dom4j.io.DOMWriter;

public final class DOM4JMarshaller {

	private static final javax.xml.transform.TransformerFactory FACTORY = javax.xml.transform.TransformerFactory
			.newInstance();

	private DOM4JMarshaller() {

	}

	public static org.dom4j.Document sourceToDocument(Source source)
			throws TransformerException, DocumentException {

		// org.dom4j.io.DocumentResult docResult = new
		// org.dom4j.io.DocumentResult();
		// FACTORY.newTransformer().transform(source, docResult);
		// return docResult.getDocument();
		//

		// fix for unsupported xmlns="" declaration processing over dom4j
		// implementation
		// // old version:
		// // org.dom4j.io.DocumentResult docResult = new
		// org.dom4j.io.DocumentResult();
		// // factory.newTransformer().transform(request, docResult);
		// // org.dom4j.Document requestDoc = docResult.getDocument();
		// new version:
		java.io.ByteArrayOutputStream os = new java.io.ByteArrayOutputStream();
		FACTORY.newTransformer().transform(source,
				new javax.xml.transform.stream.StreamResult(os));
		return new org.dom4j.io.SAXReader()
				.read(new java.io.ByteArrayInputStream(os.toByteArray()));
		// end of fix

	}

	public static Source documentToSource(org.dom4j.Document document)
			throws org.dom4j.DocumentException {
	    return new DOMSource(new DOMWriter().write(document));
	}

}
