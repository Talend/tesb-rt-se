package org.apache.esb.sts.provider;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.xml.namespace.QName;
import junit.framework.TestCase;
import org.junit.Test;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

public class ProviderPasswordCallbackTest {

	WSPasswordCallback callback = new  WSPasswordCallback("", "", "", WSPasswordCallback.USERNAME_TOKEN_UNKNOWN);
	
	@Test
	public void testProviderPasswordCallbackNullParameters() {
		
		try {
			ProviderPasswordCallback ppc = new ProviderPasswordCallback();
			ppc.handle(null);
			fail("NullPointerException should be thrown");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedCallbackException e) {
			// TODO Auto-generated catch block
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
		}		
	}
	
	@Test
	public void testProviderPasswordCallbackEmptyCallbackParameters() {
		
		try {
			ProviderPasswordCallback ppc = new ProviderPasswordCallback();
			Callback[] c = new WSPasswordCallback[1];
			c[0] = createMock(WSPasswordCallback.class);
			ppc.handle(c);
			fail("UnsupportedCallbackException should be thrown");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedCallbackException e) {
			// TODO Auto-generated catch block
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
		}		
	}
	
	@Test
	public void testProviderPasswordCallback() {
		try {
			ProviderPasswordCallback ppc = new ProviderPasswordCallback();
			WSPasswordCallback[] c = new WSPasswordCallback[1];
			c[0] = new  WSPasswordCallback("test", "", "", WSPasswordCallback.USERNAME_TOKEN_UNKNOWN);
			ppc.handle(c);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedCallbackException e) {
			// TODO Auto-generated catch block
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
		}		
	}
	
	@Test
	public void testHandleMessage() {
		ProviderPasswordCallback ppc = new ProviderPasswordCallback();
		String res = ppc.resetUsername();
		res = ppc.resetUsername();
		assertNull(res);
	}
}
