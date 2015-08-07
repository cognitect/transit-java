// Copyright 2014 Cognitect. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS-IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.cognitect.transit;

import com.cognitect.transit.impl.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
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

    public Reader reader(String s)  {
        try {
            InputStream in = new ByteArrayInputStream(s.getBytes());
            return TransitFactory.reader(TransitFactory.Format.JSON, in);
        } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public Reader reader(String s, Map<String, ReadHandler<?, ?>> customHandlers) {
        try {
            InputStream in = new ByteArrayInputStream(s.getBytes());
            return TransitFactory.reader(TransitFactory.Format.JSON, in, customHandlers);
        } catch (Throwable e) { throw new RuntimeException(e); }

    }

    public void testReadString() throws IOException {

        assertEquals("foo", reader("\"foo\"").read());
        assertEquals("~foo", reader("\"~~foo\"").read());
        assertEquals("`foo", reader("\"~`foo\"").read());
        assertEquals("foo", ((Tag)reader("\"~#foo\"").read()).getValue());
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
        assertEquals(":foo", v.toString());

        List v2 = (List)reader("[\"~:foo\",\"^"+(char)WriteCache.BASE_CHAR_IDX+"\",\"^"+(char)WriteCache.BASE_CHAR_IDX+"\"]").read();
        assertEquals(":foo", v2.get(0).toString());
        assertEquals(":foo", v2.get(1).toString());
        assertEquals(":foo", v2.get(2).toString());
    }

    public void testReadInteger() throws IOException {

        Reader r = reader("\"~i42\"");
        assertEquals(42L, r.read());
        r = reader("\"~n4256768765123454321897654321234567\"");
        assertEquals(0, (new BigInteger("4256768765123454321897654321234567")).compareTo(
                          (BigInteger)r.read()));
    }

    public void testReadDouble() throws IOException {

        assertEquals(new Double("42.5"), reader("\"~d42.5\"").read());
    }

    public void testReadSpecialNumbers() throws IOException {
        assertEquals(Double.NaN, reader("\"~zNaN\"").read());
        assertEquals(Double.POSITIVE_INFINITY, reader("\"~zINF\"").read());
        assertEquals(Double.NEGATIVE_INFINITY, reader("\"~z-INF\"").read());
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

    public void testReadURI() throws IOException {

        URI uri = TransitFactory.uri("http://www.foo.com");

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

        assertEquals(TransitFactory.taggedValue("j", "foo"), reader("\"~jfoo\"").read());
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

    public void testReadArrayWithNested() throws IOException {

        Date d = new Date();
        String t = JsonParser.dateTimeFormat.format(d);

        List l = (List)reader("[\"~:foo\", \"~t" + t + "\", \"~?t\"]").read();

        assertEquals(3, l.size());

        assertEquals(":foo", l.get(0).toString());
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

        assertEquals(":foo", m.get("a").toString());
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

        Ratio r = (Ratio)reader("{\"~#ratio\": [\"~n1\",\"~n2\"]}").read();

        assertEquals(BigInteger.valueOf(1), r.getNumerator());
        assertEquals(BigInteger.valueOf(2), r.getDenominator());
        assertEquals(0.5d, r.getValue().doubleValue(), 0.01d);
    }

    public void testReadCmap() throws IOException {

        Map m = reader("{\"~#cmap\": [{\"~#ratio\":[\"~n1\",\"~n2\"]},1,{\"~#list\":[1,2,3]},2]}").read();

        assertEquals(2, m.size());

        Iterator<Map.Entry> i = m.entrySet().iterator();
        while(i.hasNext()) {
            Map.Entry e = i.next();
            if((Long)e.getValue() == 1L) {
                Ratio r = (Ratio)e.getKey();
                assertEquals(BigInteger.valueOf(1), r.getNumerator());
                assertEquals(BigInteger.valueOf(2), r.getDenominator());
            }
            else if((Long)e.getValue() == 2L) {
                List l = (List)e.getKey();
                assertEquals(1L, l.get(0));
                assertEquals(2L, l.get(1));
                assertEquals(3L, l.get(2));
            }
        }
    }

    public void testReadSetTagAsString() throws IOException {
        Object o = reader("{\"~~#set\": [1, 2, 3]}").read();
        assertFalse(o instanceof Set);
        assertTrue(o instanceof Map);
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
        assertEquals("~:foo", rc.cacheRead("^" + (char) WriteCache.BASE_CHAR_IDX, false));
        assertEquals("~$bar", rc.cacheRead("~$bar", false));
        assertEquals("~$bar", rc.cacheRead("^" + (char)(WriteCache.BASE_CHAR_IDX + 1), false));
        assertEquals("~#baz", rc.cacheRead("~#baz", false));
        assertEquals("~#baz", rc.cacheRead("^" + (char) (WriteCache.BASE_CHAR_IDX + 2), false));
        assertEquals("foobar", rc.cacheRead("foobar", false));
        assertEquals("foobar", rc.cacheRead("foobar", false));
        assertEquals("foobar", rc.cacheRead("foobar", true));
        assertEquals("foobar", rc.cacheRead("^" + (char) (WriteCache.BASE_CHAR_IDX + 3), true));
        assertEquals("abc", rc.cacheRead("abc", false));
        assertEquals("abc", rc.cacheRead("abc", false));
        assertEquals("abc", rc.cacheRead("abc", true));
        assertEquals("abc", rc.cacheRead("abc", true));
    }

    // Writing

    public String write(Object o, TransitFactory.Format format, Map<Class, WriteHandler<?, ?>> customHandlers) {
        try {
            OutputStream out = new ByteArrayOutputStream();
            Writer w = TransitFactory.writer(format, out, customHandlers);
            w.write(o);
            return out.toString();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String write(Object o, TransitFactory.Format format) {
        return write(o, format, null);
    }

    public String writeJsonVerbose(Object o) {
        try {
            return write(o, TransitFactory.Format.JSON_VERBOSE);
        } catch (Throwable e) { throw new RuntimeException(e); }
    }

    public String writeJson(Object o) {
        try {
            return write(o, TransitFactory.Format.JSON);
        } catch (Throwable e) { throw new RuntimeException(e); }
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
        Writer w = TransitFactory.writer(TransitFactory.Format.JSON_VERBOSE, out);
        w.write(inObject);
        String s = out.toString();
        InputStream in = new ByteArrayInputStream(s.getBytes());
        Reader reader = TransitFactory.reader(TransitFactory.Format.JSON, in);
        Object outObject = reader.read();

        assertTrue(isEqual(inObject, outObject));
    }

    public String scalar(String value) {
        return "[\"~#'\","+value+"]";
    }

    public String scalarVerbose(String value) {
        return "{\"~#'\":"+value+"}";
    }

    public void testWriteNull() throws Exception {

        assertEquals(scalarVerbose("null"), writeJsonVerbose(null));
        assertEquals(scalar("null"), writeJson(null));
    }

    public void testWriteKeyword() throws Exception {

        assertEquals(scalarVerbose("\"~:foo\""), writeJsonVerbose(TransitFactory.keyword("foo")));
        assertEquals(scalar("\"~:foo\""), writeJson(TransitFactory.keyword("foo")));

        List l = new ArrayList();
        l.add(TransitFactory.keyword("foo"));
        l.add(TransitFactory.keyword("foo"));
        l.add(TransitFactory.keyword("foo"));
        assertEquals("[\"~:foo\",\"~:foo\",\"~:foo\"]", writeJsonVerbose(l));
        assertEquals("[\"~:foo\",\"^0\",\"^0\"]", writeJson(l));
    }

    public void testWriteString() throws Exception {

        assertEquals(scalarVerbose("\"foo\""), writeJsonVerbose("foo"));
        assertEquals(scalar("\"foo\""), writeJson("foo"));
        assertEquals(scalarVerbose("\"~~foo\""), writeJsonVerbose("~foo"));
        assertEquals(scalar("\"~~foo\""), writeJson("~foo"));
    }

    public void testWriteBoolean() throws Exception {

        assertEquals(scalarVerbose("true"), writeJsonVerbose(true));
        assertEquals(scalar("true"), writeJson(true));
        assertEquals(scalar("false"), writeJson(false));

        Map m = new HashMap();
        m.put(true, 1);
        assertEquals("{\"~?t\":1}", writeJsonVerbose(m));
        assertEquals("[\"^ \",\"~?t\",1]", writeJson(m));
        Map m2 = new HashMap();
        m2.put(false, 1);
        assertEquals("{\"~?f\":1}", writeJsonVerbose(m2));
        assertEquals("[\"^ \",\"~?f\",1]", writeJson(m2));
    }

    public void testWriteInteger() throws Exception {

        assertEquals(scalarVerbose("42"), writeJsonVerbose(42));
        assertEquals(scalarVerbose("42"), writeJsonVerbose(42L));
        assertEquals(scalarVerbose("42"), writeJsonVerbose(new Byte("42")));
        assertEquals(scalarVerbose("42"), writeJsonVerbose(new Short("42")));
        assertEquals(scalarVerbose("42"), writeJsonVerbose(new Integer("42")));
        assertEquals(scalarVerbose("42"), writeJsonVerbose(new Long("42")));
        assertEquals(scalarVerbose("\"~n42\""), writeJsonVerbose(new BigInteger("42")));
        assertEquals(scalarVerbose("\"~n4256768765123454321897654321234567\""), writeJsonVerbose(new BigInteger("4256768765123454321897654321234567")));
    }

    public void testWriteIntegerAtJSONBoundaries() throws Exception {

        assertEquals(scalarVerbose("9007199254740991"),       writeJsonVerbose((long) Math.pow(2, 53) - 1));
        assertEquals(scalarVerbose("\"~i9007199254740992\""), writeJsonVerbose((long) Math.pow(2, 53)));

        assertEquals(scalarVerbose("-9007199254740991"),       writeJsonVerbose(1 - (long) Math.pow(2, 53)));
        assertEquals(scalarVerbose("\"~i-9007199254740992\""), writeJsonVerbose(0 - (long) Math.pow(2, 53)));
    }

    public void testWriteFloatDouble() throws Exception {

        assertEquals(scalarVerbose("42.5"), writeJsonVerbose(42.5));
        assertEquals(scalarVerbose("42.5"), writeJsonVerbose(new Float("42.5")));
        assertEquals(scalarVerbose("42.5"), writeJsonVerbose(new Double("42.5")));
    }

    public void testSpecialNumbers() throws Exception {
        assertEquals(scalar("\"~zNaN\""), writeJson(Double.NaN));
        assertEquals(scalar("\"~zINF\""), writeJson(Double.POSITIVE_INFINITY));
        assertEquals(scalar("\"~z-INF\""), writeJson(Double.NEGATIVE_INFINITY));

        assertEquals(scalar("\"~zNaN\""), writeJson(Float.NaN));
        assertEquals(scalar("\"~zINF\""), writeJson(Float.POSITIVE_INFINITY));
        assertEquals(scalar("\"~z-INF\""), writeJson(Float.NEGATIVE_INFINITY));

        assertEquals(scalarVerbose("\"~zNaN\""), writeJsonVerbose(Double.NaN));
        assertEquals(scalarVerbose("\"~zINF\""), writeJsonVerbose(Double.POSITIVE_INFINITY));
        assertEquals(scalarVerbose("\"~z-INF\""), writeJsonVerbose(Double.NEGATIVE_INFINITY));

        assertEquals(scalarVerbose("\"~zNaN\""), writeJsonVerbose(Float.NaN));
        assertEquals(scalarVerbose("\"~zINF\""), writeJsonVerbose(Float.POSITIVE_INFINITY));
        assertEquals(scalarVerbose("\"~z-INF\""), writeJsonVerbose(Float.NEGATIVE_INFINITY));
    }

    public void testWriteBigDecimal() throws Exception {

        assertEquals(scalarVerbose("\"~f42.5\""), writeJsonVerbose(new BigDecimal("42.5")));
    }

    public void testWriteTime() throws Exception {

        Date d = new Date();
        String dateString = AbstractParser.dateTimeFormat.format(d);
        long dateLong = d.getTime();
        assertEquals(scalarVerbose("\"~t" + dateString + "\""), writeJsonVerbose(d));
        assertEquals(scalar("\"~m" + dateLong + "\""), writeJson(d));
    }

    public void testWriteUUID() throws Exception {

        UUID uuid = UUID.randomUUID();

        assertEquals(scalarVerbose("\"~u" + uuid.toString() + "\""), writeJsonVerbose(uuid));
    }

    public void testWriteURI() throws Exception {

        URI uri = TransitFactory.uri("http://www.foo.com");

        assertEquals(scalarVerbose("\"~rhttp://www.foo.com\""), writeJsonVerbose(uri));
    }

    public void testWriteBinary() throws Exception {

        byte[] bytes = "foobarbaz".getBytes();
        byte[] encodedBytes = Base64.encodeBase64(bytes);

        assertEquals(scalarVerbose("\"~b" + new String(encodedBytes) + "\""), writeJsonVerbose(bytes));
    }

    public void testWriteSymbol() throws Exception {

        assertEquals(scalarVerbose("\"~$foo\""), writeJsonVerbose(TransitFactory.symbol("foo")));
    }

    public void testWriteArray() throws Exception {

        List l = new ArrayList();
        l.add(1);
        l.add(2);
        l.add(3);

        assertEquals("[1,2,3]", writeJsonVerbose(l));
        assertEquals("[1,2,3]", writeJson(l));
    }

    public void testWritePrimitiveArrays() throws Exception {

        int[] ints = {1,2};
        assertEquals("[1,2]", writeJsonVerbose(ints));
        long[] longs = {1L,2L};
        assertEquals("[1,2]", writeJsonVerbose(longs));
        float[] floats = {1.5f,2.78f};
        assertEquals("[1.5,2.78]", writeJsonVerbose(floats));
        boolean[] bools = {true,false};
        assertEquals("[true,false]", writeJsonVerbose(bools));
        double[] doubles = {1.654d,2.8765d};
        assertEquals("[1.654,2.8765]", writeJsonVerbose(doubles));
        short[] shorts = {1,2};
        assertEquals("[1,2]", writeJsonVerbose(shorts));
        char[] chars = {53,47};
        assertEquals("[\"~c5\",\"~c/\"]", writeJsonVerbose(chars));
    }

    public void testWriteMap() throws Exception {

        Map m = new LinkedHashMap();
        m.put("foo", 1);
        m.put("bar", 2);

        assertEquals("{\"foo\":1,\"bar\":2}", writeJsonVerbose(m));
        assertEquals("[\"^ \",\"foo\",1,\"bar\",2]", writeJson(m));
    }

    public void testWriteEmptyMap() throws Exception {
        Map m = new HashMap();
        assertEquals("{}", writeJsonVerbose(m));
        assertEquals("[\"^ \"]", writeJson(m));
    }

    public void testWriteSet() throws Exception {

        Set s = new LinkedHashSet();
        s.add("foo");
        s.add("bar");

        assertEquals("{\"~#set\":[\"foo\",\"bar\"]}", writeJsonVerbose(s));
        assertEquals("[\"~#set\",[\"foo\",\"bar\"]]", writeJson(s));
    }

    public void testWriteEmptySet() throws Exception {

        Set s = new HashSet();
        assertEquals("{\"~#set\":[]}", writeJsonVerbose(s));
        assertEquals("[\"~#set\",[]]", writeJson(s));
    }

    public void testWriteList() throws Exception {

        List l = new LinkedList();
        l.add("foo");
        l.add("bar");

        assertEquals("{\"~#list\":[\"foo\",\"bar\"]}", writeJsonVerbose(l));
        assertEquals("[\"~#list\",[\"foo\",\"bar\"]]", writeJson(l));
    }

    public void testWriteEmptyList() throws Exception {

        List l = new LinkedList();
        assertEquals("{\"~#list\":[]}", writeJsonVerbose(l));
        assertEquals("[\"~#list\",[]]", writeJson(l));
    }

    public void testWriteCharacter() throws Exception {

        assertEquals(scalarVerbose("\"~cf\""), writeJsonVerbose('f'));
    }

    public void testWriteRatio() throws Exception {

        Ratio r = new RatioImpl(BigInteger.valueOf(1), BigInteger.valueOf(2));

        assertEquals("{\"~#ratio\":[\"~n1\",\"~n2\"]}", writeJsonVerbose(r));
        assertEquals("[\"~#ratio\",[\"~n1\",\"~n2\"]]", writeJson(r));
    }

    public void testWriteCmap() throws Exception {

        Ratio r = new RatioImpl(BigInteger.valueOf(1), BigInteger.valueOf(2));
        Map m = new HashMap();
        m.put(r, 1);
        assertEquals("{\"~#cmap\":[{\"~#ratio\":[\"~n1\",\"~n2\"]},1]}", writeJsonVerbose(m));
        assertEquals("[\"~#cmap\",[[\"~#ratio\",[\"~n1\",\"~n2\"]],1]]", writeJson(m));
    }

    public void testWriteCache() {

        WriteCache wc = new WriteCache(true);
        assertEquals("~:foo", wc.cacheWrite("~:foo", false));
        assertEquals("^" + (char)WriteCache.BASE_CHAR_IDX, wc.cacheWrite("~:foo", false));
        assertEquals("~$bar", wc.cacheWrite("~$bar", false));
        assertEquals("^" + (char)(WriteCache.BASE_CHAR_IDX + 1), wc.cacheWrite("~$bar", false));
        assertEquals("~#baz", wc.cacheWrite("~#baz", false));
        assertEquals("^" + (char)(WriteCache.BASE_CHAR_IDX + 2), wc.cacheWrite("~#baz", false));
        assertEquals("foobar", wc.cacheWrite("foobar", false));
        assertEquals("foobar", wc.cacheWrite("foobar", false));
        assertEquals("foobar", wc.cacheWrite("foobar", true));
        assertEquals("^" + (char)(WriteCache.BASE_CHAR_IDX + 3), wc.cacheWrite("foobar", true));
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
        assertEquals("[\"~`jfoo\"]", writeJsonVerbose(l));
        assertEquals(scalarVerbose("\"~`jfoo\""), writeJsonVerbose("`jfoo"));
        List l2 = new ArrayList();
        l2.add(1L);
        l2.add(2L);
        assertEquals("{\"~#point\":[1,2]}", writeJsonVerbose(TransitFactory.taggedValue("point", l2)));
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

        assertEquals(":abc", l.get(0).toString());
        assertEquals(":bbb", l.get(1).toString());
        assertEquals(":ccc", l.get(2).toString());
        assertEquals(":dab", l.get(3).toString());
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

    public void testMapWithEscapedKey() {
        Map m1 = new HashMap();
        m1.put("~Gfoo", 20L);
        String str = writeJson(m1);
        Map m2 = (Map) reader(str).read();
        assertTrue(m2.keySet().contains("~Gfoo"));
        assertTrue(m2.get("~Gfoo").equals(20L));
    }

    public void testLink() {
        Link l1 = TransitFactory.link("http://google.com", "search", "name", "link", "prompt");
        String str = writeJson(l1);
        Link l2 = (Link) reader(str).read();
        assertEquals("http://google.com", l2.getHref().getValue());
        assertEquals("search", l2.getRel());
        assertEquals("name", l2.getName());
        assertEquals("link", l2.getRender());
        assertEquals("prompt", l2.getPrompt());
    }

    public void testEmptySet() {
        String str = writeJson(new HashSet());
        assertTrue(reader(str).read() instanceof Set);
    }

    public void test() {
        Writer<Object> w = TransitFactory.writer(TransitFactory.Format.JSON, null);
        Writer<Map<String, String>> w2 = TransitFactory.writer(TransitFactory.Format.JSON, null);
        Writer w3 = TransitFactory.writer(TransitFactory.Format.JSON, null);
    }

    public void testPrettyPrint() {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            JsonFactory jf = new JsonFactory();
            JsonGenerator jg = jf.createGenerator(bytes);
            jg.writeString(":db/ident");
            jg.close();
            String s = new String(bytes.toByteArray());
            System.out.println(s);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static class TestListWriteHandler extends AbstractWriteHandler<List<Object>, Object> {

        @Override
        public String tag(List<Object> o) {
            if (o instanceof RandomAccess) // ArrayList, Stack, Vector
                return "array";
            else
                return "list";
        }

        @Override
        public Object rep(List<Object> o) {
            if (o instanceof LinkedList)
                return TransitFactory.taggedValue("array", o);
            else
                return o;
        }
    }

    public void testWriteHandlerCache() {
        Map<Class, WriteHandler<?, ?>> handlers = new HashMap<Class, WriteHandler<?, ?>>();
        handlers.put(java.util.List.class, new TestListWriteHandler());

        for (int i = 0; i < 2; i++) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Writer<Object> w = TransitFactory.writer(TransitFactory.Format.JSON, out, handlers);
        }
    }

    public void testReadHandlerMapWithNoCustomHandlers() {
        assertEquals("foo", reader("\"foo\"", TransitFactory.readHandlerMap(null)).read());
    }

    public void testReadHandlerMapWithCustomHandler() {
        ReadHandler customHandler = new ReadHandler() {
            @Override
            public Object fromRep(Object o) {
                return o.toString() + " (processed)";
            }
        };
        Map<String, ReadHandler<?, ?>> customHandlers = new HashMap<String, ReadHandler<?, ?>>();
        customHandlers.put("thing", customHandler);
        String s = reader("{\"~#thing\":\"stored value\"}", TransitFactory.readHandlerMap(customHandlers)).read();
        assertEquals("stored value (processed)", s);
    }

    private WriteHandler customWriteHandler() {
        return new WriteHandler() {
            @Override
            public String tag(Object o) {
                return "s";
            }

            @Override
            public Object rep(Object o) {
                return o + " (custom)";
            }

            @Override
            public String stringRep(Object o) {
                return null;
            }

            @Override
            public WriteHandler getVerboseHandler() {
                return new WriteHandler() {
                    @Override
                    public String tag(Object o) {
                        return "s";
                    }

                    @Override
                    public Object rep(Object o) {
                        return o + " (verbose custom)";
                    }

                    @Override
                    public String stringRep(Object o) {
                        return null;
                    }

                    @Override
                    public WriteHandler getVerboseHandler() {
                        return null;
                    }
                };
            }
        };
    }

    public void testWriteHandlerMapWithNoCustomHandlers() {
        assertEquals(scalar("37"), write(37, TransitFactory.Format.JSON, TransitFactory.writeHandlerMap(null)));
    }

    public void testWriteHandlerMapWithCustomHandler() {
        WriteHandler customHandler = customWriteHandler();

        Map<Class, WriteHandler<?, ?>> customHandlers = new HashMap<Class, WriteHandler<?, ?>>();
        customHandlers.put(String.class, customHandler);
        String result = write("37", TransitFactory.Format.JSON, TransitFactory.writeHandlerMap(customHandlers));
        assertEquals(scalar("\"37 (custom)\""), result);
    }

    public void testWriteHandlerMapWithCustomHandlerVerbose() {
        WriteHandler customHandler = customWriteHandler();

        Map<Class, WriteHandler<?, ?>> customHandlers = new HashMap<Class, WriteHandler<?, ?>>();
        customHandlers.put(String.class, customHandler);
        WriteHandlerMap writeHandlerMap = new WriteHandlerMap(customHandlers);
        String result = write("37", TransitFactory.Format.JSON_VERBOSE, writeHandlerMap);
        assertEquals(scalarVerbose("\"37 (verbose custom)\""), result);
    }
}
