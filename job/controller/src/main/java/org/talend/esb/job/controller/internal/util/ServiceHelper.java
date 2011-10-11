/*
 * #%L
 * Talend :: ESB :: Job :: Controller
 * %%
 * Copyright (C) 2011 Talend Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.talend.esb.job.controller.internal.util;

import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;

import org.apache.cxf.Bus;
import org.apache.cxf.binding.soap.Soap12;
import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.binding.soap.model.SoapBindingInfo;
import org.apache.cxf.binding.soap.saaj.SAAJFactoryResolver;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.InterfaceInfo;
import org.apache.cxf.service.model.MessageInfo;
import org.apache.cxf.service.model.MessagePartInfo;
import org.apache.cxf.service.model.OperationInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.tools.common.extensions.soap.SoapOperation;
import org.apache.cxf.tools.util.SOAPBindingUtil;
import org.apache.cxf.wsdl.WSDLManager;

public final class ServiceHelper {

    private static final QName XSD_ANY_TYPE =
            new QName("http://www.w3.org/2001/XMLSchema", "anyType");

    private ServiceHelper() {
    }

    public static void addOperation(final ServiceInfo si,
            String operationName, boolean isRequestResponse) {
        final InterfaceInfo ii = si.getInterface();
        final String namespace = ii.getName().getNamespaceURI();

        final OperationInfo oi = ii.addOperation(
            new QName(namespace, operationName));
        final MessageInfo mii = oi.createMessage(
            new QName(namespace, operationName + "Request"),
            MessageInfo.Type.INPUT);
        oi.setInput(operationName + "Request", mii);
        MessagePartInfo mpi = mii.addMessagePart("request");
        mpi.setTypeQName(XSD_ANY_TYPE);
        if (isRequestResponse) {
            MessageInfo mio = oi.createMessage(
                new QName(namespace, operationName + "Response"),
                MessageInfo.Type.OUTPUT);
            oi.setOutput(operationName + "Response", mio);
            mpi = mio.addMessagePart("response");
            mpi.setTypeQName(XSD_ANY_TYPE);
        }

        final BindingInfo bi = si.getBindings().iterator().next();
        final BindingOperationInfo boi = new BindingOperationInfo(bi, oi);
        bi.addOperation(boi);
        if (bi instanceof SoapBindingInfo) {
//            SoapOperationInfo soi = new SoapOperationInfo();
//            soi.setAction(operationName);
//            boi.addExtensor(soi);

            // org.apache.cxf.binding.soap.SoapBindingFactory
            final SoapBindingInfo sbi = (SoapBindingInfo)bi;
            Bus bs = org.apache.cxf.bus.spring.SpringBusFactory.getDefaultBus();
            WSDLManager m = bs.getExtension(WSDLManager.class);
            ExtensionRegistry extensionRegistry = m.getExtensionRegistry();
            boolean isSoap12 = sbi.getSoapVersion() instanceof Soap12;
            try {
                SoapOperation soapOperation =
                    SOAPBindingUtil.createSoapOperation(extensionRegistry, isSoap12);
                soapOperation.setSoapActionURI(operationName/*soi.getAction()*/);
//                soapOperation.setStyle(soi.getStyle());
                boi.addExtensor(soapOperation);
            } catch (WSDLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void removeOperation(final ServiceInfo si, String operationName) {
        InterfaceInfo ii = si.getInterface();

        final String namespace = ii.getName().getNamespaceURI();
        OperationInfo oi = ii.getOperation(
            new QName(namespace, operationName));
        ii.removeOperation(oi);

        BindingInfo bi = si.getBindings().iterator().next();
        BindingOperationInfo boi = bi.getOperation(oi);
        bi.removeOperation(boi);
    }

    // org.apache.cxf.jaxws.JaxWsClientProxy
    public static SOAPFault createSoapFault(Exception ex) throws SOAPException {
        SOAPFault soapFault = SAAJFactoryResolver.createSOAPFactory(null).createFault(); 
        if (ex instanceof SoapFault) {
            if (!soapFault.getNamespaceURI().equals(((SoapFault)ex).getFaultCode().getNamespaceURI())
                && SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE
                    .equals(((SoapFault)ex).getFaultCode().getNamespaceURI())) {
                //change to 1.1
                try {
                    soapFault = SAAJFactoryResolver.createSOAPFactory(null).createFault();
                } catch (Throwable t) {
                    //ignore
                }
            }
            soapFault.setFaultString(((SoapFault)ex).getReason());
            soapFault.setFaultCode(((SoapFault)ex).getFaultCode());
            soapFault.setFaultActor(((SoapFault)ex).getRole());
            if (((SoapFault)ex).getSubCode() != null) {
                soapFault.appendFaultSubcode(((SoapFault)ex).getSubCode());
            }

            if (((SoapFault)ex).hasDetails()) {
                org.w3c.dom.Node nd = soapFault.getOwnerDocument().importNode(((SoapFault)ex).getDetail(),
                                                                  true);
                nd = nd.getFirstChild();
                soapFault.addDetail();
                while (nd != null) {
                    org.w3c.dom.Node next = nd.getNextSibling();
                    soapFault.getDetail().appendChild(nd);
                    nd = next;
                }
            }
        } else {
            String msg = ex.getMessage();
            if (msg != null) {
                soapFault.setFaultString(msg);
            }
        }      
        return soapFault;
    }

}
