# Configuring XA transaction support in Talend ESB


## Introduction Java Transaction API (JTA)

JTA allows transactions to span more then one resource. So for example a transaction can span two databases or for example a database and a JMS Server.

The main integration point is the interface javax.transaction.TransactionManager. It allows to manage transactions as well as to enlist transactional resources. Only resources that are enlisted with a transaction are taking part in transactions coordinated by it.

The main interface for user code is javax.transaction.UserTransaction. It allows to begin, commit and rollback transactions but not to enlist resources.

Also see the [JTA 1.2 API][jta1.2].

## Transactional Resources

The following technologies support XA transactions.

* JMS (ActiveMQ)
* jdbc (H2, derby, Oracle, mysql, postgresql)
* jcr repositories (Jackrabbit)

## Wrapping a DataSource for XA

An XA capable database offers an XADataSource. This data source is not to be used directly by users though. The user will always bind to the plain DataSource interface. It is the duty of the application server to wrap an XADataSource with pooling and XA auto enlistment.

In OSGi there is also the issue that standard jdbc does not work well with OSGi classloading. So the OSGi alliance specified the DataSourceFactory interface. A DataSourceFactory is provided as an OSGi service by the database driver or by an external adapter. It allows to create DataSource and XADataSource instances in an OSGi friendly way.

The [pax-jdbc project][pax-jdbc] provides DataSourceFactory adapters for legacy drivers as well as pooling and XA auto enlistment wrappers for existing DataSourceFactory services.  

For H2 the installation is below. This installs the H2 database as well as adapters for pooling and XA support.

    feature:repo-add pax-jdbc 0.7.0
    feature:install pax-jdbc-h2 pax-jdbc-pool-dbcp2 pax-jdbc-config transaction

The installation can be checked with `service:list DataSourceFactory`. It should list the original H2 DataSourceFactory as well as a pooling one and a pooling/XA one. The different services can be discerned by their service properties.

To create an actual DataSource pax-jdbc-config monitors config files with the naming scheme etc/org.ops4j.datasource-<yourname>.cfg. It will automatically create a DataSource for each such config.

You can reference the DataSourceFactory using the special property osgi.jdbc.driver.name or osgi.jdbc.driver.class. The value should match the respective property value of the DataSourceFactory service. For more details see the [pax-jdbc docs][pax-jdbc].

## Wrapping a ConnectionFactory for XA

For JMS the situation is similar like for databases. The jms provider offers an XAConnectionFactory that needs to be wrapped with pooling and auto enlistement support. Unfortunately there is no spec like the DataSourceFactory. So the wrapping can not be done fully generically.

So the best practice is to create a blueprint context that creates, wraps and publishes the ConnectionFactory. In the ebook example this is done in the ebook-connectionfactory project. The wrapping is done with the help of the activemq pooling library. See the [blueprint context][cf] for details.

## XA support in Apache Aries JPA

Prerequiste is to have a correctly wrapped DataSource or DataSourceFactory like described above.

The next thing is to create a bundle for the jpa Entities. For the most part this works like in JEE. The entities need to be correctly annotated and a persistence.xml should be put into META-INF/persistence. The main thing to keep in mind for OSGi is to add a special Manifest header `Meta-Persistence:META-INF/persistence.xml` which marks the bundle as containing a persistence unit and points to the location of the persistence.xml.

Aries JPA will monitor all bundles in state Starting and Active for the Meta-Persistence header above. It will then scan the persistence.xml and create a matching EnityManagerFactory and also an EntityManager as an OSGi service.

These services can then be leveraged in a second bundle that uses JPA to access the entities. The easiest way to do this is to leverage the [blueprint-maven-plugin][blueprint-maven-plugin]. It scans the classes for CDI/JEE annoations and creates a blueprint xml from them. The skeleton of a class using JPA would look like this:

	@Singleton
	@Transactional
	public class BookRepositoryImpl {
		@PersistenceContext(unitName="ebook")
		EntityManager em;
		
	}

@Singleton marks the class as a bean to be listed in the blueprint.xml. This is the major difference to CDI where it is not needed to mark beans in a special way.

@Transactional can be used on class or method level. An annotation on method level overrides the same on class level. By default this will define a transaction of type TxType.Required. This is the typical type for methods that change JPA entities. It defines that the method will join a transaction if one exists or create a new one if neded. For methods that only read data the TxType.Supports can be used. It allows the method to participate in a transaction but also to run without. For a full desription of the annoation seet the JTA 1.2 specification.

@PersistenceContext defines that the variable should be injected with the EntityManager of the named persistence unit. Aries JPA takes care of creating thread safe EntityManager that can be used like in JEE. The lifecycle of the EntityManager is bound to an OSGi Coordination. By default the coordination spans that outermost call marked with @Transactional. This is important as the EntityManager itself is not thread safe so Aries JPA internally needs to bind one instance to a thread but also needs to make sure the EntityManager is relatively short lived to avoid classloading problems in case of bundle restarts.

The user can also create a Coordination manually using the CoordinatorService. In the ebook example this is done using a CXF interceptor that creates the coordination on the CXF level when a request is received and only ends it when the serialization is done. This makes the EntityManager available during serialization and allows to lazily load entities without the problem of having detached entities.

## XA support and JMS in Apache Camel

It is also possible to use transactions in Apache Camel routes. Camel relies in the spring transaction abstraction so it is necessary to use the right wrapper class to make JTA transactions work.

A transactional route can look like this:

	from("jms:queue1").transactional().to("jms:queue2")

The `transactional()` DSl Element tells karaf to start a transaction when the route enters the segment and commit once the route is completed succesfully. In case of unhandled errors it will roll back.

The above approach would not cover the `from` part though where the message is received. So to make the whole route transactional it is necessary to also configure the jms component to do JTA transactions.

For this to work like in databases we first need to provide a ConnectionFactory as an OSGi service that is correctly wrapped for XA and pooling.

The ConnectionFactory and the TransactionManager are referenced as OSGi services. They then need to be injected into the JMSComponent and the SpringTransactionPolicy.

	<reference id="connectionFactory" interface="javax.jms.ConnectionFactory" />
	<reference id="transactionManager" interface="org.springframework.transaction.PlatformTransactionManager"/>

	<bean id="jms" class="org.apache.camel.component.jms.JmsComponent">
		<property name="connectionFactory" ref="connectionFactory"/>
		<property name="transactionManager" ref="transactionManager"/>
	</bean>
	
	<bean id="PROPAGATION_REQUIRED" class="org.apache.camel.spring.spi.SpringTransactionPolicy">
		<property name="transactionManager" ref="transactionManager"/>
		<property name="propagationBehaviorName" value="PROPAGATION_REQUIRED"/>
	</bean>

With this setup the JmsComponent will always receive messages inside a JTA transaction and will also participate in a JTA transaction when sending out messages.

Interestingly we are looking up the transaction manager as spring PlatformTransactionManager. This is possible as the default transaction manager in karaf from Aries already implements this interface in addition to the plain JTA TransactionManager. 

## Redelivery in JMS

Apache Camel can do redelivery using the camel error handling. This is not related to JTA though. So the more solid way is to configure redelivery in the JMS broker.

ActiveMQ can [configure redelivery on the ConnectionFactory and centrally on the broker][activemq_redelivery].
The user can set the number of redeliveries, the backoff behaviour and the dead letter queue where messages end up if all fails. 


[cf]: https://raw.githubusercontent.com/Talend/tesb-rt-se/master/examples/tesb/ebook/ebook-connectionfactory/src/main/resources/OSGI-INF/blueprint/blueprint.xml "XA wrapping of a JMS ConnectionFactory"

[jta1.2]: https://www.jcp.org/en/jsr/detail?id=907 "JTA 1.2 spec"

[pax-jdbc]: https://ops4j1.jira.com/wiki/display/PAXJDBC/Pooling+and+XA+support+for+DataSourceFactory "Pooling and XA support for DataSourceFactory"

[blueprint-maven-plugin]: http://aries.apache.org/modules/blueprint-maven-plugin.html "Aries blueprint-maven-plugin documentation"

[activemq_redelivery]: http://activemq.apache.org/message-redelivery-and-dlq-handling.html "ActiveMQ Message Redelivery and DLQ Handling"
