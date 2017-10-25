package org.apache.cxf.sts.war;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.junit.Assert;
import static org.junit.Assert.assertSame;
import org.junit.Test;

public class PasswordCallbackHandlerTest {

	@Test
	public void testPasswordCallbackHandler() throws Exception {

		PasswordCallbackHandler handler = new PasswordCallbackHandler();

		List<Callback> callbacksList = new ArrayList<Callback>();

		WSPasswordCallback callback = new WSPasswordCallback("id", 0);
		callback.setIdentifier("mystskey");
		callbacksList.add(callback);

		handler.handle(callbacksList.toArray(new Callback[callbacksList.size()]));

		assertSame("stskpass", callback.getPassword());

	}

	@Test
	public void testPasswordCallbackHandlerNotCorrectIdentifier() throws Exception {

		PasswordCallbackHandler handler = new PasswordCallbackHandler();

		List<Callback> callbacksList = new ArrayList<Callback>();

		WSPasswordCallback callback = new WSPasswordCallback("id", 0);
		callback.setIdentifier("notMystskey");
		callbacksList.add(callback);

		handler.handle(callbacksList.toArray(new Callback[callbacksList.size()]));

		Assert.assertNull(callback.getPassword());

	}

}