
# Example eBook (XA Transactions with JPA and JMS)

This example implements a backend and UI showing the ebooks of the Gutenberg project.
It allows to import the whole gutenberg index using a camel route.

# Install

    feature:repo-add mvn:org.talend.esb.examples.ebook/ebook-features/6.1.0-SNAPSHOT/xml
    feature:install -v example-ebook-backend example-ebook-importer example-ebook-ui

Then create a directory gutenberg below the karaf directory and put some rdf files from the gutenberg index into it.
The book data will automatically imported into the database.

## Modules

1.  Importer for the Gutenberg Index
2.  Database backend offering the Library service using Aries JPA
3.  UI to browse and view the books and to send books to an ebook reader
 
## Importer

Allows to import the complete ebook index of project Gutenberg into a database. The index consists of RDF files in a zip.
The index zip is dropped into a directory for an Apache Camel route to process.

In a first step the zip is extracted into a second directory. There a second route takes over that parses each RDF file into a Pojo and stores these into the DB using the service offered by the backend.

## Backend and REST

Offers an BookRepository OSGi service to manage and browse the ebook index. Inside it uses blueprint and Aries JPA.
The service is written using CDI Annotations. The blueprint xml is generated at build time.

As second service Offers the eBook index as a REST service for the UI and potentially also for other applications
The rest service also allows to send ebooks to an email address. This can be used to send to an Amazon kindle reader. 

## UI

Allows to browse the book index, select books and send to an email address. it is implemented using Angular JS
