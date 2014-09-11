package org.talend.esb.policy.transformation.test.internal;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;

import org.springframework.beans.factory.InitializingBean;
import org.talend.services.test.library._1_0.Library;
import org.talend.services.test.library._1_0.SeekBookError;
import org.talend.types.test.generalobjects.errorhandling._1.ExceptionFrame;
import org.talend.types.test.generalobjects.errorhandling._1.ExceptionType;
import org.talend.types.test.library.common._1.BookType;
import org.talend.types.test.library.common._1.ListOfBooks;
import org.talend.types.test.library.common._1.PersonType;
import org.talend.types.test.library.common._1.SearchFor;

public class LibraryServerImpl implements Library, InitializingBean {

    @Resource
    private WebServiceContext wsContext;


    @Override
    public ListOfBooks seekBook(SearchFor body) throws SeekBookError {

        System.out.println("***************************************************************");
        System.out.println("*** seekBook request (Request-Response operation) is received *");
        System.out.println("***************************************************************");

        showSeekBookRequest(body);

        List<String> authorsLastNames = body.getAuthorLastName();
        if (authorsLastNames != null && authorsLastNames.size() > 0) {
            String authorsLastName = authorsLastNames.get(0);
            if (authorsLastName != null && authorsLastName.length() > 0 &&
                    !"Icebear".equalsIgnoreCase(authorsLastName)) {
                SeekBookError e = prepareException("No book available from author " + authorsLastName);

                System.out.println("No book available from author " +  authorsLastName);
                System.out.println("\nSending business fault (SeekBook error) with parameters:");

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

        System.out.println("Book(s) is found:");

        showSeekBookResponse(result);

        return result;
    }


    private void showSeekBookRequest(final SearchFor request){
        if(request == null){
            System.out.println("Request body is empty");
            return;
        }
        System.out.println("Autors last name in request: " );
        List<String> authorLastName = request.getAuthorLastName();
        for (String name : authorLastName) {
            System.out.println(name);
        }
    }

    private void showSeekBookResponse(final ListOfBooks response ){

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

    @Override
    public void afterPropertiesSet() throws Exception {
        //System.out.println("Library Server is ready");
    }
}
