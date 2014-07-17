package org.talend.services.demos.common;

import java.util.Date;
import java.util.List;

import org.talend.services.demos.library._1_0.SeekBookError;
import org.talend.types.demos.generalobjects.errorhandling._1.ExceptionFrame;
import org.talend.types.demos.generalobjects.errorhandling._1.ExceptionType;
import org.talend.types.demos.library.common._1.BookType;
import org.talend.types.demos.library.common._1.ListOfBooks;
import org.talend.types.demos.library.common._1.PersonType;

public class Utils {
	/**
	 * Show books.
	 *
	 * @param booksList the books list
	 */
	public static void showBooks(final ListOfBooks booksList){
   	
    	if(booksList != null && booksList.getBook() != null && !booksList.getBook().isEmpty()){
    		List<BookType> books = booksList.getBook();
    		System.out.println("\nNumber of books: " + books.size());
    		int cnt = 0;
    		for (BookType book : books) {
    			System.out.println("\nBookNum: " + (cnt++ + 1));
    			List<PersonType> authors = book.getAuthor();
    			if(authors != null && !authors.isEmpty()){
    				for (PersonType author : authors) {
    					System.out.println("Author:  " + author.getFirstName() + 
    										" " + author.getLastName());
					}
    			}
    			System.out.println("Title:   " + book.getTitle());
    			System.out.println("Year:    " + book.getYearPublished());
    			if(book.getISBN()!=null){
    				System.out.println("ISBN:    " + book.getISBN());
    			}
				
			}
    	}else{
    		System.out.println("List of books is empty");
    	}
    	System.out.println("");
	}
	
	public static void showSeekBookError(final SeekBookError e){
        System.out.println("Error message:  " + e.getMessage());
    	System.out.println("Exception text: " + e.getFaultInfo().
    			getException().get(0).getExceptionText() +"\n");
	}
	
	public static void showLendingRequest(final String isbnNumber, final Date dateOfBirth, final String zip,
			final Date borrowed){
		System.out.println("ISBN number: " + isbnNumber);
		System.out.println("Date of birth: " + dateOfBirth);
		System.out.println("ZIP: " + zip);
		System.out.println("Borrowed: " + borrowed);
	}

    public static void showExceptionFrame(ExceptionFrame exceptionFrame) {
        ExceptionType type = exceptionFrame.getException().get(0);
        if (type != null && type.getExceptionText() != null) {
            System.out.print("Error message is: " + type.getExceptionText());
        }
    }
}
