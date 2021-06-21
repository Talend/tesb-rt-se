###
#
# ============================================================================
#
# Copyright (c) 2006-2021 Talend Inc. - www.talend.com
#
# This source code is available under agreement available at
# %InstallDIR%\license.txt
#
# You should have received a copy of the agreement
# along with this program; if not, write to Talend SA
# 5/7 rue Salomon De Rothschild, 92150 Suresnes, France
#
# ============================================================================
#
###
--------------------------------------------
Talend Registry Rent a Car example artifacts
--------------------------------------------
To be able to run the Example that uses Talend Registry it is necessary to upload
some artifacts into it before deploying the application. This readme describes how
to upload these artifacts into Talend Registry.

---------------------------
Upload Rent a Car resources
---------------------------
You have several ways to upload resources needed for the example.

1. Execute following commands from Karaf shell:

1.1. Install registry commands component to the container:
feature:install tesb-registry-server-commands

1.2. Upload resources to the registry:
tregistry:create wsdl "<path-to-this-folder>/CRMService.wsdl"
tregistry:create wsdl "<path-to-this-folder>/ReservationService.wsdl"
tregistry:create ws-policy "<path-to-this-folder>/usernameToken.policy"
tregistry:create ws-policy-attach "<path-to-this-folder>/CRMServicePolicyAttachment.policy"
tregistry:create ws-policy-attach "<path-to-this-folder>/ReservationServicePolicyAttachment.policy"

2. From command line using curl:
curl -X POST -d @UsernameTokenRequest.xml --header "Content-Type:application/atom+xml;type=entry" http://localhost:8040/services/registry/admin/ws-policy
curl -X POST -d @CRMRequest.xml --header "Content-Type:application/atom+xml;type=entry" http://localhost:8040/services/registry/admin/wsdl
curl -X POST -d @ReservationRequest.xml --header "Content-Type:application/atom+xml;type=entry" http://localhost:8040/services/registry/admin/wsdl
curl -X POST -d @CRMAttachmentRequest.xml --header "Content-Type:application/atom+xml;type=entry" http://localhost:8040/services/registry/admin/ws-policy-attach
curl -X POST -d @ReservationAttachmentRequest.xml --header "Content-Type:application/atom+xml;type=entry" http://localhost:8040/services/registry/admin/ws-policy-attach

