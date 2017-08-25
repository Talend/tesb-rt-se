package org.talend.esb.mep.requestcallback.test.internal;


import static org.junit.Assert.*;
import org.junit.Test;
import org.talend.esb.mep.requestcallback.impl.callcontext.Base64Coder;

public class Base64CoderTest {


    @Test
    public void test() {
        final String initial = "Was it a cat I saw?";

        String result = Base64Coder.encodeString(initial);
        assertEquals(initial, Base64Coder.decodeString(result));

        char[] resultByte = Base64Coder.encode(initial.getBytes());
        assertEquals(initial, new String(Base64Coder.decode(resultByte)));

        resultByte = Base64Coder.encode(initial.getBytes(), initial.getBytes().length);
        assertEquals(initial, new String(Base64Coder.decode(resultByte, 0, resultByte.length)));

        result = Base64Coder.encodeLines(initial.getBytes());
        assertEquals(initial, new String(Base64Coder.decodeLines(result)));
    }

}
