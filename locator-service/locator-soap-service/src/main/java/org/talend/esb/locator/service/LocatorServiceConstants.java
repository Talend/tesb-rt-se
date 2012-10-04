package org.talend.esb.locator.service;

public interface LocatorServiceConstants {

    // policy id
    String ID_POLICY_TOKEN = "org.talend.esb.job.token.policy";
    String ID_POLICY_SAML = "org.talend.esb.job.saml.policy";

    enum EsbSecurity {
        NO("NO"), TOKEN("TOKEN"), SAML("SAML");

        String esbSecurity;

        EsbSecurity(String esbSecurity) {
            this.esbSecurity = esbSecurity;
        }

        public static EsbSecurity fromString(String value) {
            if (null == value) {
                return NO;
            }
            for (EsbSecurity esbSecurity : EsbSecurity.values()) {
                if (esbSecurity.esbSecurity.equals(value)) {
                    return esbSecurity;
                }
            }
            throw new IllegalArgumentException("Unsupported secutity value: "
                    + value);
        }
    }
}
