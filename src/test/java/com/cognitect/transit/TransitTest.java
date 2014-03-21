package com.cognitect.transit;

import com.cognitect.transit.impl.JsonParser;
import com.cognitect.transit.impl.JsonReader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public Reader testReader(String s) throws IOException {

        InputStream in = new ByteArrayInputStream(s.getBytes());
        return new JsonReader(in);
    }

    public void testReadString() throws IOException {

        assertEquals("foo", testReader("\"foo\"").read());
        assertEquals("~foo", testReader("\"~~foo\"").read());
        assertEquals("`foo", testReader("\"~`foo\"").read());
        assertEquals("~#foo", testReader("\"~#foo\"").read());
        assertEquals("^foo", testReader("\"~^foo\"").read());
    }

    public void testReadBoolean() throws IOException {

        assertTrue((Boolean) testReader("\"~?t\"").read());
        assertFalse((Boolean) testReader("\"~?f\"").read());
    }

    public void testReadNull() throws IOException {

        assertNull(testReader("\"~_\"").read());
    }

    public void testReadKeyword() throws IOException {

        Reader r = testReader("\"~:foo\"");
        Object v = r.read();
        assertEquals("foo", v.toString());
        assertEquals("foo", ((Keyword)v).value);
    }

    public void testReadInteger() throws IOException {

        Reader r = testReader("\"~i42\"");
        assertEquals(42L, r.read());
        r = testReader("\"~i4256768765123454321897654321234567\"");
        assertEquals(0, (new BigInteger("4256768765123454321897654321234567")).compareTo(
                          (BigInteger)r.read()));
    }

    public void testReadDouble() throws IOException {

        assertEquals(new Double("42.5"), testReader("\"~d42.5\"").read());
    }

    public void testReadBigDecimal() throws IOException {

        assertEquals(0, (new BigDecimal("42.5")).compareTo(
                          (BigDecimal)testReader("\"~f42.5\"").read()));
    }

    public void testReadTime() throws IOException {

        Date d = new Date();
        long t = d.getTime();
        String timeString = JsonParser.dateTimeFormat.format(d);

        assertEquals(t, ((Date) testReader("\"~t" + timeString + "\"").read()).getTime());
        // TODO: the case below should be a tagged map
        //assertEquals(t, ((Date)Encode.decode("~t" + d.getTime())).getTime());
    }

    public void testReadUUID() throws IOException {

        UUID uuid = UUID.randomUUID();

        assertEquals(0, uuid.compareTo((UUID)testReader("\"~u" + uuid.toString() + "\"").read()));
        // TODO: add tests for decoding a UUID as a tagged map
    }

    public void testReadURI() throws URISyntaxException, IOException {

        URI uri = new URI("http://www.foo.com");

        assertEquals(0, uri.compareTo((URI)testReader("\"~rhttp://www.foo.com\"").read()));
    }

    public void testReadSymbol() throws IOException {

        Reader r = testReader("\"~$foo\"");
        Object v = r.read();
        assertEquals("foo", v.toString());
        assertEquals("foo", ((Symbol)v).value);
    }

    public void testReadCharacter() throws IOException {

        assertEquals('f', testReader("\"~cf\"").read());
    }

    public void testReadBinary() throws IOException {

        byte[] bytes = "foobarbaz".getBytes();
        byte[] encodedBytes = Base64.encodeBase64(bytes);
        byte[] decoded = (byte[])testReader("\"~b" + new String(encodedBytes) + "\"").read();

        assertEquals(bytes.length, decoded.length);

        boolean same = true;
        for(int i=0;i<bytes.length;i++) {
            if(bytes[i]!=decoded[i])
                same = false;
        }

        assertTrue(same);
    }

    public void testReadUnknown() throws IOException {

        assertEquals("~jfoo", testReader("\"~jfoo\"").read());
    }

    public void testReadArray() throws IOException {

        List l = (List)testReader("[1, 2, 3]").read();

        assertTrue(l instanceof ArrayList);
        assertEquals(3, l.size());

        assertEquals(1L, l.get(0));
        assertEquals(2L, l.get(1));
        assertEquals(3L, l.get(2));
    }

    public void testReadArrayWithNested() throws IOException {

        Date d = new Date();
        String t = JsonParser.dateTimeFormat.format(d);

        List l = (List)testReader("[\"~:foo\", \"~t" + t + "\", \"~?t\"]").read();

        assertEquals(3, l.size());

        assertEquals("foo", ((Keyword)l.get(0)).value);
        assertEquals(d.getTime(), ((Date)l.get(1)).getTime());
        assertTrue((Boolean) l.get(2));
    }

    public void testReadMap() throws IOException {

        Map m = (Map)testReader("{\"a\": 2, \"b\": 4}").read();

        assertEquals(2, m.size());

        assertEquals(2L, m.get("a"));
        assertEquals(4L, m.get("b"));
    }

    public void testReadMapWithNested() throws IOException {

        String uuid = UUID.randomUUID().toString();

        Map m = (Map)testReader("{\"a\": \"~:foo\", \"b\": \"~u" + uuid + "\"}").read();

        assertEquals(2, m.size());

        assertEquals("foo", ((Keyword)m.get("a")).value);
        assertEquals(uuid, m.get("b").toString());
    }

    public void testReadSet() throws IOException {

        Set s = (Set)testReader("{\"~#set\": [1, 2, 3]}").read();

        assertEquals(3, s.size());

        assertTrue(s.contains(1L));
        assertTrue(s.contains(2L));
        assertTrue(s.contains(3L));
    }

    public void testReadList() throws IOException {

        List l = (List)testReader("{\"~#list\": [1, 2, 3]}").read();

        assertTrue(l instanceof LinkedList);
        assertEquals(3, l.size());

        assertEquals(1L, l.get(0));
        assertEquals(2L, l.get(1));
        assertEquals(3L, l.get(2));
    }

    public void testReadUnknownTaggedMap() throws IOException {

        Map m = (Map)testReader("{\"~#foo\": [1, 2, 3]}").read();

        assertTrue(m instanceof HashMap);
        assertEquals(1, m.size());

        List l = (List)m.get("~#foo");

        System.out.println(l);

        assertEquals(3, l.size());

//        assertEquals(1L, l.get(0));
//        assertEquals(2L, l.get(1));
//        assertEquals(3L, l.get(2));
    }

    public void testReadMany() throws IOException {

        Reader r = testReader("true null false \"foo\" 42.2 42");
        assertTrue((Boolean)r.read());
        assertNull(r.read());
        assertFalse((Boolean) r.read());
        assertEquals("foo", r.read());
        assertEquals(42.2, r.read());
        assertEquals(42L, r.read());
    }
}
