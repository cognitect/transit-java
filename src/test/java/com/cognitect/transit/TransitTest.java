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
import java.util.*;

public class TransitTest extends TestCase {

    public TransitTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TransitTest.class);
    }

    // Reading

    public Reader reader(String s) throws IOException {

        InputStream in = new ByteArrayInputStream(s.getBytes());
        return Reader.getJsonInstance(in, null);
    }

    public void testReadString() throws IOException {

        assertEquals("foo", reader("\"foo\"").read());
        assertEquals("~foo", reader("\"~~foo\"").read());
        assertEquals("`foo", reader("\"~`foo\"").read());
        assertEquals("~#foo", reader("\"~#foo\"").read());
        assertEquals("^foo", reader("\"~^foo\"").read());
    }

    public void testReadBoolean() throws IOException {

        assertTrue((Boolean) reader("\"~?t\"").read());
        assertFalse((Boolean) reader("\"~?f\"").read());
    }

    public void testReadNull() throws IOException {

        assertNull(reader("\"~_\"").read());
    }

    public void testReadKeyword() throws IOException {

        Object v = reader("\"~:foo\"").read();
        assertEquals("foo", v.toString());
        assertEquals("foo", ((Keyword)v).value);

        List v2 = (List)reader("[\"~:foo\",\"^"+(char)33+"\",\"^"+(char)33+"\"]").read();
        assertEquals("foo", ((Keyword)v2.get(0)).value);
        assertEquals("foo", ((Keyword)v2.get(1)).value);
        assertEquals("foo", ((Keyword)v2.get(2)).value);
    }

    public void testReadInteger() throws IOException {

        Reader r = reader("\"~i42\"");
        assertEquals(42L, r.read());
        r = reader("\"~i4256768765123454321897654321234567\"");
        assertEquals(0, (new BigInteger("4256768765123454321897654321234567")).compareTo(
                          (BigInteger)r.read()));
    }

    public void testReadDouble() throws IOException {

        assertEquals(new Double("42.5"), reader("\"~d42.5\"").read());
    }

    public void testReadBigDecimal() throws IOException {

        assertEquals(0, (new BigDecimal("42.5")).compareTo(
                          (BigDecimal)reader("\"~f42.5\"").read()));
    }

    public void testReadTime() throws IOException {

        Date d = new Date();
        long t = d.getTime();
        String timeString = JsonParser.dateTimeFormat.format(d);

        assertEquals(t, ((Date) reader("\"~t" + timeString + "\"").read()).getTime());
        assertEquals(t, ((Date) reader("{\"~#t\": " + t + "}").read()).getTime());
    }

    public void testReadUUID() throws IOException {

        UUID uuid = UUID.randomUUID();
        long hi64 = uuid.getMostSignificantBits();
        long lo64 = uuid.getLeastSignificantBits();

        assertEquals(0, uuid.compareTo((UUID)reader("\"~u" + uuid.toString() + "\"").read()));
        assertEquals(0, uuid.compareTo((UUID)reader("{\"~#u\": [" + hi64 + ", " + lo64 + "]}").read()));
    }

    public void testReadURI() throws URISyntaxException, IOException {

        URI uri = new URI("http://www.foo.com");

        assertEquals(0, uri.compareTo((URI)reader("\"~rhttp://www.foo.com\"").read()));
    }

    public void testReadSymbol() throws IOException {

        Reader r = reader("\"~$foo\"");
        Object v = r.read();
        assertEquals("foo", v.toString());
        assertEquals("foo", ((Symbol)v).value);
    }

    public void testReadCharacter() throws IOException {

        assertEquals('f', reader("\"~cf\"").read());
    }

    public void testReadBinary() throws IOException {

        byte[] bytes = "foobarbaz".getBytes();
        byte[] encodedBytes = Base64.encodeBase64(bytes);
        byte[] decoded = (byte[])reader("\"~b" + new String(encodedBytes) + "\"").read();

        assertEquals(bytes.length, decoded.length);

        boolean same = true;
        for(int i=0;i<bytes.length;i++) {
            if(bytes[i]!=decoded[i])
                same = false;
        }

        assertTrue(same);
    }

    public void testReadUnknown() throws IOException {

        assertEquals("~jfoo", reader("\"~jfoo\"").read());
    }

    public void testReadArray() throws IOException {

        List l = (List)reader("[1, 2, 3]").read();

        assertTrue(l instanceof ArrayList);
        assertEquals(3, l.size());

        assertEquals(1L, l.get(0));
        assertEquals(2L, l.get(1));
        assertEquals(3L, l.get(2));
    }

    public void testReadPrimitiveArrays() throws IOException {

        int[] ai = (int[])reader("{\"~#ints\":[1,2]}").read();
        assertEquals(ai[0], 1);
        assertEquals(ai[1], 2);
        long[] al = (long[])reader("{\"~#longs\":[1,2]}").read();
        assertEquals(al[0], 1L);
        assertEquals(al[1], 2L);
        float[] af = (float[])reader("{\"~#floats\":[1.7,2.5]}").read();
        assertEquals(af[0], 1.7f);
        assertEquals(af[1], 2.5f);
        boolean[] ab = (boolean[])reader("{\"~#bools\":[true,false]}").read();
        assertEquals(ab[0], true);
        assertEquals(ab[1], false);
        double[] ad = (double[])reader("{\"~#doubles\":[1.78,2.59]}").read();
        assertEquals(ad[0], 1.78d);
        assertEquals(ad[1], 2.59d);
        short[] as = (short[])reader("{\"~#shorts\":[1,2]}").read();
        assertEquals(as[0], (short)1);
        assertEquals(as[1], (short)2);
        char[] ac = (char[])reader("{\"~#chars\":[1,2]}").read();
        assertEquals(ac[0], (char)1);
        assertEquals(ac[1], (char)2);
    }

    public void testReadArrayWithNested() throws IOException {

        Date d = new Date();
        String t = JsonParser.dateTimeFormat.format(d);

        List l = (List)reader("[\"~:foo\", \"~t" + t + "\", \"~?t\"]").read();

        assertEquals(3, l.size());

        assertEquals("foo", ((Keyword)l.get(0)).value);
        assertEquals(d.getTime(), ((Date)l.get(1)).getTime());
        assertTrue((Boolean) l.get(2));
    }

    public void testReadMap() throws IOException {

        Map m = (Map)reader("{\"a\": 2, \"b\": 4}").read();

        assertEquals(2, m.size());

        assertEquals(2L, m.get("a"));
        assertEquals(4L, m.get("b"));
    }

    public void testReadMapWithNested() throws IOException {

        String uuid = UUID.randomUUID().toString();

        Map m = (Map)reader("{\"a\": \"~:foo\", \"b\": \"~u" + uuid + "\"}").read();

        assertEquals(2, m.size());

        assertEquals("foo", ((Keyword)m.get("a")).value);
        assertEquals(uuid, m.get("b").toString());
    }

    public void testReadSet() throws IOException {

        Set s = (Set)reader("{\"~#set\": [1, 2, 3]}").read();

        assertEquals(3, s.size());

        assertTrue(s.contains(1L));
        assertTrue(s.contains(2L));
        assertTrue(s.contains(3L));
    }

    public void testReadList() throws IOException {

        List l = (List)reader("{\"~#list\": [1, 2, 3]}").read();

        assertTrue(l instanceof LinkedList);
        assertEquals(3, l.size());

        assertEquals(1L, l.get(0));
        assertEquals(2L, l.get(1));
        assertEquals(3L, l.get(2));
    }

    public void testReadUnknownTaggedMap() throws IOException {

        Map m = (Map)reader("{\"~#foo\": [1, 2, 3]}").read();

        assertTrue(m instanceof HashMap);
        assertEquals(1, m.size());

        List l = (List)m.get("~#foo");

        assertEquals(3, l.size());

        assertEquals(1L, l.get(0));
        assertEquals(2L, l.get(1));
        assertEquals(3L, l.get(2));
    }

    public void testReadRatio() throws IOException {

        Ratio r = (Ratio)reader("{\"~#ratio\": [1,2]}").read();

        assertEquals(1L, r.numerator);
        assertEquals(2L, r.denominator);
        assertEquals(0.5d, r.doubleValue(), 0.01d);
    }

    public void testReadCmap() throws IOException {

        Map m = (Map)reader("{\"~#cmap\": [{\"~#ratio\":[1,2]},1,{\"~#list\":[1,2,3]},2]}").read();

        assertEquals(2, m.size());

        Iterator<Map.Entry> i = m.entrySet().iterator();
        while(i.hasNext()) {
            Map.Entry e = i.next();
            if((Long)e.getValue() == 1L) {
                Ratio r = (Ratio)e.getKey();
                assertEquals(1L, r.numerator);
                assertEquals(2L, r.denominator);
            }
            else if((Long)e.getValue() == 2L) {
                List l = (List)e.getKey();
                assertEquals(1L, l.get(0));
                assertEquals(2L, l.get(1));
                assertEquals(3L, l.get(2));
            }
        }
    }

    public void testReadMany() throws IOException {

        Reader r = reader("true null false \"foo\" 42.2 42");
        assertTrue((Boolean)r.read());
        assertNull(r.read());
        assertFalse((Boolean) r.read());
        assertEquals("foo", r.read());
        assertEquals(42.2, r.read());
        assertEquals(42L, r.read());
    }

    public void testReadCache() {

        ReadCache rc = new ReadCache();
        assertEquals("~:foo", rc.cacheRead("~:foo", false));
        assertEquals("~:foo", rc.cacheRead("^" + (char) 33, false));
        assertEquals("~$bar", rc.cacheRead("~$bar", false));
        assertEquals("~$bar", rc.cacheRead("^" + (char) 34, false));
        assertEquals("~#baz", rc.cacheRead("~#baz", false));
        assertEquals("~#baz", rc.cacheRead("^" + (char) 35, false));
        assertEquals("foobar", rc.cacheRead("foobar", false));
        assertEquals("foobar", rc.cacheRead("foobar", false));
        assertEquals("foobar", rc.cacheRead("foobar", true));
        assertEquals("foobar", rc.cacheRead("^" + (char) 36, true));
        assertEquals("abc", rc.cacheRead("abc", false));
        assertEquals("abc", rc.cacheRead("abc", false));
        assertEquals("abc", rc.cacheRead("abc", true));
        assertEquals("abc", rc.cacheRead("abc", true));
    }

    // Writing

    public String write(Object o) throws Exception {

        OutputStream out = new ByteArrayOutputStream();
        Writer w = Writer.getJsonInstance(out, null);
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
        Writer w = Writer.getJsonInstance(out, null);
        w.write(inObject);
        String s = out.toString();
        InputStream in = new ByteArrayInputStream(s.getBytes());
        Reader reader = Reader.getJsonInstance(in, null);
        Object outObject = reader.read();

        assertTrue(isEqual(inObject, outObject));
    }

    public void testWriteNull() throws Exception {

        assertEquals("null", write(null));
    }

    public void testWriteKeyword() throws Exception {

        assertEquals("\"~:foo\"", write(new Keyword("foo")));

        List l = new ArrayList();
        l.add(new Keyword("foo"));
        l.add(new Keyword("foo"));
        l.add(new Keyword("foo"));
        assertEquals("[\"~:foo\",\"^" + (char) 33 + "\",\"^" + (char) 33 + "\"]", write(l));
    }

    public void testWriteString() throws Exception {

        assertEquals("\"foo\"", write("foo"));
        assertEquals("\"~~foo\"", write("~foo"));
    }

    public void testWriteBoolean() throws Exception {

        assertEquals("true", write(true));
        assertEquals("false", write(false));
    }

    public void testWriteInteger() throws Exception {

        assertEquals("42", write(42));
        assertEquals("42", write(42L));
        assertEquals("42", write(new Byte("42")));
        assertEquals("42", write(new Short("42")));
        assertEquals("42", write(new Integer("42")));
        assertEquals("42", write(new Long("42")));
        assertEquals("42", write(new BigInteger("42")));
        assertEquals("\"~i4256768765123454321897654321234567\"", write(new BigInteger("4256768765123454321897654321234567")));
    }

    public void testWriteFloatDouble() throws Exception {

        assertEquals("42.5", write(42.5));
        assertEquals("42.5", write(new Float("42.5")));
        assertEquals("42.5", write(new Double("42.5")));
    }

    public void testWriteBigDecimal() throws Exception {

        assertEquals("\"~f42.5\"", write(new BigDecimal("42.5")));
    }

    public void testWriteTime() throws Exception {

        Date d = new Date();
        String dateString = AbstractParser.dateTimeFormat.format(d);
        assertEquals("\"~t" + dateString + "\"", write(d));
    }

    public void testWriteUUID() throws Exception {

        UUID uuid = UUID.randomUUID();

        assertEquals("\"~u" + uuid.toString() + "\"", write(uuid));
    }

    public void testWriteURI() throws Exception {

        URI uri = new URI("http://www.foo.com");

        assertEquals("\"~rhttp://www.foo.com\"", write(uri));
    }

    public void testWriteBinary() throws Exception {

        byte[] bytes = "foobarbaz".getBytes();
        byte[] encodedBytes = Base64.encodeBase64(bytes);

        assertEquals("\"~b" + new String(encodedBytes) + "\"", write(bytes));
    }

    public void testWriteSymbol() throws Exception {

        assertEquals("\"~$foo\"", write(new Symbol("foo")));
    }

    public void testWriteArray() throws Exception {

        List l = new ArrayList();
        l.add(1);
        l.add(2);
        l.add(3);

        assertEquals("[1,2,3]", write(l));
    }

    public void testWritePrimitiveArrays() throws Exception {

        int[] ints = {1,2};
        assertEquals("{\"~#ints\":[1,2]}", write(ints));
        long[] longs = {1L,2L};
        assertEquals("{\"~#longs\":[1,2]}", write(longs));
        float[] floats = {1.5f,2.78f};
        assertEquals("{\"~#floats\":[1.5,2.78]}", write(floats));
        boolean[] bools = {true,false};
        assertEquals("{\"~#bools\":[true,false]}", write(bools));
        double[] doubles = {1.654d,2.8765d};
        assertEquals("{\"~#doubles\":[1.654,2.8765]}", write(doubles));
        short[] shorts = {1,2};
        assertEquals("{\"~#shorts\":[1,2]}", write(shorts));
        char[] chars = {53,47};
        assertEquals("{\"~#chars\":[\"~c5\",\"~c/\"]}", write(chars));
    }

    public void testWriteMap() throws Exception {

        Map m = new HashMap();
        m.put("foo", 1);
        m.put("bar", 2);

        assertEquals("{\"foo\":1,\"bar\":2}", write(m));
    }

    public void testWriteSet() throws Exception {

        Set s = new HashSet();
        s.add("foo");
        s.add("bar");

        assertEquals("{\"~#set\":[\"foo\",\"bar\"]}", write(s));
    }

    public void testWriteList() throws Exception {

        List l = new LinkedList();
        l.add("foo");
        l.add("bar");

        assertEquals("{\"~#list\":[\"foo\",\"bar\"]}", write(l));
    }

    public void testWriteCharacter() throws Exception {

        assertEquals("\"~cf\"", write('f'));
    }

    public void testWriteRatio() throws Exception {

        Ratio r = new Ratio(1, 2);

        assertEquals("{\"~#ratio\":[1,2]}", write(r));
    }

    public void testWriteCmap() throws Exception {

        Ratio r = new Ratio(1, 2);
        Map m = new HashMap();
        m.put(r, 1);
        assertEquals("{\"~#cmap\":[{\"~#ratio\":[1,2]},1]}", write(m));
    }

    public void testWriteCache() {

        WriteCache wc = new WriteCache();
        assertEquals("~:foo", wc.cacheWrite("~:foo", false));
        assertEquals("^" + (char)33, wc.cacheWrite("~:foo", false));
        assertEquals("~$bar", wc.cacheWrite("~$bar", false));
        assertEquals("^" + (char)34, wc.cacheWrite("~$bar", false));
        assertEquals("~#baz", wc.cacheWrite("~#baz", false));
        assertEquals("^" + (char)35, wc.cacheWrite("~#baz", false));
        assertEquals("foobar", wc.cacheWrite("foobar", false));
        assertEquals("foobar", wc.cacheWrite("foobar", false));
        assertEquals("foobar", wc.cacheWrite("foobar", true));
        assertEquals("^" + (char)36, wc.cacheWrite("foobar", true));
        assertEquals("abc", wc.cacheWrite("abc", false));
        assertEquals("abc", wc.cacheWrite("abc", false));
        assertEquals("abc", wc.cacheWrite("abc", true));
        assertEquals("abc", wc.cacheWrite("abc", true));
    }
}
