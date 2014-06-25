// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;
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

    public void testReadTime() throws Exception {
        Date d = new Date();
        long t = d.getTime();
        Date dt = ((Date)reader("\"~m" + t + "\"").read());
        assertEquals(t, dt.getTime());

        List l = ((List)reader("[\"~m" + t + "\"]").read());
        dt = (Date) l.get(0);

        assertEquals(t, dt.getTime());

        List human = ((List)reader("[\"~t1776-07-04T12:00:00.000Z\",\"~t1970-01-01T00:00:00.000Z\",\"~t2000-01-01T12:00:00.000Z\",\"~t2014-04-07T22:17:17.000Z\"]").read());
        assertEquals(4, human.size());

        List machine = ((List)reader("[\"~m-6106017600000\",\"~m0\",\"~m946728000000\",\"~m1396909037000\"]").read());
        assertEquals(4, machine.size());

        for (int i = 0; i < human.size(); i++) {
            Date dh = (Date) human.get(i);
            Date dm = (Date) machine.get(i);

            assertEquals(dh.compareTo(dm), 0);
        }
    }

    public void testReadMap() throws IOException {
        Map m = (Map)reader("[\"^ \",\"foo\",1,\"bar\",2]").read();

        assertTrue(m instanceof HashMap);
        assertTrue(m.containsKey("foo"));
        assertTrue(m.containsKey("bar"));
        assertEquals(1L, m.get("foo"));
        assertEquals(2L, m.get("bar"));
    }

    public void testReadMapWithNested() throws IOException {
        Map m = (Map)reader("[\"^ \",\"foo\",1,\"bar\",[\"^ \",\"baz\",3]]").read();

        assertTrue(m instanceof HashMap);
        assertTrue(m.get("bar") instanceof HashMap);
        assertTrue(((Map)m.get("bar")).containsKey("baz"));
        assertEquals(3L, ((Map)m.get("bar")).get("baz"));
    }

    // Writing

    public String write(Object o) throws Exception {

        OutputStream out = new ByteArrayOutputStream();
        Writer w = TransitFactory.writer(TransitFactory.Format.JSON, out, null);
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
        Writer w = TransitFactory.writer(TransitFactory.Format.JSON, out, null);
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

        assertEquals("[\"^ \",\"foo\",1]", write(m));

        Map m2 = new LinkedHashMap();
        m2.put("foo", 1);
        m2.put("bar", 2);

        assertEquals("[\"^ \",\"foo\",1,\"bar\",2]", write(m2));
    }

    public void testWriteEmptyMap() throws Exception {

        Map m = new HashMap();
        assertEquals("[\"^ \"]", write(m));
    }

    public String scalar(String value) {
        return "{\"~#'\":"+value+"}";
    }

    public void testWriteTime() throws Exception {

        final Date d = new Date();
        long tm = d.getTime();
        String t = write(d);
        List l = new ArrayList() {{ add(d); }};

        assertEquals(scalar("\"~m" + tm + "\""), t);

        t = write(l);
        assertEquals("[\"~m" + tm + "\"]", t);

        final Date da[] = {new Date(-6106017600000l),
                           new Date(0),
                           new Date(946728000000l),
                           new Date(1396909037000l)};

        l = Arrays.asList(da);
        t = write(l);
        assertEquals( "[\"~m" + da[0].getTime() + "\","
                     + "\"~m" + da[1].getTime() + "\","
                     + "\"~m" + da[2].getTime() + "\","
                     + "\"~m" + da[3].getTime() + "\"]", t);
    }

}
