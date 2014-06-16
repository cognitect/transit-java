// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

import com.cognitect.transit.impl.*;
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
        return TransitFactory.reader(TransitFactory.Format.JSON, in, null);
    }

    public void testReadString() throws IOException {

        assertEquals("foo", reader("\"foo\"").read());
        assertEquals("~foo", reader("\"~~foo\"").read());
        assertEquals("`foo", reader("\"~`foo\"").read());
        assertEquals("~#foo", reader("\"~#foo\"").read());
        assertEquals("^foo", reader("\"~^foo\"").read());
    }

    public void testReadBoolean() throws IOException {

        assertTrue((Boolean)reader("\"~?t\"").read());
        assertFalse((Boolean)reader("\"~?f\"").read());

        Map m = (Map)reader("{\"~?t\":1,\"~?f\":2}").read();
        assertEquals(1L, m.get(true));
        assertEquals(2L, m.get(false));
    }

    public void testReadNull() throws IOException {

        assertNull(reader("\"~_\"").read());
    }

    public void testReadKeyword() throws IOException {

        Object v = reader("\"~:foo\"").read();
        assertEquals("foo", v.toString());

        List v2 = (List)reader("[\"~:foo\",\"^"+(char)33+"\",\"^"+(char)33+"\"]").read();
        assertEquals("foo", v2.get(0).toString());
        assertEquals("foo", v2.get(1).toString());
        assertEquals("foo", v2.get(2).toString());
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

    private long readTimeString(String timeString) throws IOException {
        return ((Date)reader("\"~t" + timeString + "\"").read()).getTime();
    }

    private SimpleDateFormat formatter(String formatString) {

        SimpleDateFormat df = new SimpleDateFormat(formatString);
        df.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        return df;
    }

    private void assertReadsFormat(String formatString) throws Exception {

        Date d = new Date();
        SimpleDateFormat df = formatter(formatString);
        String ds = df.format(d);
        assertEquals(df.parse(ds).getTime(), readTimeString(ds));
    }

    public void testReadTime() throws Exception {

        Date d = new Date();
        long t = d.getTime();
        String timeString = JsonParser.dateTimeFormat.format(d);

        assertEquals(t, readTimeString(timeString));

        assertEquals(t, ((Date)reader("{\"~#m\": " + t + "}").read()).getTime());

        assertReadsFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        assertReadsFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        assertReadsFormat("yyyy-MM-dd'T'HH:mm:ss.SSS-00:00");
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

        assertEquals("`jfoo", reader("\"~jfoo\"").read());
        List l = Arrays.asList(1L, 2L);
        assertEquals(TransitFactory.taggedValue("point", l), reader("{\"~#point\":[1,2]}").read());
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

        assertEquals("foo", l.get(0).toString());
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

        assertEquals("foo", m.get("a").toString());
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

    public String write(Object o, TransitFactory.Format format) throws Exception {
        OutputStream out = new ByteArrayOutputStream();
        Writer w = TransitFactory.writer(format, out, null);
        w.write(o);
        return out.toString();
    }

    public String writeHuman(Object o) throws Exception {
        return write(o, TransitFactory.Format.JSON_VERBOSE);
    }

    public String writeMachine(Object o) throws Exception {
        return write(o, TransitFactory.Format.JSON);
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
        Writer w = TransitFactory.writer(TransitFactory.Format.JSON_VERBOSE, out, null);
        w.write(inObject);
        String s = out.toString();
        InputStream in = new ByteArrayInputStream(s.getBytes());
        Reader reader = TransitFactory.reader(TransitFactory.Format.JSON, in, null);
        Object outObject = reader.read();

        assertTrue(isEqual(inObject, outObject));
    }

    public String scalar(String value) {
        return "{\"~#'\":"+value+"}";
    }

    public void testWriteNull() throws Exception {

        assertEquals(scalar("null"), writeHuman(null));
        assertEquals(scalar("null"), writeMachine(null));
    }

    public void testWriteKeyword() throws Exception {

        assertEquals(scalar("\"~:foo\""), writeHuman(TransitFactory.keyword("foo")));
        assertEquals(scalar("\"~:foo\""), writeMachine(TransitFactory.keyword("foo")));

        List l = new ArrayList();
        l.add(TransitFactory.keyword("foo"));
        l.add(TransitFactory.keyword("foo"));
        l.add(TransitFactory.keyword("foo"));
        assertEquals("[\"~:foo\",\"~:foo\",\"~:foo\"]", writeHuman(l));
        assertEquals("[\"~:foo\",\"^!\",\"^!\"]", writeMachine(l));
    }

    public void testWriteString() throws Exception {

        assertEquals(scalar("\"foo\""), writeHuman("foo"));
        assertEquals(scalar("\"foo\""), writeMachine("foo"));
        assertEquals(scalar("\"~~foo\""), writeHuman("~foo"));
        assertEquals(scalar("\"~~foo\""), writeMachine("~foo"));
    }

    public void testWriteBoolean() throws Exception {

        assertEquals(scalar("true"), writeHuman(true));
        assertEquals(scalar("true"), writeMachine(true));
        assertEquals(scalar("false"), writeMachine(false));

        Map m = new HashMap();
        m.put(true, 1);
        assertEquals("{\"~?t\":1}", writeHuman(m));
        assertEquals("[\"^ \",\"~?t\",1]", writeMachine(m));
        Map m2 = new HashMap();
        m2.put(false, 1);
        assertEquals("{\"~?f\":1}", writeHuman(m2));
        assertEquals("[\"^ \",\"~?f\",1]", writeMachine(m2));
    }

    public void testWriteInteger() throws Exception {

        assertEquals(scalar("42"), writeHuman(42));
        assertEquals(scalar("42"), writeHuman(42L));
        assertEquals(scalar("42"), writeHuman(new Byte("42")));
        assertEquals(scalar("42"), writeHuman(new Short("42")));
        assertEquals(scalar("42"), writeHuman(new Integer("42")));
        assertEquals(scalar("42"), writeHuman(new Long("42")));
        assertEquals(scalar("42"), writeHuman(new BigInteger("42")));
        assertEquals(scalar("\"~i4256768765123454321897654321234567\""), writeHuman(new BigInteger("4256768765123454321897654321234567")));
    }

    public void testWriteFloatDouble() throws Exception {

        assertEquals(scalar("42.5"), writeHuman(42.5));
        assertEquals(scalar("42.5"), writeHuman(new Float("42.5")));
        assertEquals(scalar("42.5"), writeHuman(new Double("42.5")));
    }

    public void testWriteBigDecimal() throws Exception {

        assertEquals(scalar("\"~f42.5\""), writeHuman(new BigDecimal("42.5")));
    }

    public void testWriteTime() throws Exception {

        Date d = new Date();
        String dateString = AbstractParser.dateTimeFormat.format(d);
        long dateLong = d.getTime();
        assertEquals(scalar("\"~t" + dateString + "\""), writeHuman(d));
        assertEquals(scalar("\"~m" + dateLong + "\""), writeMachine(d));
    }

    public void testWriteUUID() throws Exception {

        UUID uuid = UUID.randomUUID();

        assertEquals(scalar("\"~u" + uuid.toString() + "\""), writeHuman(uuid));
    }

    public void testWriteURI() throws Exception {

        URI uri = new URI("http://www.foo.com");

        assertEquals(scalar("\"~rhttp://www.foo.com\""), writeHuman(uri));
    }

    public void testWriteBinary() throws Exception {

        byte[] bytes = "foobarbaz".getBytes();
        byte[] encodedBytes = Base64.encodeBase64(bytes);

        assertEquals(scalar("\"~b" + new String(encodedBytes) + "\""), writeHuman(bytes));
    }

    public void testWriteSymbol() throws Exception {

        assertEquals(scalar("\"~$foo\""), writeHuman(TransitFactory.symbol("foo")));
    }

    public void testWriteArray() throws Exception {

        List l = new ArrayList();
        l.add(1);
        l.add(2);
        l.add(3);

        assertEquals("[1,2,3]", writeHuman(l));
        assertEquals("[1,2,3]", writeMachine(l));
    }

    public void testWritePrimitiveArrays() throws Exception {

        int[] ints = {1,2};
        assertEquals("{\"~#ints\":[1,2]}", writeHuman(ints));
        long[] longs = {1L,2L};
        assertEquals("{\"~#longs\":[1,2]}", writeHuman(longs));
        float[] floats = {1.5f,2.78f};
        assertEquals("{\"~#floats\":[1.5,2.78]}", writeHuman(floats));
        boolean[] bools = {true,false};
        assertEquals("{\"~#bools\":[true,false]}", writeHuman(bools));
        double[] doubles = {1.654d,2.8765d};
        assertEquals("{\"~#doubles\":[1.654,2.8765]}", writeHuman(doubles));
        short[] shorts = {1,2};
        assertEquals("{\"~#shorts\":[1,2]}", writeHuman(shorts));
        char[] chars = {53,47};
        assertEquals("{\"~#chars\":[\"~c5\",\"~c/\"]}", writeHuman(chars));
    }

    public void testWriteMap() throws Exception {

        Map m = new HashMap();
        m.put("foo", 1);
        m.put("bar", 2);

        assertEquals("{\"foo\":1,\"bar\":2}", writeHuman(m));
        assertEquals("[\"^ \",\"foo\",1,\"bar\",2]", writeMachine(m));
    }

    public void testWriteEmptyMap() throws Exception {
        Map m = new HashMap();
        assertEquals("{}", writeHuman(m));
        assertEquals("[\"^ \"]", writeMachine(m));
    }

    public void testWriteSet() throws Exception {

        Set s = new HashSet();
        s.add("foo");
        s.add("bar");

        assertEquals("{\"~#set\":[\"foo\",\"bar\"]}", writeHuman(s));
        assertEquals("{\"~#set\":[\"foo\",\"bar\"]}", writeMachine(s));
    }

    public void testWriteEmptySet() throws Exception {

        Set s = new HashSet();
        assertEquals("{\"~#set\":[]}", writeHuman(s));
        assertEquals("{\"~#set\":[]}", writeMachine(s));
    }

    public void testWriteList() throws Exception {

        List l = new LinkedList();
        l.add("foo");
        l.add("bar");

        assertEquals("{\"~#list\":[\"foo\",\"bar\"]}", writeHuman(l));
        assertEquals("{\"~#list\":[\"foo\",\"bar\"]}", writeMachine(l));
    }

    public void testWriteEmptyList() throws Exception {

        List l = new LinkedList();
        assertEquals("{\"~#list\":[]}", writeHuman(l));
        assertEquals("{\"~#list\":[]}", writeMachine(l));
    }

    public void testWriteCharacter() throws Exception {

        assertEquals(scalar("\"~cf\""), writeHuman('f'));
    }

    public void testWriteRatio() throws Exception {

        Ratio r = new Ratio(1, 2);

        assertEquals("{\"~#ratio\":[1,2]}", writeHuman(r));
        assertEquals("{\"~#ratio\":[1,2]}", writeMachine(r));
    }

    public void testWriteCmap() throws Exception {

        Ratio r = new Ratio(1, 2);
        Map m = new HashMap();
        m.put(r, 1);
        assertEquals("{\"~#cmap\":[{\"~#ratio\":[1,2]},1]}", writeHuman(m));
        assertEquals("{\"~#cmap\":[{\"~#ratio\":[1,2]},1]}", writeMachine(m));
    }

    public void testWriteCache() {

        WriteCache wc = new WriteCache(true);
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

    public void testWriteCacheDisabled() {

        WriteCache wc = new WriteCache(false);
        assertEquals("foobar", wc.cacheWrite("foobar", false));
        assertEquals("foobar", wc.cacheWrite("foobar", false));
        assertEquals("foobar", wc.cacheWrite("foobar", true));
        assertEquals("foobar", wc.cacheWrite("foobar", true));
    }

    public void testWriteUnknown() throws Exception {

        List l = new ArrayList();
        l.add("`jfoo");
        assertEquals("[\"~`jfoo\"]", writeHuman(l));
        assertEquals(scalar("\"~`jfoo\""), writeHuman("`jfoo"));
        List l2 = new ArrayList();
        l2.add(1L);
        l2.add(2L);
        assertEquals("{\"~#point\":[1,2]}", writeHuman(TransitFactory.taggedValue("point", l2)));
    }

    public void testUseKeywordAsMapKey() {

        Map m = new HashMap();
        m.put(TransitFactory.keyword("foo"), 1);
        m.put("foo", 2);
        m.put(TransitFactory.keyword("bar"), 3);
        m.put("bar", 4);

        assertEquals(1, m.get(TransitFactory.keyword("!foo".substring(1))));
        assertEquals(2, m.get("!foo".substring(1)));
        assertEquals(3, m.get(TransitFactory.keyword("!bar".substring(1))));
        assertEquals(4, m.get("!bar".substring(1)));
    }

    public void testUseSymbolAsMapKey() {

        Map m = new HashMap();
        m.put(TransitFactory.symbol("foo"), 1);
        m.put("foo", 2);
        m.put(TransitFactory.symbol("bar"), 3);
        m.put("bar", 4);

        assertEquals(1, m.get(TransitFactory.symbol("!foo".substring(1))));
        assertEquals(2, m.get("!foo".substring(1)));
        assertEquals(3, m.get(TransitFactory.symbol("!bar".substring(1))));
        assertEquals(4, m.get("!bar".substring(1)));
    }

    public void testKeywordEquality() {

        String s = "foo";

        Keyword k1 = TransitFactory.keyword("foo");
        Keyword k2 = TransitFactory.keyword("!foo".substring(1));
        Keyword k3 = TransitFactory.keyword("bar");

        assertEquals(k1, k2);
        assertEquals(k2, k1);
        assertFalse(k1.equals(k3));
        assertFalse(k3.equals(k1));
        assertFalse(s.equals(k1));
        assertFalse(k1.equals(s));
    }

    public void testKeywordHashCode() {

        String s = "foo";
        Keyword k1 = TransitFactory.keyword("foo");
        Keyword k2 = TransitFactory.keyword("!foo".substring(1));
        Keyword k3 = TransitFactory.keyword("bar");
        Symbol symbol = TransitFactory.symbol("bar");

        assertEquals(k1.hashCode(), k2.hashCode());
        assertFalse(k3.hashCode() == k1.hashCode());
        assertFalse(symbol.hashCode() == k1.hashCode());
        assertFalse(s.hashCode() == k1.hashCode());
    }

    public void testKeywordComparator() {

        List<Keyword> l = new ArrayList<Keyword>();
        l.add(TransitFactory.keyword("bbb"));
        l.add(TransitFactory.keyword("ccc"));
        l.add(TransitFactory.keyword("abc"));
        l.add(TransitFactory.keyword("dab"));

        Collections.sort(l);

        assertEquals("abc", l.get(0).toString());
        assertEquals("bbb", l.get(1).toString());
        assertEquals("ccc", l.get(2).toString());
        assertEquals("dab", l.get(3).toString());
    }

    public void testSymbolEquality() {

        String s = "foo";

        Symbol sym1 = TransitFactory.symbol("foo");
        Symbol sym2 = TransitFactory.symbol("!foo".substring(1));
        Symbol sym3 = TransitFactory.symbol("bar");

        assertEquals(sym1, sym2);
        assertEquals(sym2, sym1);
        assertFalse(sym1.equals(sym3));
        assertFalse(sym3.equals(sym1));
        assertFalse(s.equals(sym1));
        assertFalse(sym1.equals(s));
    }

    public void testSymbolHashCode() {

        String s = "foo";
        Symbol sym1 = TransitFactory.symbol("foo");
        Symbol sym2 = TransitFactory.symbol("!foo".substring(1));
        Symbol sym3 = TransitFactory.symbol("bar");
        Keyword symbol = TransitFactory.keyword("bar");

        assertEquals(sym1.hashCode(), sym2.hashCode());
        assertFalse(sym3.hashCode() == sym1.hashCode());
        assertFalse(symbol.hashCode() == sym1.hashCode());
        assertFalse(s.hashCode() == sym1.hashCode());
    }

    public void testSymbolComparator() {

        List<Symbol> l = new ArrayList<Symbol>();
        l.add(TransitFactory.symbol("bbb"));
        l.add(TransitFactory.symbol("ccc"));
        l.add(TransitFactory.symbol("abc"));
        l.add(TransitFactory.symbol("dab"));

        Collections.sort(l);

        assertEquals("abc", l.get(0).toString());
        assertEquals("bbb", l.get(1).toString());
        assertEquals("ccc", l.get(2).toString());
        assertEquals("dab", l.get(3).toString());
    }
}
