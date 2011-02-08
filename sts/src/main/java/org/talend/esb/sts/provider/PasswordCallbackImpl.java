package org.talend.esb.sts.provider;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

public class PasswordCallbackImpl implements CallbackHandler {
//	private static final Log LOG = LogFactory.getLog(PasswordCallbackImpl.class.getName());
	
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
        
/*        try {
			throw new Exception(pc.getIdentifier());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.error(e);
		}
        if (pc.getIdentifier().equals("joe")) {
            // set the password on the callback. This will be compared to the
            // password which was sent from the client.
            pc.setPassword("password");
        }  else {
        	pc.setPassword("password");
        }*/
        GlobalUser.setUserName(pc.getIdentifier());
        GlobalUser.setUserPassword(pc.getPassword());
    }
}
