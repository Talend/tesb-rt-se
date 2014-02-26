package org.talend.services.demos.server;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.talend.services.demos.library._1_0.Library;
import org.talend.services.demos.library._1_0.SeekBookError;
import org.talend.types.demos.generalobjects.errorhandling._1.ExceptionFrame;
import org.talend.types.demos.generalobjects.errorhandling._1.ExceptionType;
import org.talend.types.demos.library.common._1.BookType;
import org.talend.types.demos.library.common._1.ListOfBooks;
import org.talend.types.demos.library.common._1.PersonType;
import org.talend.types.demos.library.common._1.SearchFor;

public class LibraryServerImpl implements Library {

	@Override
	public void createLending(String isbnNumber, Date dateOfBirth, String zip,
			Date borrowed) {
		System.out.println("Lending request:");
		System.out.print("ISBN number: ");
		System.out.println(isbnNumber);
	}

	@Override
	public ListOfBooks seekBook(SearchFor body) throws SeekBookError {
		List<String> authorsLastNames = body.getAuthorLastName();
		if (authorsLastNames != null && authorsLastNames.size() > 0) {
			String authorsLastName = authorsLastNames.get(0);
			if (authorsLastName != null && authorsLastName.length() > 0 &&
					!"Icebear".equalsIgnoreCase(authorsLastName)) {
				SeekBookError e = prepareException("No book available from author " + authorsLastName);
				throw e;
			}
		}
		ListOfBooks result = new ListOfBooks();
		BookType book = new BookType();
		result.getBook().add(book);
		PersonType author = new PersonType();
		book.getAuthor().add(author);
		author.setFirstName("Jack");
		author.setLastName("Icebear");
		Calendar dateOfBirth = new GregorianCalendar(101, Calendar.JANUARY, 2);
		author.setDateOfBirth(dateOfBirth.getTime());
		book.getTitle().add("Survival in the Arctic");
		book.getPublisher().add("Frosty Edition");
		book.setYearPublished("2010");
		return result;
	}

	private SeekBookError prepareException(String message) {
		ExceptionType exception = new ExceptionType();
		exception.setOperation("seekBook");
		exception.setServiceName("LibraryService");				
		exception.setExceptionText(message);
		ExceptionFrame frame = new ExceptionFrame();
		frame.getException().add(exception);
		SeekBookError e = new SeekBookError("Book not found", frame);
		return e;
	}
}
