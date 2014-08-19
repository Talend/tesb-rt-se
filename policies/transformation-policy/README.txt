This readme describes how to use transformation feature in Talend ESB.

Two different types of transformations are currently supported:
1. XSLT transformation - transforms the message on the base of XSLT script
2. Lightweight transformation - uses own syntax for stream based transformations 

Detailed feature description can be found in http://cxf.apache.org/docs/xslt-feature.html.

1. XSLT Transformation

XSLT Transformation can be enabled via policy or by adding feature.

Note: as far as Xalan XSLT engine is actually not completely stream oriented, XSLT Feature breaks streaming. However it uses high-performance DTM (Document Table Model) instead complete DOM model.
Performance can be improved in the future by using further versions of Xalan or other XSLT engines (like Saxon or STX oriented Joost). 

Supported assertion attributes:
type - xslt (if not specified, assumed as xslt)
inXSLTPath - Path to XSLT script for inbound transformation;
outXSLTPath - Path to XSLT script for outbound transformation;

a) Enabling via policy

At first you should upload XSLT transformation policy to the service registry and attach it to a service.
Here is example of the policy:

<wsp:Policy Name="wspolicy_xslt"  xmlns:wsp="http://www.w3.org/ns/ws-policy">
    <wsp:ExactlyOne>
        <wsp:All>
            <tpa:Transformation xmlns:tpa="http://types.talend.com/policy/assertion/1.0" 
              inXSLTPath="requestTransformation.xsl" outXSLTPath="responseTransformation.xsl"/>
        </wsp:All>
    </wsp:ExactlyOne>
</wsp:Policy>

inXSLTPath, outXSLTPath attributes can be also specified through context properties:
"org.talend.esb.transformation.in.xslt-path"
"org.talend.esb.transformation.out.xslt-path"
If context properties are specified, they overwrite corresponded policy attributes.

inXSLTPath, outXSLTPath attributes can contain:
 - HTTP URL's (e.g. http://example.org/xsl/requestTransformation.xsl )
 - Path to xsl file, relative to TESB container (e.g. etc/requestTransformation.xsl )
 - Classpath path to xsl file


b) Enabling via feature
You can add XSLT feature to features list:

<bean id="xsltFeature" class="org.talend.esb.policy.transformation.feature.XSLTFeature">
    <property name="inXSLTPath" value="requestTransformation.xsl" />
    <property name="outXSLTPath" value="responseTransformation.xsl" />
</bean>

2. Simple Transformation
Supported assertion attributes:

type - simple

Policy sample:
<wsp:Policy Name="wspolicy_xslt"  xmlns:wsp="http://www.w3.org/ns/ws-policy">
    <wsp:ExactlyOne>
        <wsp:All>
            <tpa:Transformation xmlns:tpa="http://types.talend.com/policy/assertion/1.0" type="simple"/>
        </wsp:All>
    </wsp:ExactlyOne>
</wsp:Policy>

To activate transformation it is necessary to specify in/out transformation maps using contaxt properties:
"org.talend.esb.transformation.in.transform-map"
"org.talend.esb.transformation.out.transform-map"

See details about transformation maps in the http://cxf.apache.org/docs/transformationfeature.html
 