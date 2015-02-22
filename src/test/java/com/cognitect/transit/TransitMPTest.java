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

import com.cognitect.transit.impl.JsonParser;
import com.cognitect.transit.impl.Tag;
import com.cognitect.transit.impl.WriteCache;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.codec.binary.Base64;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TransitMPTest extends TestCase {

    public TransitMPTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TransitMPTest.class);
    }

    // Reading

    public Reader readerOf(Object... things) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessagePack msgpack = new MessagePack();
        Packer packer = msgpack.createPacker(out);

        for (Object o : things) {
            packer.write(o);
        }

        InputStream in = new ByteArrayInputStream(out.toByteArray());
        return TransitFactory.reader(TransitFactory.Format.MSGPACK, in);

    }

    public void testReadString() throws IOException {

        assertEquals("foo", readerOf("foo").read());
        assertEquals("~foo", readerOf("~~foo").read());
        assertEquals("`foo", readerOf("~`foo").read());
        assertEquals("foo", ((Tag)readerOf("~#foo").read()).getValue());
        assertEquals("^foo", readerOf("~^foo").read());
    }

    public void testReadBoolean() throws IOException {

        assertTrue((Boolean)readerOf("~?t").read());
        assertFalse((Boolean) readerOf("~?f").read());

        Map thing = new HashMap() {{
            put("~?t", 1);
            put("~?f", 2);
        }};

        Map m = readerOf(thing).read();
        assertEquals(1L, m.get(true));
        assertEquals(2L, m.get(false));
    }

    public void testReadNull() throws IOException {
        assertNull(readerOf("~_").read());
    }

    public void testReadKeyword() throws IOException {

        Object v = readerOf("~:foo").read();
        assertEquals(":foo", v.toString());

        List thing = new ArrayList() {{
            add("~:foo");
            add("^" + (char)WriteCache.BASE_CHAR_IDX);
            add("^" + (char)WriteCache.BASE_CHAR_IDX);
        }};

        List v2 = readerOf(thing).read();
        assertEquals(":foo", v2.get(0).toString());
        assertEquals(":foo", v2.get(1).toString());
        assertEquals(":foo", v2.get(2).toString());

    }

    public void testReadInteger() throws IOException {

        Reader r = readerOf("~i42");
        assertEquals(42L, r.read());

        r = readerOf("~n4256768765123454321897654321234567");
        assertEquals(0, (new BigInteger("4256768765123454321897654321234567")).compareTo((BigInteger)r.read()));
    }

    public void testReadDouble() throws IOException {

        assertEquals(new Double("42.5"), readerOf("~d42.5").read());
    }

    public void testReadBigDecimal() throws IOException {

        assertEquals(0, (new BigDecimal("42.5")).compareTo((BigDecimal)readerOf("~f42.5").read()));
    }

    private long readTimeString(String timeString) throws IOException {
        return ((Date)readerOf("~t" + timeString).read()).getTime();
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
        final long t = d.getTime();
        String timeString = JsonParser.dateTimeFormat.format(d);

        assertEquals(t, readTimeString(timeString));

        assertReadsFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        assertReadsFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        assertReadsFormat("yyyy-MM-dd'T'HH:mm:ss.SSS-00:00");

        Map thing = new HashMap() {{
            put("~#m", t);
        }};

        assertEquals(t, ((Date)readerOf(thing).read()).getTime());
    }

    public void testReadUUID() throws IOException {

        UUID uuid = UUID.randomUUID();
        final long hi64 = uuid.getMostSignificantBits();
        final long lo64 = uuid.getLeastSignificantBits();

        assertEquals(0, uuid.compareTo((UUID)readerOf("~u" + uuid.toString()).read()));

        List thing = new ArrayList() {{
            add("~#u");
            add(new ArrayList() {{
                add(hi64);
                add(lo64);
            }});
        }};

        assertEquals(0, uuid.compareTo((UUID)readerOf(thing).read()));
    }

    public void testReadURI() throws URISyntaxException, IOException {

        URI uri = TransitFactory.uri("http://www.foo.com");

        assertEquals(0, uri.compareTo((URI)readerOf("~rhttp://www.foo.com").read()));
    }

    public void testReadSymbol() throws IOException {

        Reader r = readerOf("~$foo");
        Object v = r.read();
        assertEquals("foo", v.toString());
    }

    public void testReadCharacter() throws IOException {

        assertEquals('f', readerOf("~cf").read());
    }

    // Binary data tests

    public void testReadBinary() throws IOException {

        byte[] bytes = "foobarbaz".getBytes();
        byte[] encodedBytes = Base64.encodeBase64(bytes);
        byte[] decoded = readerOf("~b" + new String(encodedBytes)).read();

        assertEquals(bytes.length, decoded.length);

        boolean same = true;
        for(int i=0;i<bytes.length;i++) {
            if(bytes[i]!=decoded[i])
                same = false;
        }

        assertTrue(same);
    }

    public void testReadUnknown() throws IOException {

        assertEquals(TransitFactory.taggedValue("j", "foo"), readerOf("~jfoo").read());

        final List l = Arrays.asList(1L, 2L);

        Map thing = new HashMap() {{
            put("~#point", l);
        }};

        assertEquals(TransitFactory.taggedValue("point", l), readerOf(thing).read());
    }

    public void testReadArray() throws IOException {
        long[] thing = {1L, 2L, 3L};

        List l = readerOf(thing).read();

        assertTrue(l instanceof ArrayList);
        assertEquals(3, l.size());

        assertEquals(1L, l.get(0));
        assertEquals(2L, l.get(1));
        assertEquals(3L, l.get(2));
    }

    public void testReadArrayWithNestedDoubles() throws IOException {
        List thing = new ArrayList() {{
            add(-3.14159);
            add(3.14159);
            add(4.0E11);
            add(2.998E8);
            add(6.626E-34);
        }};

        List l = readerOf(thing).read();

        for(int i = 0; i < l.size(); i++) {
            assertEquals(l.get(i), thing.get(i));
        }
    }

    public void testReadArrayWithNested() throws IOException {

        Date d = new Date();
        final String t = JsonParser.dateTimeFormat.format(d);

        List thing = new ArrayList() {{
            add("~:foo");
            add("~t" + t);
            add("~?t");
        }};

        List l = readerOf(thing).read();

        assertEquals(3, l.size());

        assertEquals(":foo", l.get(0).toString());
        assertEquals(d.getTime(), ((Date)l.get(1)).getTime());
        assertTrue((Boolean) l.get(2));

        final Date da[] = {new Date(-6106017600000l),
                           new Date(0),
                           new Date(946728000000l),
                           new Date(1396909037000l)};

        List dates = new ArrayList() {{
            add("~t" + JsonParser.dateTimeFormat.format(da[0]));
            add("~t" + JsonParser.dateTimeFormat.format(da[1]));
            add("~t" + JsonParser.dateTimeFormat.format(da[2]));
            add("~t" + JsonParser.dateTimeFormat.format(da[3]));
        }};

        l = readerOf(dates).read();

        for (int i = 0; i < l.size(); i++) {
            Date date = (Date)l.get(i);
            assertEquals(date, da[i]);
        }
    }

    public void testReadMap() throws IOException {

        Map thing = new HashMap() {{
            put("a", 2);
            put("b", 4);
        }};

        Map m = readerOf(thing).read();

        assertEquals(2, m.size());

        assertEquals(2L, m.get("a"));
        assertEquals(4L, m.get("b"));
    }

    public void testReadMapWithNested() throws IOException {

        final String uuid = UUID.randomUUID().toString();

        Map thing = new HashMap() {{
            put("a", "~:foo");
            put("b", "~u" + uuid);
        }};

        Map m = readerOf(thing).read();

        assertEquals(2, m.size());

        assertEquals(":foo", m.get("a").toString());
        assertEquals(uuid, m.get("b").toString());
    }

    public void testReadSet() throws IOException {

        final int[] ints = {1,2,3};

        Map thing = new HashMap() {{
            put("~#set", ints);
        }};

        Set s = readerOf(thing).read();

        assertEquals(3, s.size());

        assertTrue(s.contains(1L));
        assertTrue(s.contains(2L));
        assertTrue(s.contains(3L));
    }

    public void testReadList() throws IOException {
        final int[] ints = {1,2,3};

        Map thing = new HashMap() {{
            put("~#list", ints);
        }};

        List l = readerOf(thing).read();

        assertTrue(l instanceof LinkedList);
        assertEquals(3, l.size());

        assertEquals(1L, l.get(0));
        assertEquals(2L, l.get(1));
        assertEquals(3L, l.get(2));
    }

    public void testReadRatio() throws IOException {
        final String[] ratioRep = {"~n1", "~n2"};

        Map thing = new HashMap() {{
            put("~#ratio", ratioRep);
        }};

        Ratio r = readerOf(thing).read();

        assertEquals(BigInteger.valueOf(1), r.getNumerator());
        assertEquals(BigInteger.valueOf(2), r.getDenominator());
        assertEquals(0.5d, r.getValue().doubleValue(), 0.01d);
    }

    public void testReadCmap() throws IOException {
        final String[] ratioRep = {"~n1", "~n2"};
        final int[] mints = {1,2,3};

        final Map ratio = new HashMap() {{
            put("~#ratio", ratioRep);
        }};

        final Map list = new HashMap() {{
            put("~#list", mints);
        }};

        final List things = new ArrayList() {{
            add(ratio);
            add(1);
            add(list);
            add(2);
        }};

        final Map thing = new HashMap() {{
            put("~#cmap", things);
        }};

        Map m = readerOf(thing).read();

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

    public void testReadMany() throws IOException {

        Reader r = readerOf(true, null, false, "foo", 42.2, 42);
        assertTrue((Boolean)r.read());
        assertNull(r.read());
        assertFalse((Boolean) r.read());
        assertEquals("foo", r.read());
        assertEquals(42.2, r.read());
        assertEquals(42L, r.read());
    }

    public void testWriteReadTime() throws Exception {

        final Date da[] = {new Date(-6106017600000l),
                new Date(0),
                new Date(946728000000l),
                new Date(1396909037000l)};

        List l = Arrays.asList(da);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Writer w = TransitFactory.writer(TransitFactory.Format.MSGPACK, out);
        w.write(l);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        Reader r = TransitFactory.reader(TransitFactory.Format.MSGPACK, in);
        Object o = r.read();
    }

    public void testWriteReadSpecialNumbers() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Writer w = TransitFactory.writer(TransitFactory.Format.MSGPACK, out);
        w.write(Double.NaN);
        w.write(Float.NaN);
        w.write(Double.POSITIVE_INFINITY);
        w.write(Float.POSITIVE_INFINITY);
        w.write(Double.NEGATIVE_INFINITY);
        w.write(Float.NEGATIVE_INFINITY);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        Reader r = TransitFactory.reader(TransitFactory.Format.MSGPACK, in);
        assert((Double)r.read()).isNaN();
        assert((Double)r.read()).isNaN();
        assertEquals(Double.POSITIVE_INFINITY, (Double)r.read());
        assertEquals(Double.POSITIVE_INFINITY, (Double)r.read());
        assertEquals(Double.NEGATIVE_INFINITY, (Double)r.read());
        assertEquals(Double.NEGATIVE_INFINITY, (Double)r.read());
    }

}
