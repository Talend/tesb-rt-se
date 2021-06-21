/*
 * #%L
 * Talend :: ESB :: Job :: Controller
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
package org.talend.esb.job.controller.internal.util;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.junit.Test;

public class DOM4JMarshallerTest {

    private static final String XML = "<RequestResponseRequest xmlns='http://www.talend.org/service/'><in xmlns:ns2='http://www.talend.org/service/' xmlns=''>s_sl_test</in></RequestResponseRequest>";

    @Test
    public void documentToSource() throws Exception {
        SAXReader reader = new SAXReader();
        Document doc = reader.read(new ByteArrayInputStream(XML.getBytes()));

        Source source = DOM4JMarshaller.documentToSource(doc);

        StringWriter writer = new StringWriter();
        TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(writer));

        // TESB-5237
        assertTrue(writer.toString().contains("xmlns=\"\""));

        // TESB-12665
        source = DOM4JMarshaller.documentToSource(doc);
        TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(new StringWriter()));
    }
}
