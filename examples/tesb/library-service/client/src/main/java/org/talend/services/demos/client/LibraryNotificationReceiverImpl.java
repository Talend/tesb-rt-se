package org.talend.services.demos.client;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceProvider;

import org.talend.services.demos.common.Utils;
import org.talend.services.demos.library._1_0.Library;
import org.talend.services.demos.library._1_0.SeekBookError;
import org.talend.types.demos.library.common._1.BookType;
import org.talend.types.demos.library.common._1.ListOfBooks;
import org.talend.types.demos.library.common._1.SearchFor;

@WebServiceProvider
public class LibraryNotificationReceiverImpl implements Library {

    @Resource
    private WebServiceContext wsContext;

	@Override
	public void createLending(String isbnNumber, Date dateOfBirth, String zip,
			Date borrowed) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListOfBooks seekBook(SearchFor body) throws SeekBookError {
		throw new UnsupportedOperationException();
	}

	@Override
	public void seekBookInBasement(SearchFor body) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void newBooks(Date listDate, List<BookType> book) {
    	System.out.println("***************************************************************");          
        System.out.println("*** newBooks notification is received *************************");
        System.out.println("***************************************************************"); 
        
		System.out.println("New books notification:");
		
		showNewBooks(listDate, book);
	}

	private void showNewBooks(final Date listDate, final List<BookType> response ){
    	System.out.println("New books from " + DateFormat.getDateInstance().format(listDate));
    	final ListOfBooks books = new ListOfBooks();
    	books.getBook().addAll(response);
    	Utils.showBooks(books);
    }    
    
}
