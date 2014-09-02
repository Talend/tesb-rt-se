package org.talend.services.demos.server;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.talend.services.demos.library._1_0.Library;
import org.talend.types.demos.library.common._1.BookType;
import org.talend.types.demos.library.common._1.PersonType;

public class LibraryPublisher {

	/** The Library proxy will be injected either by spring or by a direct call to the setter  */
	Library library;
    
    /**
     * Gets the library.
     *
     * @return the library
     */
    public Library getLibrary() {
        return library;
    }

    /**
     * Sets the library.
     *
     * @param library the new library
     */
    public void setLibrary(Library library) {
        this.library = library;
    }

    public void publishNewBooksNotifications() throws InterruptedException {
        for (int ndx = 1; ndx < 6; ndx++) {
            Thread.sleep(4000L);
            List<BookType> newBooks = new LinkedList<BookType>();
			BookType book = new BookType();
			newBooks.add(book);
			PersonType author = new PersonType();
			book.getAuthor().add(author);
			author.setFirstName("Jack");
			author.setLastName("Icebear");
			Calendar dateOfBirth = new GregorianCalendar(101, Calendar.JANUARY, 2);
			author.setDateOfBirth(dateOfBirth.getTime());
			book.getTitle().add("More About Survival in the Arctic - Volume " + ndx);
			book.getPublisher().add("Frosty Edition");
			book.setYearPublished("2011");
			System.out.println("Publishing notification about a new book:");
			System.out.println("Jack Icebear - More About Survival in the Arctic - Volume " + ndx);
			library.newBooks(new Date(), newBooks);
        }
    }
}
