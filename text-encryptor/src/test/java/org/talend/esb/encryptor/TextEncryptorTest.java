package org.talend.esb.encryptor;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.junit.Assert;
import org.junit.Test;

public class TextEncryptorTest {

	@Test
	public void testMdcMapperBusCreationListener() throws Exception {

		TextEncryptor encryptor = new TextEncryptor();
		encryptor.textToEncrypt = "testToEncrypr";
		encryptor.encryptionPassword = "encryptionPassword";

		try {
			Assert.assertNull(encryptor.execute());
		} catch (EncryptionOperationNotPossibleException ex) {
			// ignore (JCE is not installed on test env)
		}
	}

}