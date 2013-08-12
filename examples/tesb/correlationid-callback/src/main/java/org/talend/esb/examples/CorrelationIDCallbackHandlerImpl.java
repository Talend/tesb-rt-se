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
package org.talend.esb.examples;

import org.talend.esb.correlation.CorrelationIDCallbackHandler;

public class CorrelationIDCallbackHandlerImpl implements CorrelationIDCallbackHandler {

    public CorrelationIDCallbackHandlerImpl() {

    }

    public String getCorrelationId() {
        return "CorrelationIDValue";
    }
}
