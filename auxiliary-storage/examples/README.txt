- Install AuxiliaryStorage feature to Talend Runtime (karaf):

	feature:repo-add mvn:org.talend.esb.auxiliary.storage/auxiliary-storage-features/5.6.1/xml
	feature:install tesb-aux
	
- Build Example project:

	mvn clean install

- Run example:

	mvn -Ptest

