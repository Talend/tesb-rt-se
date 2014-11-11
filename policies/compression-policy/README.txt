This readme describe how to use Compression feature in Talend ESB.

Compression feature enables gzip compression of on-the-wire data. 

COMPRESSION conflicts with standard CXF GZIP feature in case of
GZIP feature is installed after the COMPRESSION feature.

Compression can be enabled via policy or by adding feature.
Supported attribute:

threshold - the threshold under which messages are not compressed. 
            Optional. Default value="1024";


a) Enabling via policy (for soap services)

At first you should upload compression policy to the service registry and attach it to a service.
Here is example of the policy:

<wsp:Policy Name="wspolicy_compression"  xmlns:wsp="http://www.w3.org/ns/ws-policy">
    <wsp:ExactlyOne>
        <wsp:All>
            <tpa:Compression xmlns:tpa="http://types.talend.com/policy/assertion/1.0" threshold="1000" />
        </wsp:All>
    </wsp:ExactlyOne>
</wsp:Policy>

b) Enabling via feature (support both Soap and REST service)
You can add compression feature to features list:

<jaxws:features>
	<bean id="compressionFeature" class="org.talend.esb.policy.compression.feature.CompressionFeature">
		<property name="threshold" value="100"/>	
	</bean>
</jaxws:features>