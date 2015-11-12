package org.talend.esb.examples.ebook.itest;

import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

import javax.inject.Inject;
import javax.transaction.UserTransaction;

import org.hibernate.LazyInitializationException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.service.coordinator.Coordination;
import org.osgi.service.coordinator.Coordinator;
import org.talend.esb.examples.ebook.model.Book;
import org.talend.esb.examples.ebook.model.BookRepository;
import org.talend.esb.examples.ebook.model.Format;

@Ignore
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class BookRepositoryTest extends AbstractJPAItest {
    @Inject
    BookRepository bookRepository;
    
    @Inject
    UserTransaction ut;
    
    @Inject
    Coordinator coordinator;
    
    /**
     * Shows adding and removing a book with implicit XA transactions inside BookRepository
     */
    @Test
    public void testAddThenDelete() {
        Book book = createBook("My title");
        bookRepository.add(book);
        Book book2 = bookRepository.getBook(book.getId());
        Assert.assertEquals(book.getId(), book2.getId());
        Assert.assertEquals(book.getTitle(), book2.getTitle());
        bookRepository.delete(book.getId());
        Book book3 = bookRepository.getBook(book.getId());
        Assert.assertNull(book3);
    }

    /**
     * Shows that bookRepository can take part in an outer transaction
     */
    @Test
    public void testTransactionRollback() throws Exception {
        ut.begin();
        Book book = createBook("My title");
        bookRepository.add(book);
        ut.rollback();
        Book book2 = bookRepository.getBook(book.getId());
        Assert.assertNull(book2);
    }
    
    /**
     * Test default EntityManager life cycle ends at borders of BookRepository
     */
    @Test(expected = LazyInitializationException.class)
    public void testEMLifecycle() throws Exception {
        Book book = createBook("My title");
        try {
            bookRepository.add(book);
            Book book2 = bookRepository.getBook(book.getId());
            book2.getFormats().get(0);
        } finally {
            bookRepository.delete(book.getId());
        }
    }
    
    /**
     * Test EntityManager life cycle can be extended by outer Coordination 
     */
    @Test
    public void testEMLifecycleWithCoordination() throws Exception {
        Coordination coordination = coordinator.begin("coord", 10000);
        Book book = createBook("My title");
        try {
            bookRepository.add(book);
            Book book2 = bookRepository.getBook(book.getId());
            Format format = book2.getFormats().get(0);
            Assert.assertEquals(book.getFormats().get(0).getFile().toString(), format.getFile().toString());
        } finally {
            bookRepository.delete(book.getId());
            coordination.end();
        }
    }
    
    @Configuration
    public Option[] getConfiguration() {
        return new Option[] {
            baseOptions(),
            features(ebooksFeatures, "example-ebook-backend")
        };
    }
}
