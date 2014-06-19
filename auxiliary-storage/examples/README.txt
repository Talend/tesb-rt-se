- Install AuxiliaryStore feature to Talend Runtime (karaf):

	features:addurl mvn:org.talend.esb.callcontext.store/callcontext-store-features/0.1-SNAPSHOT/xml
	features:install tesb-ccs
	
- Build Example project:

	mvn clean install

- Run example:

	mvn -Ptest

