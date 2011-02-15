package org.apache.esb.sts.client;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

public class ClientKeystorePasswordCallback implements CallbackHandler {

	@Override
	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		for (Callback callback : callbacks) {
			if(callback instanceof WSPasswordCallback) {
				WSPasswordCallback wsPasswordCallback = (WSPasswordCallback)callback;
				wsPasswordCallback.setPassword("anfang");
			}
		}
		
	}

}
