package org.talend.esb.policy.compression.impl;

import java.util.Arrays;
import org.apache.cxf.ws.policy.AbstractPolicyInterceptorProvider;

/**
 * The Class CompressionInterceptorProvider.
 */
public class CompressionInterceptorProvider extends AbstractPolicyInterceptorProvider {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5698743589425687361L;
   
    /**
     * Instantiates a new compression interceptor provider.
     */
    public CompressionInterceptorProvider() {
        super(Arrays.asList(CompressionPolicyBuilder.COMPRESSION));
        
        CompressionOutInterceptor out = new CompressionOutInterceptor();
        CompressionInInterceptor in = new CompressionInInterceptor();

        remove(this.getOutInterceptors());
        
        this.getOutInterceptors().add(out);
        
        this.getInInterceptors().add(in);
   }        
} 
