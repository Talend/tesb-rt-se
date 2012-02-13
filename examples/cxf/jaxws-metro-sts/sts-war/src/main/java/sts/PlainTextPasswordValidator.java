/**
 * Copyright (C) 2011 Talend Inc. - www.talend.com
 */
package sts;

import com.sun.xml.wss.impl.callback.PasswordValidationCallback;

public class PlainTextPasswordValidator implements
        PasswordValidationCallback.PasswordValidator {

    public boolean validate(PasswordValidationCallback.Request request)
            throws PasswordValidationCallback.PasswordValidationException {

        PasswordValidationCallback.PlainTextPasswordRequest plainTextRequest 
            = (PasswordValidationCallback.PlainTextPasswordRequest) request;
        if ("alice".equals(plainTextRequest.getUsername())
                && "clarinet".equals(plainTextRequest.getPassword())) {
            return true;
        } else if ("bob".equals(plainTextRequest.getUsername())
                && "trombone".equals(plainTextRequest.getPassword())) {
            return true;
        }
        return false;
    }
}

