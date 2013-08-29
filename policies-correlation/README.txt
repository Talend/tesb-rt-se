This readme describe how to use Correlation ID feature in Talend ESB.

Correlation ID can be enabled via policy or by adding feature.
If we do not specify callback handler the id will be generated automatically and value will be the same for request and response.
We can set in in jaxws or jaxrs properties and it looks like:

You can enable CorrelationID feature in these ways:
a) Enabling via policy (for soap services)

At first you should upload correlation policy to the service registry and attach it to a service.
Here is example of the policy:
<wsp:Policy Name="wspolicy_schema_correlation_id"  xmlns:wsp="http://www.w3.org/ns/ws-policy">
    <wsp:ExactlyOne>
        <wsp:All>
            <tpa:CorrelationID xmlns:tpa="http://types.talend.com/policy/assertion/1.0" type="CALLBACK" />
        </wsp:All>
    </wsp:ExactlyOne>
</wsp:Policy>

b) Enabling via feature (for rest and soap service)
You can aff correlationID feature to features list:
<jaxrs:features>
    <bean class="org.talend.esb.policy.correlation.feature.CorrelationIDFeature"/>
</jaxrs:features>

Also in both cases you should specify correlation id handler if needed
<jaxws:properties>
  <entry key="correlation-id.callback-handler">
    <bean class="common.talend.CorrelationHandler" />
  </entry>
</jaxws:properties>
Where common.talend.CorrelationHandler is class that implement 
org.talend.esb.policy.correlation.CorrelationIDCallbackHandler interface

If callback is not specified in properties correlation id will be generated automatically.
