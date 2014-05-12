/*
 * ============================================================================
 *
 * Copyright (C) 2011 - 2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 *
 * ============================================================================
 */
package org.talend.esb.callcontext.store.common.exception;

public class CallContextStoreException extends RuntimeException {

    /**
    *
    */
   private static final long serialVersionUID = 8902450708514783397L;

   public CallContextStoreException(String message) {
       super(message);
   }

   public CallContextStoreException(String string, Throwable e) {
       super(string, e);
   }

}
