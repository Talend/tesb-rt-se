- Install AuxiliaryStorage feature to Talend Runtime (karaf):

	features:addurl mvn:org.talend.esb.auxiliary.storage/auxiliary-storage-features/5.6.1/xml
	features:install tesb-aux
	
- Build Example project:

	mvn clean install

- Run example:

	mvn -Ptest

