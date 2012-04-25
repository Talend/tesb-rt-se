###############################################################################
#
# Copyright (c) 2011 - 2012 Talend Inc. - www.talend.com
# All rights reserved.
#
# This program and the accompanying materials are made available
# under the terms of the Apache License v2.0
# which accompanies this distribution, and is available at
# http://www.apache.org/licenses/LICENSE-2.0
#
###############################################################################
Rent-a-Car Example features
===========================


Build Rent-a-Car Example features
---------------------------------
From the base directory of this example (i.e., where this README file is
located), the maven pom.xml file can be used to build this example. 

Using maven commands on either UNIX/Linux or Windows:
(JDK 1.6.0 and Maven 3.0.3 or later required)

mvn clean install                 (building feature for basic Rent-a-Car example)
mvn clean install -Pslsam         (building feature for Service Locator and Service Activity Monitoring enabled Rent-a-Car example)
mvn clean install -Psts           (building feature for Security Token Service enabled Rent-a-Car example)
mvn clean install -Pall           (building feature for Rent-a-Car example that enabled with all tesb features: SL, SAM, STS)


How to install Rent-a-Car Example features to OSGI container
------------------------------------------------------------

Rent A Car Basic:
features:addurl mvn:org.talend.esb.examples.rent-a-car/features/<version>/xml
features:install tesb-rac-app
features:install tesb-rac-services

Rent A Car with Locator and SAM:
features:addurl mvn:org.talend.esb.examples.rent-a-car/features-sl-sam/<version>/xml
features:install tesb-rac-app-sl-sam
features:install tesb-rac-services-sl-sam

Rent A Car with STS:
features:addurl mvn:org.talend.esb.examples.rent-a-car/features-sts/<version>/xml
features:install tesb-rac-app-sts
features:install tesb-rac-services-sts

Rent A Car with all features:
features:addurl mvn:org.talend.esb.examples.rent-a-car/features-all/<version>/xml
features:install tesb-rac-app-all
features:install tesb-rac-services-all