package org.talend.esb.policy.compression.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import java.util.zip.GZIPOutputStream;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.MessageSenderInterceptor;
import org.apache.cxf.io.AbstractThresholdOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.policy.AssertionInfo;
import org.apache.neethi.Assertion;
import org.talend.esb.policy.compression.impl.internal.CompressionConstants;
import org.talend.esb.policy.compression.impl.internal.CompressionHelper;

/**
 *Interceptor that compresses outgoing messages using gzip.
 *This interceptor supports a compression
 * {@link #threshold} (default 1kB) - messages smaller than this threshold will
 * not be compressed. 
 */
public class CompressionOutInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final Logger LOG = LogUtils.getL7dLogger(CompressionOutInterceptor.class);

    /**
     * Compression threshold in bytes - messages smaller than this will not be
     * compressed.
     */
    private int threshold = CompressionConstants.TRESHOLD_ATTRIBUTE_DEFAULT;
    
    public CompressionOutInterceptor() {
        super(Phase.PREPARE_SEND);
        addAfter(MessageSenderInterceptor.class.getName());
    }
    public CompressionOutInterceptor(int threshold) {
        super(Phase.PREPARE_SEND);
        addAfter(MessageSenderInterceptor.class.getName());
        this.threshold = threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getThreshold() {
        return threshold;
    }
    
	public void handleMessage(Message message) throws Fault {
		try {
			
			// Load threshold value from policy assertion
			AssertionInfo ai = CompressionPolicyBuilder.getAssertion(message);
			if (ai != null){
				Assertion a = ai.getAssertion();
				if ( a instanceof CompressionAssertion) {
					this.setThreshold(((CompressionAssertion)a).getThreshold());
				}
			}
			
			wrapOriginalOutputStream(message);
			
			// Confirm policy processing
			if (ai != null){
				ai.setAsserted(true);
			}				

		}catch (RuntimeException e) {
			throw e;
		}catch (Exception e) {
			throw new Fault(e);
		}
	}
	
    public void wrapOriginalOutputStream(Message message) throws Fault {
            // remember the original output stream, we will write compressed
            // data to this later
            OutputStream os = message.getContent(OutputStream.class);
            if (os == null) {
                return;
            }

            // new stream to cache the original os
            CachedOutputStream wrapper = new CachedOutputStream();
            CachedOutputStreamCallback callback = new CompressionCachedOutputStreamCallback(os, threshold, message);
            wrapper.registerCallback(callback);
            message.setContent(OutputStream.class, wrapper);
    }
    
    public static class CompressionCachedOutputStreamCallback implements CachedOutputStreamCallback {
        private final OutputStream origOutStream;
        private final int threshold;
        private final Message message;

        public CompressionCachedOutputStreamCallback(OutputStream os, int threshold, Message message) {
            this.origOutStream = os;
            this.threshold=threshold;
            this.message = message;
        }

        @Override
        public void onFlush(CachedOutputStream wrapper) {            
        }
        
        public String getEncoding(){
        	return "base64";
        }
        
        public String getAlgoritm(){
        	return "gzip";
        }

        @Override
        public void onClose(CachedOutputStream wrapper) {
        	try {
            	//InputStream with actual SOAP message
            	InputStream wrappedIS = wrapper.getInputStream();
            	
            	if(MessageUtils.isFault(message)){
            		//Skipping compression of SOAP fault
            		wrappedIS.reset();
                	IOUtils.copy(wrappedIS, origOutStream);
            	}else{
                	//Loading SOAP body content to separate stream
                    CachedOutputStream soapBodyContent = new CachedOutputStream();
                    
        			Scanner scanner = new Scanner(wrappedIS);
        			MatchResult bodyPosition = null;
                    try {
                    	bodyPosition = CompressionHelper.loadSoapBodyContent(soapBodyContent, scanner, 
                    			CompressionConstants.SOAP_BODY_PATTERN);
                    	if(bodyPosition==null){
                    		throw new RuntimeException("Compression: SOAP body is not found");
                    	}
        			} catch (XMLStreamException e) {
        				throw new Fault("Can not load SOAP Body content for compression", LOG, e, e.getMessage());
        			}            	
                	
                    //Compressing soap body content
                    CachedOutputStream compressedSoapBody = new CachedOutputStream();
                    GZipThresholdOutputStream compressor 
                        = new GZipThresholdOutputStream(threshold,  compressedSoapBody, message);
                    IOUtils.copy(soapBodyContent.getInputStream(), compressor);
                    compressor.flush();
                    compressor.close();
                    
                    if(compressor.isThresholdReached()){
                    	// body compression was performed
                    	// apply Base64 encoding for compressed data
                        byte[] encodedBodyBytes = (new Base64()).encode(compressedSoapBody.getBytes());
                        
                        //copy original SOAP message with compressed (and base64 encoded) body content to
                        //original output stream
                        CompressionHelper.replaceBodyInSOAP(wrapper.getBytes(), 
                        		bodyPosition, 
                        		new ByteArrayInputStream(encodedBodyBytes), 
                        		origOutStream,  
                        		CompressionConstants.getCompressionWrapperStartTag(getAlgoritm(), getEncoding()), 
                        		CompressionConstants.getCompressionWrapperEndTag(), false);
                    }else{
                    	//compression was not performed (threshold is not reached)
                    	//skip base64 encoding
                    	//copy cached data from original SOAP to original stream
                    	wrappedIS.reset();
                    	IOUtils.copy(wrappedIS, origOutStream);
                    }            		
            	}
            	

     
            } catch (Exception e) {
                throw new Fault("Soap Body compression failed", LOG, e, e.getMessage());
            } finally {
                try {
                	origOutStream.flush();
                	origOutStream.close();
                } catch (IOException e) {
                    LOG.warning("Cannot close stream after compression: " + e.getMessage());
                } catch (IllegalStateException e) {
                	// Exception caught and logged, as it is frequently hiding a previously thrown exception.
                	LOG.log(Level.SEVERE, "Irregular attempt at closing output stream after compression", e);
                }
            }
        }
    }
    
    static class GZipThresholdOutputStream extends AbstractThresholdOutputStream {
        Message message;
        private boolean thresholdReached = false;
        
        public GZipThresholdOutputStream(int t, OutputStream orig,
                                          Message msg) {
            super(t);
            super.wrappedStream = orig;
            message = msg;
        }
        
        @Override
        public void thresholdNotReached() {
        	thresholdReached = false;
            LOG.fine("Message is smaller than compression threshold, not compressing.");
        }

        @Override
        public void thresholdReached() throws IOException {
        	thresholdReached = true;
            LOG.fine("Compressing message.");
            GZIPOutputStream zipOutput = new GZIPOutputStream(wrappedStream);
            wrappedStream = zipOutput;
        }
        
        public boolean isThresholdReached(){
        	return thresholdReached;
        }
    }
}
