package com.cognitect.transit;

import com.cognitect.transit.impl.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.codec.binary.Base64;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TransitTestMP extends TestCase {

    public TransitTestMP(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TransitTestMP.class);
    }

    // Reading

    public Reader readerOf(Object s) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MessagePack msgpack = new MessagePack();
        Packer packer = msgpack.createPacker(out);
        packer.write(s);

        InputStream in = new ByteArrayInputStream(out.toByteArray());
        return Reader.getMsgpackInstance(in, null);
    }

    public void testReadString() throws IOException {

        assertEquals("foo", readerOf("foo").read());
        assertEquals("~foo", readerOf("~~foo").read());
        assertEquals("`foo", readerOf("~`foo").read());
        assertEquals("~#foo", readerOf("~#foo").read());
        assertEquals("^foo", readerOf("~^foo").read());
    }

    public void testReadBoolean() throws IOException {

        assertTrue((Boolean)readerOf("~?t").read());
        assertFalse((Boolean) readerOf("~?f").read());

        Map thing = new HashMap() {{
            put("~?t", 1);
            put("~?f", 2);
        }};

        Map m = (Map)readerOf(thing).read();
        assertEquals(1L, m.get(true));
        assertEquals(2L, m.get(false));
    }

    public void testReadNull() throws IOException {
        assertNull(readerOf("~_").read());
    }

    public void testReadKeyword() throws IOException {

        Object v = readerOf("~:foo").read();
        assertEquals("foo", v.toString());

        List thing = new ArrayList() {{
            add("~:foo");
            add("^" + (char)33);
            add("^" + (char)33);
        }};

        List v2 = (List)readerOf(thing).read();
        assertEquals("foo", v2.get(0).toString());
        assertEquals("foo", v2.get(1).toString());
        assertEquals("foo", v2.get(2).toString());

    }

    public void testReadInteger() throws IOException {

        Reader r = readerOf("~i42");
        assertEquals(42L, r.read());

        r = readerOf("~i4256768765123454321897654321234567");
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
            put("~#t", new ArrayList() {{
                add(t);
            }});
        }};

        // TODO assertEquals(t, ((Date)readerOf(thing).read()).getTime());
    }

    public void testReadUUID() throws IOException {

        UUID uuid = UUID.randomUUID();
        final long hi64 = uuid.getMostSignificantBits();
        final long lo64 = uuid.getLeastSignificantBits();

        assertEquals(0, uuid.compareTo((UUID)readerOf("~u" + uuid.toString()).read()));

        Map thing = new HashMap() {{
            put("~#u", new ArrayList() {{
                add(hi64);
                add(lo64);
            }});
        }};

        // TODO assertEquals(0, uuid.compareTo((UUID)readerOf(thing).read()));
    }

    public void testReadURI() throws URISyntaxException, IOException {

        URI uri = new URI("http://www.foo.com");

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
        byte[] decoded = (byte[])readerOf("~b" + new String(encodedBytes)).read();

        assertEquals(bytes.length, decoded.length);

        boolean same = true;
        for(int i=0;i<bytes.length;i++) {
            if(bytes[i]!=decoded[i])
                same = false;
        }

        assertTrue(same);
    }

    public void testReadUnknown() throws IOException {

        assertEquals("`jfoo", readerOf("~jfoo").read());

        final List l = Arrays.asList(1L, 2L);

        Map thing = new HashMap() {{
            put("~#point", l);
        }};

        // TODO assertEquals(new TaggedValue("point", l), readerOf(thing).read());
    }

    public void testReadArray() throws IOException {
        long[] thing = {1L, 2L, 3L};

        List l = (List)readerOf(thing).read();

        assertTrue(l instanceof ArrayList);
        assertEquals(3, l.size());

        assertEquals(1L, l.get(0));
        assertEquals(2L, l.get(1));
        assertEquals(3L, l.get(2));
    }

    public void testReadPrimitiveArrays() throws IOException {

        Map thing = new HashMap();

        int[] ints = {1,2};
        thing.put("~#ints", ints);

/* TODO
        int[] ai = (int[])readerOf(thing).read();
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
*/
    }

    public void testReadArrayWithNested() throws IOException {

        Date d = new Date();
        final String t = JsonParser.dateTimeFormat.format(d);

        List thing = new ArrayList() {{
            add("~:foo");
            add("~t" + t);
            add("~?t");
        }};

        List l = (List)readerOf(thing).read();

        assertEquals(3, l.size());

        assertEquals("foo", l.get(0).toString());
        assertEquals(d.getTime(), ((Date)l.get(1)).getTime());
        assertTrue((Boolean) l.get(2));
    }

    
}
