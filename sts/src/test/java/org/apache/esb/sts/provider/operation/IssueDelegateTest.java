package org.apache.esb.sts.provider.operation;

import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.esb.sts.provider.ProviderPasswordCallback;
import org.apache.esb.sts.provider.STSException;
import org.apache.esb.sts.provider.token.Saml1TokenProvider;
import org.apache.esb.sts.provider.token.Saml2TokenProvider;
import org.apache.esb.sts.provider.token.TokenProvider;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DocumentTypeImpl;
import org.apache.xerces.dom.ElementNSImpl;
import org.apache.xml.security.keys.KeyInfo;
import org.easymock.EasyMock;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.security.auth.callback.PasswordCallback;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.junit.Test;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenType;
import org.oasis_open.docs.ws_sx.ws_trust._200512.UseKeyType;
import org.w3._2000._09.xmldsig.KeyInfoType;
import org.w3._2000._09.xmldsig.X509DataType;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class IssueDelegateTest {

	RequestSecurityTokenType requestMock = createMock(RequestSecurityTokenType.class);
	
	ProviderPasswordCallback passwordCallbackMock = createMock(ProviderPasswordCallback.class);
	
	private static final String CERT_DATA = "MIICbDCCAdWgAwIBAgIBezANBgkqhkiG9w0BAQUFADAhMR8wHQYDVQQDDBZSdURpLVJvb3RDQS0wMUBydWRpLVBDMB4XDTEwMTEyMjE2MzkzNVoXDTExMDMwMjE2MzkzNVowJzElMCMGA1UEAwwcU2VjdXJpdHlUb2tlblNlcnZpY2VQcm92aWRlcjCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAl4sZX2T61J+5lm1fsSMDn5KFkiYbOGYaSXN8CC57aEZjVx1m7wKuQNNaskC5DA+x1mLcFqWN5OqO6+gphbHnZ3/LStRLQfckmv/2Kigg2MVikNduIpT94nEmKl4FP5aI+yDAGofXLUjnpiBGHUoMj8qVYmM3n4ZgyZXuX7/x3ukCAwEAAaOBrTCBqjAOBgNVHQ8BAf8EBAMCBDAwRAYDVR0jBD0wO4AU+pT7F58ucXMA186r19VELZeiIyyhIKQeMBwxGjAYBgNVBAMMEVJ1RGlfQ0EwMUBpYWJnLmRlggF7MFIGA1UdHwEB/wRIMEYwRKBCoECCPmh0dHA6Ly9zZXJ2aWNlcy5uYXRvLmludC9ERVUvQncvSVQvUnVEaS9JQVMvMDAxL1NvYVBraV9TZXJ2aWNlMA0GCSqGSIb3DQEBBQUAA4GBAFL1KM415BxQzn6zGHtI2RhkB2NcNQNkrybKfp2VrP66zcL9aIB5HRRN0RFFikLoSiJX7jHESS+tepGwg56kOgPk2f80WbHeMapeYK8MDT0F+yLdufEhAYbKNT7NALHVRA4HN+CEi4PHa9qVOOoJ2wmzhxrD4fVfUv/jWYY/+X4i";
	
	@Test
	public void TestIssueDelegateNullParameter()	{
		try	{
			IssueDelegate id = new IssueDelegate();
			assertNotNull(id);
			ProviderPasswordCallback passwordCallback = new ProviderPasswordCallback();
			id.setPasswordCallback(passwordCallback);
			
			id.issue(null);
			fail("NullPointerException should be thrown");
		} catch(NullPointerException e) {
			
		}
	}
	
	@Test
	public void TestIssueDelegate()	{
		IssueDelegate id = new IssueDelegate();
		assertNotNull(id);
			
		EasyMock.expect(requestMock.getAny()).andStubReturn(Arrays.asList());
		EasyMock.replay(requestMock);
			
		EasyMock.expect(passwordCallbackMock.resetUsername()).andReturn("username");
		EasyMock.replay(passwordCallbackMock);
			
		id.setPasswordCallback(passwordCallbackMock);
		
		TokenProvider tp1 = new Saml1TokenProvider();
		TokenProvider tp2 = new Saml2TokenProvider();
		id.setTokenProviders(Arrays.asList(tp1, tp2));
		
		id.issue(requestMock);
			
		verify(requestMock);
	}
	
	@Test
	public void TestIssueDelegateUsernameNull()	{
		try	{
			IssueDelegate id = new IssueDelegate();
			assertNotNull(id);
			
			EasyMock.expect(requestMock.getAny()).andStubReturn(Arrays.asList());
			EasyMock.replay(requestMock);
			
			EasyMock.expect(passwordCallbackMock.resetUsername()).andReturn(null);
			EasyMock.replay(passwordCallbackMock);
			
			id.setPasswordCallback(passwordCallbackMock);
			
			TokenProvider tp1 = new Saml1TokenProvider();
			TokenProvider tp2 = new Saml2TokenProvider();
			id.setTokenProviders(Arrays.asList(tp1, tp2));
			
			id.issue(requestMock);
			verify(requestMock);
			
			fail("STSException should be thrown");
			
		} catch(STSException e) {
			
		}
	}
	
	@Test
	public void TestIssueDelegateWithCert() throws CertificateException	{
		IssueDelegate id = new IssueDelegate();
		assertNotNull(id);
		
		JAXBElement<byte[]> jX509Certificate = new JAXBElement<byte[]>(QName.valueOf("X509Certificate"), byte[].class, Base64.decodeBase64(CERT_DATA.getBytes()));
		
		X509DataType x509DataType = new X509DataType();
		x509DataType.getX509IssuerSerialOrX509SKIOrX509SubjectName().add(jX509Certificate);
		JAXBElement<X509DataType> jX509DataType = new JAXBElement<X509DataType>(QName.valueOf("X509Data"), X509DataType.class, x509DataType);
		
		KeyInfoType keyInfoType = new KeyInfoType();
		keyInfoType.getContent().add(jX509DataType);
		JAXBElement<KeyInfoType> jKeyInfoType = new JAXBElement<KeyInfoType>(QName.valueOf("KeyInfo"), KeyInfoType.class, keyInfoType);
		
		UseKeyType useKeyType = new UseKeyType();
		useKeyType.setAny(jKeyInfoType);
		JAXBElement<UseKeyType> jUseKeyType = new JAXBElement<UseKeyType>(QName.valueOf("UseKey"), UseKeyType.class, useKeyType);
		
		EasyMock.expect(requestMock.getAny()).andStubReturn(Arrays.asList((Object)jUseKeyType));
		EasyMock.replay(requestMock);
			
		EasyMock.expect(passwordCallbackMock.resetUsername()).andReturn(null);
		EasyMock.replay(passwordCallbackMock);
			
		id.setPasswordCallback(passwordCallbackMock);
		
		TokenProvider tp1 = new Saml1TokenProvider();
		TokenProvider tp2 = new Saml2TokenProvider();
		id.setTokenProviders(Arrays.asList(tp1, tp2));
		
		id.issue(requestMock);
			
		verify(requestMock);
	}
	
	@Test
	public void TestIssueDelegateWithInvalidCert() throws CertificateException	{
		IssueDelegate id = new IssueDelegate();
		assertNotNull(id);
		
//		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
//		X509Certificate x509Certificate = null;
//		try {
//			x509Certificate = (X509Certificate)certificateFactory.generateCertificate(new ByteArrayInputStream(Base64.decodeBase64(CERT_DATA.getBytes())));
//		} catch (CertificateException e) {
//			e.printStackTrace();
//		}
//		JAXBElement<X509Certificate> jX509Certificate = new JAXBElement<X509Certificate>(QName.valueOf("X509Certificate"), X509Certificate.class, x509Certificate);
		
		JAXBElement<byte[]> jX509Certificate = new JAXBElement<byte[]>(QName.valueOf("X509Certificate"), byte[].class, CERT_DATA.getBytes());
		
		X509DataType x509DataType = new X509DataType();
		x509DataType.getX509IssuerSerialOrX509SKIOrX509SubjectName().add(jX509Certificate);
		JAXBElement<X509DataType> jX509DataType = new JAXBElement<X509DataType>(QName.valueOf("X509Data"), X509DataType.class, x509DataType);
		
		KeyInfoType keyInfoType = new KeyInfoType();
		keyInfoType.getContent().add(jX509DataType);
		JAXBElement<KeyInfoType> jKeyInfoType = new JAXBElement<KeyInfoType>(QName.valueOf("KeyInfo"), KeyInfoType.class, keyInfoType);
		
		UseKeyType useKeyType = new UseKeyType();
		useKeyType.setAny(jKeyInfoType);
		JAXBElement<UseKeyType> jUseKeyType = new JAXBElement<UseKeyType>(QName.valueOf("UseKey"), UseKeyType.class, useKeyType);
		
		EasyMock.expect(requestMock.getAny()).andStubReturn(Arrays.asList((Object)jUseKeyType));
		EasyMock.replay(requestMock);
			
		EasyMock.expect(passwordCallbackMock.resetUsername()).andReturn(null);
		EasyMock.replay(passwordCallbackMock);
			
		id.setPasswordCallback(passwordCallbackMock);
		
		TokenProvider tp1 = new Saml1TokenProvider();
		TokenProvider tp2 = new Saml2TokenProvider();
		id.setTokenProviders(Arrays.asList(tp1, tp2));		
		
		try {
			id.issue(requestMock);
			fail("CertificateException should be thrown");
		} catch(Exception e) {
			
		}
			
		verify(requestMock);
	}
	
	
	@Test
	public void TestIssueDelegateWithInvalidCert2() throws CertificateException	{
		IssueDelegate id = new IssueDelegate();
		assertNotNull(id);
		
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		X509Certificate x509Certificate = null;
		try {
			x509Certificate = (X509Certificate)certificateFactory.generateCertificate(new ByteArrayInputStream(Base64.decodeBase64(CERT_DATA.getBytes())));
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		JAXBElement<X509Certificate> jX509Certificate = new JAXBElement<X509Certificate>(QName.valueOf("X509Certificate"), X509Certificate.class, x509Certificate);
		
		//JAXBElement<byte[]> jX509Certificate = new JAXBElement<byte[]>(QName.valueOf("X509Certificate"), byte[].class, CERT_DATA.getBytes());
		
		X509DataType x509DataType = new X509DataType();
		x509DataType.getX509IssuerSerialOrX509SKIOrX509SubjectName().add(jX509Certificate);
		JAXBElement<X509DataType> jX509DataType = new JAXBElement<X509DataType>(QName.valueOf("X509Data"), X509DataType.class, x509DataType);
		
		KeyInfoType keyInfoType = new KeyInfoType();
		keyInfoType.getContent().add(jX509DataType);
		JAXBElement<KeyInfoType> jKeyInfoType = new JAXBElement<KeyInfoType>(QName.valueOf("KeyInfo"), KeyInfoType.class, keyInfoType);
		
		UseKeyType useKeyType = new UseKeyType();
		useKeyType.setAny(jKeyInfoType);
		JAXBElement<UseKeyType> jUseKeyType = new JAXBElement<UseKeyType>(QName.valueOf("UseKey"), UseKeyType.class, useKeyType);
		
		EasyMock.expect(requestMock.getAny()).andStubReturn(Arrays.asList((Object)jUseKeyType));
		EasyMock.replay(requestMock);
			
		EasyMock.expect(passwordCallbackMock.resetUsername()).andReturn(null);
		EasyMock.replay(passwordCallbackMock);
			
		TokenProvider tp1 = new Saml1TokenProvider();
		TokenProvider tp2 = new Saml2TokenProvider();
		id.setTokenProviders(Arrays.asList(tp1, tp2));
		
		id.setPasswordCallback(passwordCallbackMock);
		
		
		try {
			id.issue(requestMock);
			fail("CertificateException should be thrown");
		} catch(Exception e) {
			
		}
			
		verify(requestMock);
	}
}
