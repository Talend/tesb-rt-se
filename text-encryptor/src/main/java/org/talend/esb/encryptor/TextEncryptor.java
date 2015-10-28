package org.talend.esb.encryptor;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.jasypt.properties.PropertyValueEncryptionUtils;

//TODO: Add description
@Command(scope = "tesb", name = "encrypt-text", description = "Encrypt text using Jasypt with BouncyCastle provider")
@Service
public class TextEncryptor implements Action {

    private static final String ALGORITHM = "PBEWITHSHA256AND128BITAES-CBC-BC";
    private static final String PASSWORD_ENV_NAME = "TESB_ENV_PASSWORD";
    private static final String PROVIDER_NAME = "BC";


    //TODO: Add description
    @Argument(index = 0,
            name = "TextToEncrypt",
            description = "Text, that need to be encrypted",
            required = true,
            multiValued = false)
    String textToEncrypt;

    //TODO: Add description
    @Argument(index = 1,
            name = "EncryptionPassword",
            description = "Password that will be used for encryption",
            required = false,
            multiValued = false)
    String encryptionPassword;

    @Override
    public Object execute() throws Exception {
        StandardPBEStringEncryptor enc = new StandardPBEStringEncryptor();
        EnvironmentStringPBEConfig env = new EnvironmentStringPBEConfig();
        env.setProvider(new BouncyCastleProvider());
        env.setProviderName(PROVIDER_NAME);
        env.setAlgorithm(ALGORITHM);
        if (encryptionPassword != null) {
            env.setPassword(encryptionPassword);
            System.out.println("Specified password for decryption should be set to " + PASSWORD_ENV_NAME + " env variable");
        } else {
            if (System.getenv(PASSWORD_ENV_NAME) != null) {
                env.setPasswordEnvName(PASSWORD_ENV_NAME);
            } else {
                System.out.println(PASSWORD_ENV_NAME + " system variable is not specified. ");
                System.out.println("Second parameter should be used to specify password.");
            }
        }
        enc.setConfig(env);
        System.out.println(PropertyValueEncryptionUtils.encrypt(textToEncrypt, enc));
        return null;
    }
}
