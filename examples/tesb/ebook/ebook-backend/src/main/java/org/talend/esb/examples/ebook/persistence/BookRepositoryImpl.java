package org.talend.esb.examples.ebook.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.Transactional;

import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.esb.examples.ebook.model.Book;
import org.talend.esb.examples.ebook.model.BookRepository;
import org.talend.esb.examples.ebook.model.Format;
import org.talend.esb.examples.ebook.model.Subject;

@OsgiServiceProvider(classes = {BookRepository.class})
@Singleton
@Transactional
public class BookRepositoryImpl implements BookRepository {
    Logger LOG = LoggerFactory.getLogger(BookRepositoryImpl.class);
    
    @PersistenceContext(unitName="ebook")
    EntityManager em;

    @Override
    public void add(Book book) {
        if (getBook(book.getId()) != null) {
            LOG.info("Ignoring book {} as it already exists.", book.getId());
            return;
        }

        for (Format fomat : book.getFormats()) {
            em.persist(fomat);
        }
        persistSubjects(book); 
        em.persist(book);
        if ("error1".equals(book.getTitle())) {
            throw new RuntimeException("Test for error handling. Should cause tx rollback");
        }
    }

    private void persistSubjects(Book book) {
        List<Subject> outSubjects = new ArrayList<Subject>();
        for (Subject subject : book.getSubjects()) {
            Subject outSubject = em.find(Subject.class, subject.getSubject());
            if (outSubject == null) {
                em.persist(subject);
                outSubject = subject;
            }
            outSubjects.add(outSubject);
            book.setSubjects(outSubjects);
            try {
                em.persist(subject);
            } catch (Exception e) {
                em.merge(subject);
            }
        }
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Book getBook(String id) {
        return em.find(Book.class, id);
    }
    
    @Override
    public List<Book> getBooks() {
        CriteriaQuery<Book> query = em.getCriteriaBuilder().createQuery(Book.class);
        return em.createQuery(query.select(query.from(Book.class))).getResultList();
    }
    
    @Override
    public void update(Book book) {
        em.merge(book);
    }
    
    @Override
    public void delete(String id) {
        Book book = getBook(id);
        if (book != null) {
            em.remove(book);
        }
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }
}
