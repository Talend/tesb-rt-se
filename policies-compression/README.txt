This readme describe how to use Compression feature in Talend ESB.

Compression feature enables gzip compression of on-the-wire data. 
An initial request from a client will not be gzipped, 
but an Accept header will be added and if the server supports it, 
the response will be gzipped and any subsequent requests will be.

Compression can be enabled via policy or by adding feature.
Supported attributes:

threshold - the threshold under which messages are not gzipped. 
            Optional. Default value="-1" (compress all messages);

force     - force gzip compression instead of negotiating via the Accept-Encoding header. 
            Optional. Default value = "false";

a) Enabling via policy (for soap services)

At first you should upload compression policy to the service registry and attach it to a service.
Here is example of the policy:

<wsp:Policy Name="wspolicy_compression"  xmlns:wsp="http://www.w3.org/ns/ws-policy">
    <wsp:ExactlyOne>
        <wsp:All>
            <tpa:Compression xmlns:tpa="http://types.talend.com/policy/assertion/1.0" threshold="1000" force="false" />
        </wsp:All>
    </wsp:ExactlyOne>
</wsp:Policy>

b) Enabling via feature (support both Soap and REST service)
You can add compression feature to features list:

<jaxws:features>
	<bean id="compressionFeature" class="org.talend.esb.policy.compression.feature.CompressionFeature">
		<property name="threshold" value="1"/>	
		<property name="force" value="true"/>
	</bean>
</jaxws:features>