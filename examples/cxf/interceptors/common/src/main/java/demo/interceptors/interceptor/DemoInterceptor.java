/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package demo.interceptors.interceptor;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamReader;

import org.apache.cxf.BusFactory;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.InterceptorChain;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptor;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.phase.PhaseManager;

/**
 * The DemoInterceptor just prints out various aspects of the Message that is
 * passed into the handleMessage call. That includes the contents, the
 * properties, the interceptors in the Chain, etc... By adding this interceptor
 * into various phases of the chain, you can see exactly how the Message
 * "changes" and is manipulated as it moves through the chain.
 */
public class DemoInterceptor implements PhaseInterceptor<Message> {
    private final String phase;

    public DemoInterceptor(String p) {
        phase = p;
    }

    /**
     * This method will add a DemoInterceptor into every in and every out phase
     * of the interceptor chains.
     * 
     * @param provider
     */
    public static void addInterceptors(InterceptorProvider provider) {
        PhaseManager phases = BusFactory.getDefaultBus().getExtension(PhaseManager.class);
        for (Phase p : phases.getInPhases()) {
            provider.getInInterceptors().add(new DemoInterceptor(p.getName()));
            provider.getInFaultInterceptors().add(new DemoInterceptor(p.getName()));
        }
        for (Phase p : phases.getOutPhases()) {
            provider.getOutInterceptors().add(new DemoInterceptor(p.getName()));
            provider.getOutFaultInterceptors().add(new DemoInterceptor(p.getName()));
        }
    }

    public void handleMessage(Message message) throws Fault {
        PhaseInterceptorChain pic = (PhaseInterceptorChain)message.getInterceptorChain();
        if (!somethingMayHaveChanged(pic)) {
            return;
        }
        System.out.println("Phase: " + phase);
        System.out.println("        out: " + MessageUtils.isOutbound(message));
        System.out.println("   contents: " + message.getContentFormats());
        System.out.println("       keys: " + message.keySet());
        XMLStreamReader reader = message.getContent(XMLStreamReader.class);
        if (reader != null) {
            // On an incoming message, once we have the XMLStreamReader,
            // we can get the current event and the element Name
            int event = reader.getEventType();
            switch (event) {
            case XMLStreamReader.START_ELEMENT:
            case XMLStreamReader.END_ELEMENT:
                System.out.println("      reader: " + event + "   qname: " + reader.getName());
                break;
            default:
                System.out.println("      reader: " + event);
            }
        }
        List<?> params = message.getContent(List.class);
        if (params != null) {
            System.out.println("      params: " + params);
        }
        System.out.println("      chain: ");
        printInterceptorChain(pic);
        System.out.println();
        System.out.println();
    }

    //just check to see if the previous interceptor was also an instanceof
    //DemoInterceptor.  If so, we don't really need to print anything 
    //as we know nothing has changed.
    private boolean somethingMayHaveChanged(PhaseInterceptorChain pic) {
        Iterator<Interceptor<? extends Message>> it = pic.iterator();
        Interceptor<? extends Message> last = null;
        while (it.hasNext()) {
            Interceptor<? extends Message> cur = it.next();
            if (cur == this) {
                if (last instanceof DemoInterceptor) {
                    return false;
                }
                return true;
            }
            last = cur;
        }
        return true;
    }

    /**
     * Prints out the interceptor chain in a format that is easy to read. It
     * also filters out instances of the DemoInterceptor so you can see what the
     * chain would look like in a normal invokation.
     * 
     * @param chain
     */
    public void printInterceptorChain(InterceptorChain chain) {
        Iterator<Interceptor<? extends Message>> it = chain.iterator();
        String phase = "";
        StringBuilder builder = null;
        while (it.hasNext()) {
            Interceptor<? extends Message> interceptor = it.next();
            if (interceptor instanceof DemoInterceptor) {
                continue;
            }
            if (interceptor instanceof PhaseInterceptor) {
                PhaseInterceptor pi = (PhaseInterceptor)interceptor;
                if (!phase.equals(pi.getPhase())) {
                    if (builder != null) {
                        System.out.println(builder.toString());
                    } else {
                        builder = new StringBuilder(100);
                    }
                    builder.setLength(0);
                    builder.append("             ");
                    builder.append(pi.getPhase());
                    builder.append(": ");
                    phase = pi.getPhase();
                }
                String id = pi.getId();
                int idx = id.lastIndexOf('.');
                if (idx != -1) {
                    id = id.substring(idx + 1);
                }
                builder.append(id);
                builder.append(' ');
            }
        }

    }

    public void handleFault(Message message) {
    }

    public Set<String> getAfter() {
        return Collections.emptySet();
    }

    public Set<String> getBefore() {
        return Collections.emptySet();
    }

    public String getId() {
        return DemoInterceptor.class.getName() + "." + phase;
    }

    public String getPhase() {
        return phase;
    }

    public Collection<PhaseInterceptor<? extends Message>> getAdditionalInterceptors() {
        return null; 
    }

}
