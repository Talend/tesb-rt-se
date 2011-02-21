package org.apache.esb.sts.provider.operation;

import org.apache.esb.sts.provider.ProviderPasswordCallback;
import org.easymock.EasyMock;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.security.auth.callback.PasswordCallback;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.junit.Test;
import org.oasis_open.docs.ws_sx.ws_trust._200512.RequestSecurityTokenType;

public class IssueDelegateTest {

	@Test
	public void TestIssueDelegateNullParameter()	{
		try	{
			IssueDelegate id = new IssueDelegate();
			assertNotNull(id);
			ProviderPasswordCallback passwordCallback = new ProviderPasswordCallback();
			id.setPasswordCallback(passwordCallback);
			id.setSaml2(false);
			id.issue(null);
		} catch(NullPointerException e) {
			
		}
	}
	
	@Test
	public void TestIssueDelegate()	{
		try	{
			IssueDelegate id = new IssueDelegate();
			assertNotNull(id);
			ProviderPasswordCallback passwordCallback = new ProviderPasswordCallback();
			id.setPasswordCallback(passwordCallback);
			id.setSaml2(false);
			RequestSecurityTokenType request = createMock(RequestSecurityTokenType.class);
			EasyMock.expect(request.getAny()).andReturn(new ArrayList<Object>());
			id.issue(request);
		} catch(NullPointerException e) {
			
		}
	}
}
