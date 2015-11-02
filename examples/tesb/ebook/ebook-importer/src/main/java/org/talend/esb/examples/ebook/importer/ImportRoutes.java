package org.talend.esb.examples.ebook.importer;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.camel.builder.RouteBuilder;
import org.ops4j.pax.cdi.api.OsgiService;
import org.talend.esb.examples.ebook.model.BookRepository;
import org.talend.esb.examples.ebook.parser.BookParser;

@Singleton
public class ImportRoutes extends RouteBuilder {
    @OsgiService
    @Inject
    BookRepository bookRepo;

    @Override
    public void configure() throws Exception {
        from("file:gutenberg?recursive=true&noop=true")
            .bean(new BookParser())
            .marshal().jaxb(true)
            .to("jms:books.in");

        from("jms:books.in")
            .transacted()
            .bean(bookRepo, "add")
            .bean(new BookLogger());
    }

}
