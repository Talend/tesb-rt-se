# Example eBook (XA Transactions with JPA and JMS)

This example implements a backend and UI showing the ebooks of the Gutenberg project.
It allows to import the whole gutenberg index using a camel route.

You can [download the full index of project gutenberg](http://gutenberg.readingroo.ms/cache/generated/feeds/rdf-files.tar.bz2 "Index download")

The Archive contains one file per book with meta data in rdf Format.

# Install

Start the standalone ActiveMQ

    activemq/bin/<OS-env>/activemq[.bat]

Unpack and start TESB

    feature:repo-add mvn:org.talend.esb.examples.ebook/ebook-features/{version}/xml
    feature:install example-ebook-backend example-ebook-importer example-ebook-ui

Then put some rdf files from the gutenberg index into the directory gutenberg below your karaf installation.
The book data will automatically imported into the database.

You should see one line in the log per imported book.

The take a look at the [Ebook UI](http://localhost:8040/ebook/). It should show a list of Books that can be selected.
On selection it shows the available formats and allows to send the Kindle format to an EMail address.

# Modules

1.  Importer for the Gutenberg Index
2.  Database backend offering the Library service using Aries JPA
3.  UI to browse and view the books and to send books to an ebook reader
 
## Importer

Allows to import the complete ebook index of project Gutenberg into a database.

The first route watches the directory "gutenberg". Each file that is found is parsed into a Book class and then dumped into jms as XML. 

The second route listen to the jms queue and processes each message using an XA transaction. The xml is unmarshalled into a Book object again and given and stored using the BookRepository service. If an error happens then the db changes are rolled back and the jms message is put back into the queue.

The error handling and retries are configured using an ActiveMQ RedeliveryPolicy in ebook-connectionfctory

## ConnectionFactory

Contains an XA ready connection factory with pooling and a RedeliveryPolicy. 

## Backend and REST

* BookRepository OSGi service to manage and browse the ebook index. Inside it uses blueprint and Aries JPA.
The service is written using CDI Annotations. The blueprint xml is generated at build time.

* BookService REST resource for the UI and potentially also for other applications
The rest service also allows to send ebooks to an email address. This can be used to send to an Amazon kindle reader. 

## UI

Allows to browse the book index, select books and send to an email address. it is implemented using Angular JS
