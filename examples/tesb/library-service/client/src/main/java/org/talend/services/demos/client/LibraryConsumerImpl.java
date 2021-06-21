/*
 * ============================================================================
 *
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 5/7 rue Salomon De Rothschild, 92150 Suresnes, France
 *
 * ============================================================================
 */
package org.talend.services.demos.client;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceProvider;

import org.apache.cxf.feature.Features;
import org.talend.esb.mep.requestcallback.feature.CallContext;
import org.talend.services.demos.common.Utils;
import org.talend.services.demos.library._1_0.LibraryConsumer;
import org.talend.types.demos.generalobjects.errorhandling._1.ExceptionFrame;
import org.talend.types.demos.library.common._1.ListOfBooks;

@WebServiceProvider
@Features(features = { "org.talend.esb.mep.requestcallback.feature.RequestCallbackFeature" })
public class LibraryConsumerImpl implements LibraryConsumer {

	private static BlockingQueue<Object> responses = new ArrayBlockingQueue<Object>(8);
    @Resource
    private WebServiceContext wsContext;

    public void seekBookInBasementResponse(ListOfBooks body) {
        System.out.println("*************************************************************************************");
        System.out.println("*** seekBookInBasementResponse response (Request-Callback operation) was received ***");
        System.out.println("*************************************************************************************\n");

        Utils.showBooks(body);

        CallContext ctx = CallContext.getCallContext(wsContext.getMessageContext());
        System.out.println("Info from CallContext:");
        if (ctx == null) {
        	System.out.println("- no CallContext");
        } else {
	        System.out.println("- Call ID is " + ctx.getCallId());
	        System.out.println("- Callback ID is " + ctx.getCallbackId());
        }
        responses.offer(body);
    }

    public void seekBookInBasementFault(ExceptionFrame exception) {
        System.out.println("*************************************************************************************");
        System.out.println("*** seekBookInBasementFault response (Request-Callback operation) was received ******");
        System.out.println("*************************************************************************************\n");

        Utils.showExceptionFrame(exception);

        CallContext ctx = CallContext.getCallContext(wsContext.getMessageContext());
        System.out.println("Info from CallContext:");
        if (ctx == null) {
        	System.out.println("- no CallContext");
        } else {
	        System.out.println("- Call ID is " + ctx.getCallId());
	        System.out.println("- Callback ID is " + ctx.getCallbackId());
        }
        responses.offer(exception);
    }

    public static Object waitForResponse() throws InterruptedException {
    	return responses.take();
    }
}
