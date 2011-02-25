<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:jaxws="http://cxf.apache.org/jaxws"
	xmlns:context="http://www.springframework.org/schema/context" xmlns:tx="http://www.springframework.org/schema/tx"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd http://cxf.apache.org/jaxws http://cxf.apache.org/schemas/jaxws.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.0.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-3.0.xsd http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-3.0.xsd">

	<!-- Mit annotation-config muss nicht extra die Bean <bean class="org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor"/> 
		konfiguriert werden. @PersistenceUnit @PersistenceContext kann so automatisch 
		gelesen werden. -->
	<context:annotation-config />

	<tx:annotation-driven />

	<!-- DO NOT CHANGE - SERVICE CONFIGURATION START -->
	<bean id="monitoringService" class="org.sopera.monitoring.impl.MonitoringServiceImpl">
		<property name="eventFilter">
			<list>
				<ref local="filterId0000" />
			</list>
		</property>
		<property name="eventManipulator">
			<list>
				<ref local="passwordFilter" />
				<ref local="contentLengthFilter" />
			</list>
		</property>
		<property name="preHandler">
			<list>
				<!-- CHANGE - ADD YOUR CUSTOM HANDLER START -->
				<!-- <ref local="SetEmptyPreHandler" /> -->
				<ref local="stageHandler" />
				<!-- CHANGE - ADD YOUR CUSTOM HANDLER END -->
			</list>
		</property>
		<property name="persistenceHandler" ref="defaultDatabaseHandler" />
		<property name="postHandler">
			<list>
				<!-- CHANGE - ADD YOUR CUSTOM HANDLER START -->
				<!-- <ref local="updateHandler" /> -->
				<ref local="checkIdPostHandler" />
				<!-- CHANGE - ADD YOUR CUSTOM HANDLER END -->
			</list>
		</property>
	</bean>
	<!-- DO NOT CHANGE - SERVICE CONFIGURATION END -->

	<!-- DO NOT CHANGE - FILTER CONFIGURATION START -->
	<bean id="passwordFilter" class="org.sopera.monitoring.handler.impl.PasswordHandler">
		<property name="tagnames">
			<list>
				<value>password</value>
				<value>passwort</value>
				<value>Password</value>
				<value>Passwort</value>
			</list>
		</property>
	</bean>
	<bean id="contentLengthFilter" class="org.sopera.monitoring.handler.impl.ContentLengthHandler">
		<property name="length" value="2000" />
		<property name="eventFilter">
			<list>
				<ref local="filterDoNotCut" />
			</list>
		</property>
	</bean>
	<!-- DO NOT CHANGE - FILTER CONFIGURATION END -->

	<bean id="filterId0000" class="org.sopera.monitoring.filter.impl.StringContentFilter">
		<property name="wordsToFilter">
			<list>
				<value>0000</value>
			</list>
		</property>
	</bean>
	<bean id="filterDoNotCut" class="org.sopera.monitoring.filter.impl.StringContentFilter">
		<property name="wordsToFilter">
			<list>
				<value>doNotCut</value>
			</list>
		</property>
	</bean>

	<!-- DO NOT CHANGE - DATABASE CONFIGURATION START -->
	<bean id="defaultDatabaseHandler"
		class="org.sopera.monitoring.handler.impl.DefaultDatabaseHandler">
		<!-- <property name="entityManagerFactory" ref="entityManagerFactory" /> -->
	</bean>
	<!-- DO NOT CHANGE - DATABASE CONFIGURATION END -->


	<!-- CHANGE - CUSTOM HANDLER CONFIGURATION START -->
	<bean id="stageHandler" class="ch.zurich.monitoring.handler.impl.StageHandler">
		<property name="stage" value="DEV" />
	</bean>
	<bean id="checkIdPostHandler" class="ch.zurich.monitoring.handler.impl.CheckIdPostHandler"></bean>
	<bean id="SetEmptyPreHandler" class="ch.zurich.monitoring.handler.impl.SetEmptyPreHandler"></bean>
	<bean id="updateHandler" class="ch.zurich.monitoring.handler.impl.UpdateHandler">
		<property name="eventFilter">
			<list>
			</list>
		</property>
	</bean>
	<!-- CHANGE - CUSTOM HANDLER CONFIGURATION END -->

	<bean id="dataSource" name="dataSource"
		class="org.springframework.jdbc.datasource.DriverManagerDataSource">
		<property name="driverClassName" value="com.ibm.db2.jcc.DB2Driver" />
		<property name="url" value="jdbc:db2://b0d0nb02.rz.ch.zurich.com:50640/CHZDZSOA:currentSchema=ZSOATEST;" />
		<property name="username" value="chzdzsur" />
		<property name="password" value="qr_Dw_cL" />
	</bean>

	<!-- This bean will be instanciated automaticaly by @PersistenceContext -->
	<bean id="entityManagerFactory"
		class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
		<property name="dataSource" ref="dataSource" />
		<property name="persistenceUnitName" value="monitoringServicePersistenceUnit"/>
		<property name="jpaVendorAdapter">
			<bean
				class="org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter">
				<property name="showSql" value="true" />
				<property name="generateDdl" value="false" />
				<property name="databasePlatform"
					value="org.eclipse.persistence.platform.database.DB2MainframePlatform" />
			</bean>
		</property>
		<!-- Aktuell kein weaving eingebunden. <property name="loadTimeWeaver"> 
			<bean class="org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver 
			" /> </property> -->
	</bean>


	<!-- For JEE Servers have a look at: http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/orm.html#orm-jpa 
		13.5.1.2 Obtaining an EntityManagerFactory from JNDI <beans> <jee:jndi-lookup 
		id="myEmf" jndi-name="persistence/myPersistenceUnit"/> </beans> -->

	<!-- the name "transactionManager" is needed for default injection into 
		tx:annotation-driven -->
	<bean id="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">
		<property name="entityManagerFactory" ref="entityManagerFactory" />
		<property name="dataSource" ref="dataSource"></property>
		<qualifier value="defaultTransaction" />
	</bean>

</beans>
