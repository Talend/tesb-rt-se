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