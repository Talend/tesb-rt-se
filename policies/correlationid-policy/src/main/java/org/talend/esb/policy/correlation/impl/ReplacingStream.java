package org.talend.esb.policy.correlation.impl;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;

public class ReplacingStream extends FilterInputStream {
	
	LinkedList<Integer> inQueue = new LinkedList<Integer>();
    LinkedList<Integer> outQueue = new LinkedList<Integer>();
    final byte[] search, replacement;

    protected ReplacingStream(InputStream in, byte[] search,
                                                   byte[] replacement) {
        super(in);
        this.search = search;
        this.replacement = replacement;
    }

    private boolean isMatchFound() {
        Iterator<Integer> inIter = inQueue.iterator();
        for (int i = 0; i < search.length; i++)
            if (!inIter.hasNext() || search[i] != inIter.next())
                return false;
        return true;
    }

    private void readAhead() throws IOException {
        while (inQueue.size() < search.length) {
            int next = super.read();
            inQueue.offer(next);
            if (next == -1)
                break;
        }
    }

    @Override
    public int read() throws IOException {
        if (outQueue.isEmpty()) {
            readAhead();
            if (isMatchFound()) {
                for (int i = 0; i < search.length; i++)
                    inQueue.remove();

                for (byte b : replacement)
                    outQueue.offer((int) b);
            } else
                outQueue.add(inQueue.remove());
        }
        return outQueue.remove();
    }
}
