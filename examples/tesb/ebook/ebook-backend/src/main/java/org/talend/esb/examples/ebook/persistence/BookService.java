package org.talend.esb.examples.ebook.persistence;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.esb.examples.ebook.model.Book;
import org.talend.esb.examples.ebook.model.BookRepository;
import org.talend.esb.examples.ebook.model.Format;

@Produces({"application/json", "test/xml"})
@Singleton
@Transactional(value=TxType.SUPPORTS)
public class BookService {
    Logger LOG = LoggerFactory.getLogger(BookService.class);
    
    @Inject
    private BookRepository bookRepo;
    
    @GET
    @Path("{id}")
    public Response getBook(@PathParam("id") String id) {
        System.out.println(id);
        Book book = bookRepo.getBook(id);
        return book == null ? Response.status(Status.NOT_FOUND).build() : Response.ok(book).build();
    }

    @GET
    public Collection<Book> getBooks() {
        List<Book> outBooks = new ArrayList<>();
        for (Book book : bookRepo.getBooks()) {
            Book outBook = new Book();
            outBook.setId(book.getId());
            outBook.setTitle(book.getTitle());
            outBook.setCreator(book.getCreator());
            outBooks.add(outBook);
        }
        return outBooks;
    }
    
    @POST
    @Path("{id}")
    public Response sendBook(@PathParam("id") String id) {
        String recipient = "root@localhost";
        LOG.info("Sending book {} to {}.", id, recipient );
        Book book = bookRepo.getBook(id);
        Format format = getMobiFormat(book);
        if (format == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        URI uri = format.getFile();
        new MailSender().send(recipient, uri);
        LOG.info("Mail sent successfully");
        return Response.ok().build();
    }

    private Format getMobiFormat(Book book) {
        for (Format format : book.getFormats()) {
            if ("application/x-mobipocket-ebook".equals(format.getMediaType())) {
                return format;
            }
        }
        return null;
    }
}
