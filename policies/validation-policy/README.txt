This readme describe how to use schema validation feature in Talend ESB.

Two different types of schema validation are currently supported:
1. WSDLSchema - validation against WSDL;
2. CustomSchema - validation against custom schema. 

1. WSDLSchema Validation

Schema Validation can be enabled via policy.

Supported attributes:
type - WSDLSchema (if not specified, assumed as WSDLSchema)
appliesTo - consumer/provider/always/none;
message - request/response/all/none;

Enabling via policy

At first you should upload Schema Validation policy to the service registry and attach it to a service.
Here is example of the policy:

<wsp:Policy xmlns:wsp="http://www.w3.org/ns/ws-policy" Name="wspolicy_schema_validation">
    <wsp:ExactlyOne>
        <wsp:All>
            <tpa:SchemaValidation xmlns:tpa="http://types.talend.com/policy/assertion/1.0" type="WSDLSchema" appliesTo="provider" message="request"/>
        </wsp:All>
    </wsp:ExactlyOne>
</wsp:Policy>

2. CustomSchema Validation
Supported assertion attributes:

type - CustomSchema (if not specified, assumed as WSDLSchema)
path - URL, absolute or relative path to custom schema;
appliesTo - consumer/provider/always/none;
message - request/response/all/none;

Policy samples:
<wsp:Policy xmlns:wsp="http://www.w3.org/ns/ws-policy" Name="wspolicy_schema_custom_validation">
    <wsp:ExactlyOne>
        <wsp:All>
            <tpa:SchemaValidation xmlns:tpa="http://types.talend.com/policy/assertion/1.0" type="CustomSchema" path="http://localhost:8080/CustomSchema.xsd" appliesTo="provider" message="request"/>
        </wsp:All>
    </wsp:ExactlyOne>
</wsp:Policy>

<wsp:Policy xmlns:wsp="http://www.w3.org/ns/ws-policy" Name="wspolicy_schema_custom_validation">
    <wsp:ExactlyOne>
        <wsp:All>
            <tpa:SchemaValidation xmlns:tpa="http://types.talend.com/policy/assertion/1.0" type="CustomSchema" path="/opt/CustomSchema.xsd" appliesTo="consumer" message="response"/>
        </wsp:All>
    </wsp:ExactlyOne>
</wsp:Policy>

 