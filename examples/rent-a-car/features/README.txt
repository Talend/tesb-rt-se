###############################################################################
#
# Copyright (c) 2011 Talend Inc. - www.talend.com
# All rights reserved.
#
# This program and the accompanying materials are made available
# under the terms of the Apache License v2.0
# which accompanies this distribution, and is available at
# http://www.apache.org/licenses/LICENSE-2.0
#
###############################################################################
Rent-a-Car Description Example 
=======================================
How to install rent-a-car demo features to OSGI container:

Rent A Car Basic:
features:addurl mvn:org.talend.esb.examples.rent-a-car/features/<version>/xml
features:install tesb-rac-app
features:install tesb-rac-services

Rent A Car with Locator:
features:addurl mvn:org.talend.esb.examples.rent-a-car/features-locator/<version>/xml
features:install tesb-rac-app-locator
features:install tesb-rac-services-locator

Rent A Car with SAM:
features:addurl mvn:org.talend.esb.examples.rent-a-car/features-sam/<version>/xml
features:install tesb-rac-app-sam
features:install tesb-rac-services-sam

Rent A Car with STS:
features:addurl mvn:org.talend.esb.examples.rent-a-car/features-sts/<version>/xml
features:install tesb-rac-app-sts
features:install tesb-rac-services-sts

Rent A Car with JMX:
features:addurl mvn:org.talend.esb.examples.rent-a-car/features-jmx/<version>/xml
features:install tesb-rac-app-jmx
features:install tesb-rac-services-jmx