// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

import com.cognitect.transit.impl.AbstractParser;
import com.cognitect.transit.impl.JsonParser;
import com.cognitect.transit.impl.ReadCache;
import com.cognitect.transit.impl.WriteCache;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TransitJSONMachineModeTest extends TestCase {

    public TransitJSONMachineModeTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TransitJSONMachineModeTest.class);
    }

    // Reading

    public Reader reader(String s) throws IOException {

        InputStream in = new ByteArrayInputStream(s.getBytes());
        return TransitFactory.reader(TransitFactory.Format.JSON, in, null);
    }

    private long readTimeString(String timeString) throws IOException {
        // TODO: Impl date format
        return 0;
    }

    private SimpleDateFormat formatter(String formatString) {

        SimpleDateFormat df = new SimpleDateFormat(formatString);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df;
    }

    public void testReadTime() throws Exception {
        // TODO: Impl date format
    }

    public void testReadMap() throws IOException {
        Map m = (Map)reader("[\"~^\",\"foo\",1,\"bar\",2]").read();

        assertTrue(m instanceof HashMap);
        assertTrue(m.containsKey("foo"));
        assertTrue(m.containsKey("bar"));
        assertEquals(1L, m.get("foo"));
        assertEquals(2L, m.get("bar"));
    }

    public void testReadMapWithNested() throws IOException {
        Map m = (Map)reader("[\"~^\",\"foo\",1,\"bar\",[\"~^\",\"baz\",3]]").read();

        assertTrue(m instanceof HashMap);
        assertTrue(m.get("bar") instanceof HashMap);
        assertTrue(((Map)m.get("bar")).containsKey("baz"));
        assertEquals(3L, ((Map)m.get("bar")).get("baz"));
    }

    // Writing

    public String write(Object o) throws Exception {

        OutputStream out = new ByteArrayOutputStream();
        Writer w = TransitFactory.writer(TransitFactory.Format.JSON_MACHINE, out, null);
        w.write(o);
        return out.toString();

    }

    public boolean isEqual(Object o1, Object o2) {

        if(o1 instanceof Boolean)
            return o1 == o2;
        else
            return false;
    }

    public void testRoundTrip() throws Exception {

        Object inObject = true;

        OutputStream out = new ByteArrayOutputStream();
        Writer w = TransitFactory.writer(TransitFactory.Format.JSON_MACHINE, out, null);
        w.write(inObject);
        String s = out.toString();
        InputStream in = new ByteArrayInputStream(s.getBytes());
        Reader reader = TransitFactory.reader(TransitFactory.Format.JSON, in, null);
        Object outObject = reader.read();

        assertTrue(isEqual(inObject, outObject));
    }

    public void testWriteMap() throws Exception {

        Map m = new HashMap();
        m.put("foo", 1);
        m.put("bar", 2);

        assertEquals("[\"~^\",\"foo\",1,\"bar\",2]", write(m));
    }

    public void testWriteEmptyMap() throws Exception {

        Map m = new HashMap();
        assertEquals("[\"~^\"]", write(m));
    }
}
