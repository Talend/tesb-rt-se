This readme describe how to use Correlation ID feature in Talend ESB.

Correlation ID can be enabled via policy or by adding feature.
If we do not specify callback handler the id will be generated automatically and value will be the same for request and response.

You can enable CorrelationID feature in these ways:
a) Enabling via policy (for soap services)

At first you should upload correlation policy to the service registry and attach it to a service.
Here are  examples of the policy:

<wsp:Policy Name="wspolicy_schema_correlation_id"  xmlns:wsp="http://www.w3.org/ns/ws-policy">
    <wsp:ExactlyOne>
        <wsp:All>
            <tpa:CorrelationID xmlns:tpa="http://types.talend.com/policy/assertion/1.0" type="callback" />
        </wsp:All>
    </wsp:ExactlyOne>
</wsp:Policy>

<wsp:Policy Name="wspolicy_schema_correlation_id"  xmlns:wsp="http://www.w3.org/ns/ws-policy">
    <wsp:ExactlyOne>
        <wsp:All>
			<tpa:CorrelationID xmlns:tpa="http://types.talend.com/policy/assertion/1.0" type="xpath" name="order">
				<tpa:Namespace prefix="ns2" uri="http://customerservice.example.com/" />
				<tpa:Part name="customer_company" optional="true" xpath="/ns2:order/customer" />
				<tpa:Part name="ordernr" xpath="/ns2:order/orderNumber" />
			</tpa:CorrelationID>
        </wsp:All>
    </wsp:ExactlyOne>
</wsp:Policy>

b) Enabling via feature (support both Soap and REST service)
You can add correlationID feature to features list:

<jaxrs:features>
    <bean class="org.talend.esb.policy.correlation.feature.CorrelationIDFeature"/>
</jaxrs:features>

Also in both cases above you should specify correlation id handler if needed
<jaxws:properties>
  <entry key="correlation-id.callback-handler">
    <bean class="common.talend.CorrelationHandler" />
  </entry>
</jaxws:properties>

Where common.talend.CorrelationHandler is a custom class that implement 
org.talend.esb.policy.correlation.CorrelationIDCallbackHandler interface

If callback is not specified in properties then Correlation Id will be generated automatically.

c) Enabling via feature using XPATH (for SOAP service only)

		<jaxws:features>
			<bean
				class="org.talend.esb.policy.correlation.feature.CorrelationIDFeature">
				<property name="name" value="order" />
				<property name="type" value="xpath" />
				<property name="xpathNamespaces">
					<list value-type="org.talend.esb.policy.correlation.impl.xpath.XpathNamespace">
						<ref bean="XpathNamespaceNS2" />
					</list>
				</property>
				<property name="xpathParts">
					<list value-type="org.talend.esb.policy.correlation.impl.xpath.XpathNamespace">
						<ref bean="XpathPartCustomerName" />
						<ref bean="XpathPartCustomerID" />
					</list>
				</property>
			</bean>
		</jaxws:features>
		
		...
		
		<bean id="XpathNamespaceNS2"
			class="org.talend.esb.policy.correlation.impl.xpath.XpathNamespace">
			<property name="prefix" value="ns2" />
			<property name="uri" value="http://customerservice.example.com/" />
		</bean>
	
		<bean id="XpathPartCustomerName"
			class="org.talend.esb.policy.correlation.impl.xpath.XpathPart">
			<property name="name" value="customerName" />
			<property name="xpath" value="/ns2:updateCustomer/customer/name" />
			<property name="optional" value="true" />
		</bean>
		
		<bean id="XpathPartCustomerID"
			class="org.talend.esb.policy.correlation.impl.xpath.XpathPart">
			<property name="name" value="customerID" />
			<property name="xpath" value="/ns2:updateCustomer/customer/customerId" />
			<property name="optional" value="false" />
		</bean>			